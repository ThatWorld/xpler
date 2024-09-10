package io.github.xpler.core.callback

import de.robv.android.xposed.XC_MethodHook
import io.github.xpler.core.XplerLog
import io.github.xpler.core.proxy.MethodParam
import io.github.xpler.core.proxy.MethodUnhook
import io.github.xpler.core.hook.OnAfterBlock
import io.github.xpler.core.hook.OnBeforeBlock
import io.github.xpler.core.hook.OnUnhookBlock

class MethodHookCallbackImpl(
    private val before: OnBeforeBlock?,
    private val after: OnAfterBlock?,
    private val onUnhook: OnUnhookBlock?,
) : XC_MethodHook() {
    var unhook: XC_MethodHook.Unhook? = null

    private val unhookConstruct = MethodUnhook::class.java.getDeclaredConstructor(XC_MethodHook.Unhook::class.java)
        .apply {
            isAccessible = true
        }

    private val paramConstructor = MethodParam::class.java.getDeclaredConstructor(MethodHookParam::class.java)
        .apply {
            isAccessible = true
        }

    override fun beforeHookedMethod(param: MethodHookParam) {
        runCatching {
            before?.invoke(paramConstructor.newInstance(param))
            if (after != null) return
            onUnhook?.invoke(unhookConstruct.newInstance(unhook))
        }.onFailure {
            XplerLog.e(
                "host app method: ${param.method}\n" +
                        "message: ${it.message}\n" +
                        "stack trace: ${it.stackTraceToString()}"
            )
        }
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        runCatching {
            after?.invoke(paramConstructor.newInstance(param))
            onUnhook?.invoke(unhookConstruct.newInstance(unhook))
        }.onFailure {
            XplerLog.e(
                "host app method: ${param.method}\n" +
                        "message: ${it.message}\n" +
                        "stack trace: ${it.stackTraceToString()}"
            )
        }
    }
}