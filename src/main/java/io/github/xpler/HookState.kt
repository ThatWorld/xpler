package io.github.xpler

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.annotation.Keep
import io.github.xpler.utils.XplerUtils
import org.json.JSONObject

object HookState {

    /**
     * 判断模块是否可用。
     *
     * see at: [io.github.xpler.HookEntrance.initModule]
     *
     */
    val isEnabled: Boolean
        get() = false

    /**
     * 似乎每个框架都自定义了 logcat tag。
     *
     * 通过反射 XposedBridge.TAG 来获取框架类型。
     *
     * see at: [io.github.xpler.HookEntrance.initModule]
     */
    val framework: String
        get() = "Unknown"

    /**
     * 获取当前框架版本号。
     */
    val version: Int
        get() = -1

    /**
     * 判断模块是否被太极启用。
     *
     * @param context Context
     * @return 是否被启用
     */
    fun isExpActive(context: Context): Boolean {
        // 是否安装太极
        val installed = XplerUtils.isAppInstalled(context, "me.weishu.exp")
        if (!installed) return false

        // 模块启用检测
        val resolver = context.contentResolver
        val uri = Uri.parse("content://me.weishu.exposed.CP/")
        var result: Bundle? = null
        try {
            try {
                result = resolver.call(uri, "active", null, null)
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            if (result == null) {
                result = resolver.call(uri, "active", null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        result ?: return false
        return result.getBoolean("active", false)
    }

    /**
     * 判断某个已安装的App是否由LSPatch内置模块。
     *
     * 调用该方法需要Manifest声明权限：android.permission.QUERY_ALL_PACKAGES
     * @param context Context
     * @param packageName PackageName
     * @return 字符串数组, 包含LSPatch基本信息：运行模式、版本名、版本号。
     */
    fun isLSPatchActive(context: Context, packageName: String): Array<String> {
        // see at:
        // https://github.com/LSPosed/LSPatch/blob/master/manager/src/main/java/org/lsposed/lspatch/util/LSPPackageManager.kt#L73
        // https://github.com/LSPosed/LSPatch/blob/master/manager/src/main/java/org/lsposed/lspatch/ui/viewmodel/manage/AppManageViewModel.kt#L42
        try {
            val packageInfo = XplerUtils.getPackageInfo(
                context = context,
                packageName = packageName,
                flags = PackageManager.GET_META_DATA,
            )
            val appInfo = packageInfo?.applicationInfo
            val config = appInfo?.metaData?.getString("lspatch") ?: return emptyArray()

            val json = Base64.decode(config, Base64.DEFAULT).toString(Charsets.UTF_8)
            val patchConfig = JSONObject(json)
            val useManager = patchConfig.getBoolean("useManager")
            val lspConfig = patchConfig.getJSONObject("lspConfig")
            val versionName = lspConfig.getString("VERSION_NAME")
            val versionCode = lspConfig.getString("VERSION_CODE")
            return arrayOf(if (useManager) "本地模式" else "集成模式", versionName, versionCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyArray()
    }
}