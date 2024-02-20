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
    val scopes: Set<Scope>

    /**
     * 在宿主Application onCreate 之前调用
     *
     * 不要在 [onCreateBefore]、[onCreateAfter] 中书写或调用相同的Hook逻辑, 毫无意义。
     */
    fun onCreateBefore(
        lp: XC_LoadPackage.LoadPackageParam,
        hostApp: Application,
    )

    /**
     * 在宿主Application onCreate 之后调用
     *
     * 不要在 [onCreateBefore]、[onCreateAfter] 中书写或调用相同的Hook逻辑, 毫无意义。
     */
    fun onCreateAfter(
        lp: XC_LoadPackage.LoadPackageParam,
        hostApp: Application,
    )

    /**
     * 针对应用的Hook作用域。
     *
     * @param packageName 宿主包名
     * @param applicationClassName 宿主Application
     * @param processName 指定某个进程名，为空则对宿主所有进程生效
     */
    data class Scope(
        val packageName: String,
        val applicationClassName: String,
        val processName: String? = null,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Scope

            if (packageName != other.packageName) return false
            if (applicationClassName != other.applicationClassName) return false
            if (processName != other.processName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = packageName.hashCode()
            result = 31 * result + applicationClassName.hashCode()
            result = 31 * result + (processName?.hashCode() ?: 0)
            return result
        }
    }
}

//
infix fun String.at(applicationClassName: String): ApplicationHookStart.Scope =
    ApplicationHookStart.Scope(
        packageName = this,
        applicationClassName = applicationClassName,
    )

infix fun String.at(part: Pair<String, String>): ApplicationHookStart.Scope =
    ApplicationHookStart.Scope(
        packageName = this,
        applicationClassName = part.first,
        processName = part.second
    )