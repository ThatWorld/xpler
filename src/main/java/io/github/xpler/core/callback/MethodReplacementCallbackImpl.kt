package io.github.xpler.core.callback

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import io.github.xpler.core.XplerLog
import io.github.xpler.core.proxy.MethodParam
import io.github.xpler.core.proxy.MethodUnhook
import io.github.xpler.core.hook.OnReplaceBlock
import io.github.xpler.core.hook.OnUnhookBlock

class MethodReplacementCallbackImpl(
    private val onReplace: OnReplaceBlock?,
    private val onUnhook: OnUnhookBlock?,
) : XC_MethodReplacement() {
    var unhook: XC_MethodHook.Unhook? = null

    private val unhookConstruct = MethodUnhook::class.java.getDeclaredConstructor(XC_MethodHook.Unhook::class.java)
        .apply {
            isAccessible = true
        }

    private val constructor = MethodParam::class.java.getDeclaredConstructor(MethodHookParam::class.java)
        .apply {
            isAccessible = true
        }

    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        runCatching {
            val invoke = onReplace!!.invoke(constructor.newInstance(param))
            onUnhook?.invoke(unhookConstruct.newInstance(unhook))
            return invoke
        }.onFailure {
            XplerLog.e(
                "host app method: ${param.method}\n" +
                        "message: ${it.message}\n" +
                        "stack trace: ${it.stackTraceToString()}"
            )
        }

        return param.resultOrThrowable
    }
}