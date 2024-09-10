package io.github.xpler.core.entrance

interface HookStart {

    /**
     * 由子类设置模块的`package`，用作获取模块启用状态[io.github.xpler.XplerState.isEnabled]
     */
    val modulePackage: String
}