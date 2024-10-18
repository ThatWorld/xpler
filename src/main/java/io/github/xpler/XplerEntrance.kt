package io.github.xpler

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.xpler.core.XplerHelper
import io.github.xpler.core.XplerLog
import io.github.xpler.core.XplerModule
import io.github.xpler.core.entrance.ApplicationHookStart
import io.github.xpler.core.entrance.DefaultHookStart
import io.github.xpler.core.entrance.HookStart
import io.github.xpler.core.hookClass
import io.github.xpler.loader.injectClassLoader
import io.github.xpler.core.proxy.LoadParam
import io.github.xpler.core.thisApplication

// Hook init entrance
abstract class XplerEntrance : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private val loadParamConstructor = LoadParam::class.java.getDeclaredConstructor(XC_LoadPackage.LoadPackageParam::class.java)
        .apply { isAccessible = true }

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam) {
        XplerModule.initModule(sparam.modulePath)
    }

    override fun handleLoadPackage(lp: XC_LoadPackage.LoadPackageParam) {
        if (this !is HookStart) {
            XposedBridge.log(IllegalArgumentException("You must implement the `HookStart` sub-level interface."))
            return
        }

        val xplerParam = loadParamConstructor.newInstance(lp)

        // init module status
        if (xplerParam.packageName == this.modulePackage) {
            initModule(xplerParam)
            return
        }

        // hook entrance
        when (this) {
            is ApplicationHookStart -> applicationHookStart(xplerParam, this)
            is DefaultHookStart -> defaultHookStart(xplerParam, this)
        }
    }

    // module status hook!!
    private fun initModule(param: LoadParam) {
        param.hookClass(XplerState::class.java)
            .method("isEnabled") {
                onAfter {
                    setResult(true)
                }
            }
            .method("getVersion") {
                onAfter {
                    setResult(XposedBridge.getXposedVersion())
                }
            }
            .method("getFramework") {
                onAfter {
                    val bridgeTag = "${XposedHelpers.getStaticObjectField(XposedBridge::class.java, "TAG")}"
                    setResult(
                        if (bridgeTag.startsWith("LSPosed")) {
                            "LSPosed"
                        } else if (bridgeTag.startsWith("EdXposed")) {
                            "EdXposed"
                        } else if (bridgeTag.startsWith("Xposed")) {
                            "Xposed"
                        } else {
                            "Unknown"
                        }
                    )
                }
            }
    }

    // ApplicationHookStart
    private fun applicationHookStart(
        param: LoadParam,
        start: ApplicationHookStart,
    ) {
        val scopes = start.scopes
        val scopePackageNames = scopes.map { it.packageName }

        // compare package name
        if (!scopePackageNames.contains(param.packageName)) {
            return
        }

        // host application
        scopes.forEach { scope ->
            // filter package name
            if (scope.packageName != param.packageName) {
                return@forEach
            }

            // filter process name
            if (!scope.processName.isNullOrEmpty() && scope.processName != param.processName) {
                return@forEach
            }

            if (scope.applicationClassName.isEmpty()) {
                XposedBridge.log(IllegalArgumentException("This scope provided application class name it's empty."))
                return@forEach
            }

            param.hookClass(scope.applicationClassName, param.classLoader)
                .method("onCreate") {
                    onBefore {
                        XplerLog.isXposed(true) // default output of logs to xposed
                        XplerHelper.initLoadParam(param)
                        val application = thisApplication
                        injectClassLoader(param, application.classLoader)
                        start.onCreateBefore(param, application)
                    }
                    onAfter {
                        val application = thisApplication
                        start.onCreateAfter(param, application)
                    }
                }
        }
    }

    // DefaultHookStart
    private fun defaultHookStart(
        param: LoadParam,
        start: DefaultHookStart,
    ) {
        XplerLog.isXposed(true) // default output of logs to xposed
        XplerHelper.initLoadParam(param)
        start.loadPackage(param)
    }
}

