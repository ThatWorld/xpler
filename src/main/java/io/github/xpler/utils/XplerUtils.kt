package io.github.xpler.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

object XplerUtils {

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
}