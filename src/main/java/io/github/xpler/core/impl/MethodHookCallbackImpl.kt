package io.github.xpler.core.impl

import de.robv.android.xposed.XC_MethodHook
import io.github.xpler.core.log.XplerLog
import io.github.xpler.core.wrapper.OnAfterBlock
import io.github.xpler.core.wrapper.OnBeforeBlock
import io.github.xpler.core.wrapper.OnUnhookBlock

class MethodHookCallbackImpl(
    private val before: OnBeforeBlock?,
    private val after: OnAfterBlock?,
    private val onUnhook: OnUnhookBlock?,
) : XC_MethodHook() {
    var unhook: XC_MethodHook.Unhook? = null

    override fun beforeHookedMethod(param: MethodHookParam) {
        runCatching {
            before?.invoke(param)
            if (after != null) return
            onUnhook?.invoke(unhook!!.hookedMethod, unhook!!.callback)
        }.onFailure {
            val err = "host app method: ${param.method}\n" +
                    "message: ${it.message}\n" +
                    "stack trace: ${it.stackTraceToString()}"
            XplerLog.e(err)
        }
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        runCatching {
            after?.invoke(param)
            onUnhook?.invoke(unhook!!.hookedMethod, unhook!!.callback)
        }.onFailure {
            val err = "host app method: ${param.method}\n" +
                    "message: ${it.message}\n" +
                    "stack trace: ${it.stackTraceToString()}"
            XplerLog.e(err)
        }
    }
}