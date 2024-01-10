package io.github.xpler.core.wrapper

import de.robv.android.xposed.XC_MethodHook
import java.lang.reflect.Member

/// 扩展方法
typealias OnBeforeBlock = XC_MethodHook.MethodHookParam.() -> Unit
typealias OnAfterBlock = XC_MethodHook.MethodHookParam.() -> Unit
typealias OnReplaceBlock = XC_MethodHook.MethodHookParam.() -> Any
typealias OnUnhookBlock = (hookMethod: Member, callback: XC_MethodHook) -> Unit

///
interface IHook {

}