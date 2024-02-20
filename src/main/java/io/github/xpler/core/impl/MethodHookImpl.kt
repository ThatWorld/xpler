package io.github.xpler.core.impl

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.xpler.core.log.XplerLog
import io.github.xpler.core.wrapper.MethodHook
import io.github.xpler.core.wrapper.OnAfterBlock
import io.github.xpler.core.wrapper.OnBeforeBlock
import io.github.xpler.core.wrapper.OnReplaceBlock
import io.github.xpler.core.wrapper.OnUnhookBlock
import java.lang.reflect.Member
import java.lang.reflect.Modifier

/// 实现类
open class MethodHookImpl(private var method: Member) : MethodHook {
    private var beforeBlock: OnBeforeBlock? = null
    private var afterBlock: OnAfterBlock? = null
    private var replaceBlock: OnReplaceBlock? = null

    private var unhookMap: MutableMap<Member, XC_MethodHook.Unhook> = mutableMapOf()
    private var unHookBlock: OnUnhookBlock? = null

    constructor(clazz: Class<*>, methodName: String, vararg argsTypes: Any) :
            this(XposedHelpers.findMethodExact(clazz, methodName, *argsTypes))


    override fun onBefore(block: OnBeforeBlock) {
        this.beforeBlock = block
    }


    override fun onAfter(block: OnAfterBlock) {
        this.afterBlock = block
    }


    override fun onReplace(block: OnReplaceBlock) {
        this.replaceBlock = block
    }


    override fun onUnhook(block: OnUnhookBlock) {
        this.unHookBlock = block
    }

    // 开启hook, 统一执行Hook逻辑
    fun startHook() {
        // 跳过抽象方法
        if (Modifier.isAbstract(method.modifiers)) {
            XplerLog.e(IllegalArgumentException("Cannot hook abstract method: $method"))
            return
        }

        if (replaceBlock != null) {
            val unhook = XposedBridge.hookMethod(method, object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    runCatching {
                        val invoke = replaceBlock!!.invoke(param)
                        maybeUnhook(param.method)
                        return invoke
                    }.onFailure {
                        val err = "host app method: ${param.method}\n" +
                                "message: ${it.message}\n" +
                                "stack trace: ${it.stackTraceToString()}"
                        XplerLog.e(err)
                    }
                    return param.resultOrThrowable
                }
            })
            unhookMap[method] = unhook
        } else {
            val unhook = XposedBridge.hookMethod(method, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    runCatching {
                        beforeBlock?.invoke(param)
                        if (afterBlock != null) return
                        maybeUnhook(param.method)
                    }.onFailure {
                        val err = "host app method: ${param.method}\n" +
                                "message: ${it.message}\n" +
                                "stack trace: ${it.stackTraceToString()}"
                        XplerLog.e(err)
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    runCatching {
                        afterBlock?.invoke(param)
                        maybeUnhook(param.method)
                    }.onFailure {
                        val err = "host app method: ${param.method}\n" +
                                "message: ${it.message}\n" +
                                "stack trace: ${it.stackTraceToString()}"
                        XplerLog.e(err)
                    }
                }
            })
            unhookMap[method] = unhook
        }
    }

    // 解开hook
    fun maybeUnhook(method: Member) {
        if (unHookBlock == null) return
        if (!unhookMap.containsKey(method)) return

        val unhook = unhookMap[method]!!
        unHookBlock!!.invoke(unhook.hookedMethod, unhook.callback)
        unhook.unhook()
        unhookMap.remove(method)
    }
}