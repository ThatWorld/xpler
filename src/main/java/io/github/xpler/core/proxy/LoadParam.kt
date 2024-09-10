package io.github.xpler.core.proxy

import android.content.pm.ApplicationInfo
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONObject

class LoadParam private constructor(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    var packageName: String
        get() = lpparam.packageName
        set(value) {
            lpparam.packageName = value
        }

    var processName: String
        get() = lpparam.processName
        set(value) {
            lpparam.packageName = value
        }

    var classLoader: ClassLoader
        get() = lpparam.classLoader
        set(value) {
            lpparam.classLoader = value
        }

    var appInfo: ApplicationInfo
        get() = lpparam.appInfo
        set(value) {
            lpparam.appInfo = value
        }

    var isFirstApplication: Boolean
        get() = lpparam.isFirstApplication
        set(value) {
            lpparam.isFirstApplication = value
        }

    override fun toString(): String {
        return JSONObject().apply {
            putOpt("packageName", packageName)
            putOpt("processName", processName)
            putOpt("classLoader", "$classLoader")
            putOpt("appInfo", "$appInfo")
            putOpt("isFirstApplication", isFirstApplication)
        }.toString(2)
    }
}