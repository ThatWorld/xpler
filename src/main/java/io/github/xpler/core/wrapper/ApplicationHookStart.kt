package io.github.xpler.core.wrapper

import android.app.Application
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 需要宿主Application时的Hook
 *
 * see at: [io.github.xpler.HookEntrance.applicationHookStart]
 */
interface ApplicationHookStart : HookStart {
    /**
     * 由子类设置宿主的 `package` 和 `application`。
     */
    val scopes: Array<Scope>

    /**
     * 在宿主Application onCreate 之前调用
     *
     * 不要在 [onCreateBefore]、[onCreateAfter] 中书写或调用相同的Hook逻辑, 毫无意义。
     */
    fun onCreateBefore(
        lpparam: XC_LoadPackage.LoadPackageParam,
        hostApp: Application,
    )

    /**
     * 在宿主Application onCreate 之后调用
     *
     * 不要在 [onCreateBefore]、[onCreateAfter] 中书写或调用相同的Hook逻辑, 毫无意义。
     */
    fun onCreateAfter(
        lpparam: XC_LoadPackage.LoadPackageParam,
        hostApp: Application,
    )

    //
    data class Scope(
        val packageName: String,
        val applicationClassName: String,
    )
}

//
infix fun String.at(b: String): ApplicationHookStart.Scope = ApplicationHookStart.Scope(this, b)