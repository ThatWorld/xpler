package io.github.xpler.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.lang.reflect.Method

object XplerUtils {

    /**
     * 获取 APK 包信息.
     *
     * @param context 上下文
     * @param apkFile APK 文件
     */
    fun getApkPackageInfo(
        context: Context,
        apkFile: File,
    ): PackageInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val flags = PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
            context.packageManager.getPackageArchiveInfo(apkFile.absolutePath, flags)
        } else {
            context.packageManager.getPackageArchiveInfo(
                apkFile.absolutePath,
                PackageManager.GET_ACTIVITIES
            )
        }
    }

    /**
     * 获取应用信息.
     *
     * @param context 上下文
     * @param packageName 包名
     * @param flags 标志
     */
    fun getPackageInfo(
        context: Context,
        packageName: String = context.packageName,
        flags: Int = PackageManager.GET_ACTIVITIES,
    ): PackageInfo? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(flags.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(packageName, flags)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 判断应用是否安装.
     *
     * @param context 上下文
     * @param packageName 包名
     */
    fun isAppInstalled(
        context: Context,
        packageName: String = context.packageName,
    ): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 参数类型比较, 若某个参数为 null 则模糊匹配, 返回 `true`, 否则进行类型比较。
     *
     * @param method 被比较方法
     * @param targetParamTypes 被比较的参数类型列表
     */
    fun compareParamTypes(method: Method, targetParamTypes: Array<out Class<*>?>): Boolean {
        val parameterTypes = method.parameterTypes

        // 比较数量
        if (parameterTypes.size != targetParamTypes.size) {
            return false
        }

        for (i in parameterTypes.indices) {
            val type = parameterTypes[i]
            val targetType = targetParamTypes[i] ?: continue // null则模糊匹配

            // 类直接比较
            if (type == targetType) {
                continue
            }

            // 参数类型不一致
            return false
        }

        // 所有参数类型一致
        return true
    }
}