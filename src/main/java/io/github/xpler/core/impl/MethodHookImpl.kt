package io.github.xpler.core.impl

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
            val impl = MethodReplacementCallbackImpl(replaceBlock, unHookBlock)
            impl.unhook = XposedBridge.hookMethod(method, impl)
        } else {
            val impl = MethodHookCallbackImpl(beforeBlock, afterBlock, unHookBlock)
            impl.unhook = XposedBridge.hookMethod(method, impl)
        }
    }
}