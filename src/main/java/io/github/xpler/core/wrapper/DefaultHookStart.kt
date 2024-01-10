package io.github.xpler.core.wrapper

import de.robv.android.xposed.callbacks.XC_LoadPackage

interface DefaultHookStart : HookStart {

    /**
     * 与 [de.robv.android.xposed.IXposedHookLoadPackage.handleLoadPackage] 一致，提供原始的加载方式。
     *
     * see at: [io.github.xpler.HookEntrance.defaultHookStart]
     */
    fun loadPackage(lpparam: XC_LoadPackage.LoadPackageParam)
}