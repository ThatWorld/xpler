package io.github.xpler.core.wrapper

interface HookStart {

    /**
     * 由子类设置模块的`package`，用作获取模块启用状态[io.github.xpler.HookState.isEnabled]
     */
    val modulePackage: String
}