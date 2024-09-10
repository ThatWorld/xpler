package io.github.xpler.core.entrance

import io.github.xpler.core.proxy.LoadParam

interface DefaultHookStart : HookStart {

    /**
     * 与 [de.robv.android.xposed.IXposedHookLoadPackage.handleLoadPackage] 一致，提供原始的加载方式。
     *
     * see at: [io.github.xpler.XplerEntrance.defaultHookStart]
     */
    fun loadPackage(lparam: LoadParam)
}