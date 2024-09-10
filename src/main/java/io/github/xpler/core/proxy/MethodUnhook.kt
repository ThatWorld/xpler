package io.github.xpler.core.proxy

import de.robv.android.xposed.XC_MethodHook
import java.lang.reflect.Member

class MethodUnhook private constructor(
    private val unhook: XC_MethodHook.Unhook,
) {
    val hookedMethod:Member
        get() = unhook.hookedMethod

    fun unhook() {
        unhook.unhook()
    }
}