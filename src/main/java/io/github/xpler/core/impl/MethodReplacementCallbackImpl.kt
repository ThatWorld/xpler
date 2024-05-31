package io.github.xpler.core.impl

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import io.github.xpler.core.log.XplerLog
import io.github.xpler.core.wrapper.OnReplaceBlock
import io.github.xpler.core.wrapper.OnUnhookBlock

class MethodReplacementCallbackImpl(
    private val onReplace: OnReplaceBlock?,
    private val onUnhook: OnUnhookBlock?,
) : XC_MethodReplacement() {
    var unhook: XC_MethodHook.Unhook? = null

    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        runCatching {
            val invoke = onReplace!!.invoke(param)
            onUnhook?.invoke(unhook!!.hookedMethod, unhook!!.callback)
            return invoke
        }.onFailure {
            val err = "host app method: ${param.method}\n" +
                    "message: ${it.message}\n" +
                    "stack trace: ${it.stackTraceToString()}"
            XplerLog.e(err)
        }

        return param.resultOrThrowable
    }
}