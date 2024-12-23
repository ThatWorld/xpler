package io.github.xpler.core

import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.content.res.XModuleResources
import android.os.Build
import de.robv.android.xposed.XposedHelpers
import io.github.xpler.utils.XplerUtils
import java.io.File

class XplerModule private constructor() {
    private lateinit var mModulePath: String
    private var mModuleRes: XModuleResources? = null
    private var mPackageInfo: PackageInfo? = null

    companion object {
        private val instance = XplerModule()

        /**
         * 初始化Xpler
         *
         * @param modulePath 模块路径
         */
        fun initModule(modulePath: String) {
            instance.mModulePath = modulePath
        }

        /**
         * 模块路径
         */
        val modulePath: String
            get() = instance.mModulePath

        /**
         * 模块资源
         */
        fun moduleResources(): XModuleResources {
            return instance.mModuleRes
                ?: XModuleResources.createInstance(modulePath, null)
                    .also { instance.mModuleRes = it }
        }

        /**
         * 模块PackageInfo
         *
         * @param context Context
         */
        fun modulePackageInfo(context: Context): PackageInfo? {
            return instance.mPackageInfo
                ?: XplerUtils.getApkPackageInfo(context, File(instance.mModulePath))
                    .also { instance.mPackageInfo = it }
        }

        /**
         * 模块版本名
         *
         * @param context Context
         */
        fun moduleVersionName(context: Context): String? {
            return modulePackageInfo(context)?.versionName
        }

        /**
         * 模块版本号
         *
         * @param context Context
         */
        fun moduleVersionCode(context: Context): Long? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                modulePackageInfo(context)?.longVersionCode
            } else {
                modulePackageInfo(context)?.versionCode?.toLong()
            }
        }

        /**
         * 注入资源, 将模块资源合并到宿主以应对资源找不到的情况
         * 避免资源id冲突建议, 重新指定资源id的前缀
         * ```kt
         * // app->build.gradle.kts
         * android {
         *  ...
         *   androidResources {
         *      additionalParameters += arrayOf("--allow-reserved-package-id", "--package-id", "0x60")
         *   }
         *  ...
         * }
         * ```
         *
         * @param context Context 宿主Context
         */
        fun injectResources(context: Context) {
            injectResources(context.resources)
        }

        /**
         * 注入资源, 将模块资源合并到宿主以应对资源找不到的情况
         * 避免资源id冲突, 重新指定修改资源id的前缀
         * ```kt
         * // app->build.gradle.kts
         * android {
         *  ...
         *   androidResources {
         *      additionalParameters += arrayOf("--allow-reserved-package-id", "--package-id", "0x60")
         *   }
         *  ...
         * }
         * ```
         *
         * @param resources Resources 宿主Resources
         */
        fun injectResources(resources: Resources?) {
            resources?.runCatching {
                XposedHelpers.callMethod(this, "addAssetPath", modulePath)
            }
        }
    }
}