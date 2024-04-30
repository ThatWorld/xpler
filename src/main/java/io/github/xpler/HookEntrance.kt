package io.github.xpler

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.xpler.core.KtXposedHelpers
import io.github.xpler.core.getStaticObjectField
import io.github.xpler.core.hookClass
import io.github.xpler.core.log.XplerLog
import io.github.xpler.core.thisApplication
import io.github.xpler.core.wrapper.ApplicationHookStart
import io.github.xpler.core.wrapper.DefaultHookStart
import io.github.xpler.core.wrapper.HookStart
import io.github.xpler.loader.injectClassLoader
import java.lang.reflect.ParameterizedType

// Hook init entrance
abstract class HookEntrance<T : HookStart> :
    IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hookStart by lazy {
        val type = this::class.java.genericSuperclass as ParameterizedType
        type.actualTypeArguments[0] as Class<*>
    }

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam) {
        KtXposedHelpers.initModule(sparam.modulePath)
    }

    override fun handleLoadPackage(lp: XC_LoadPackage.LoadPackageParam) {
        if (hookStart.isInterface) {
            XposedBridge.log(IllegalArgumentException("You must provide an implementation class for `HookStart` instead of an interface."))
            return
        }

        // create
        val newInstance = hookStart.getConstructor().newInstance() as HookStart

        // init module status
        if (lp.packageName == newInstance.modulePackage) {
            initModule(lp)
            return
        }

        // hook entrance
        when (newInstance) {
            is ApplicationHookStart -> applicationHookStart(lp, newInstance)
            is DefaultHookStart -> defaultHookStart(lp, newInstance)
        }
    }

    // module status hook!!
    private fun initModule(lp: XC_LoadPackage.LoadPackageParam) {
        lp.hookClass(HookState::class.java)
            .method("isEnabled") {
                onAfter {
                    result = true
                }
            }
            .method("getVersion") {
                onAfter {
                    result = XposedBridge.getXposedVersion()
                }
            }
            .method("getFramework") {
                onAfter {
                    val bridgeTag = XposedBridge::class.java.getStaticObjectField<String>("TAG") ?: ""
                    result = if (bridgeTag.startsWith("LSPosed")) {
                        "LSPosed"
                    } else if (bridgeTag.startsWith("EdXposed")) {
                        "EdXposed"
                    } else if (bridgeTag.startsWith("Xposed")) {
                        "Xposed"
                    } else {
                        "Unknown"
                    }
                }
            }
    }

    // ApplicationHookStart
    private fun applicationHookStart(
        lp: XC_LoadPackage.LoadPackageParam,
        start: ApplicationHookStart,
    ) {
        val scopes = start.scopes
        val scopePackageNames = scopes.map { it.packageName }

        // compare package name
        if (!scopePackageNames.contains(lp.packageName)) {
            return
        }

        // host application
        scopes.forEach { scope ->
            // filter package name
            if (scope.packageName != lp.packageName) {
                return@forEach
            }

            // filter process name
            if (!scope.processName.isNullOrEmpty() && scope.processName != lp.processName) {
                return@forEach
            }

            if (scope.applicationClassName.isEmpty()) {
                XposedBridge.log(IllegalArgumentException("This scope provided application class name it's empty."))
                return@forEach
            }

            lp.hookClass(scope.applicationClassName)
                .method("onCreate") {
                    onBefore {
                        XplerLog.isXposed(true) // default output of logs to xposed
                        KtXposedHelpers.setLpparam(lp)
                        val application = thisApplication
                        injectClassLoader(lp, application.classLoader)
                        start.onCreateBefore(lp, application)
                    }
                    onAfter {
                        val application = thisApplication
                        start.onCreateAfter(lp, application)
                    }
                }
        }
    }

    // DefaultHookStart
    private fun defaultHookStart(
        lp: XC_LoadPackage.LoadPackageParam,
        start: DefaultHookStart,
    ) {
        XplerLog.isXposed(true) // default output of logs to xposed
        KtXposedHelpers.setLpparam(lp)
        start.loadPackage(lp)
    }
}

