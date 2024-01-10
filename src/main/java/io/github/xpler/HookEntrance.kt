package io.github.xpler

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.xpler.core.KtXposedHelpers
import io.github.xpler.core.getStaticObjectField
import io.github.xpler.core.hookClass
import io.github.xpler.core.log.XplerLog
import io.github.xpler.core.lpparam
import io.github.xpler.core.thisApplication
import io.github.xpler.core.wrapper.ApplicationHookStart
import io.github.xpler.core.wrapper.DefaultHookStart
import io.github.xpler.core.wrapper.HookStart
import io.github.xpler.loader.injectClassLoader
import java.lang.reflect.ParameterizedType

// Hook init entrance
abstract class HookEntrance<T : HookStart> : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private val hookStart by lazy {
        val type = this::class.java.genericSuperclass as ParameterizedType
        type.actualTypeArguments[0] as Class<*>
    }

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam) {
        KtXposedHelpers.initModule(sparam.modulePath)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // default output of logs to xposed
        XplerLog.isXposed(true)

        if (hookStart.isInterface) {
            XplerLog.e(IllegalArgumentException("You must provide an implementation class for `HookStart` instead of an interface."))
            return
        }

        // set global lpparam
        KtXposedHelpers.setLpparam(lpparam)

        // create
        val newInstance = hookStart.getConstructor().newInstance() as HookStart

        // init module status
        if (lpparam.packageName == newInstance.modulePackage) {
            initModule(lpparam)
            return
        }

        // hook entrance
        when (newInstance) {
            is ApplicationHookStart -> applicationHookStart(newInstance)
            is DefaultHookStart -> defaultHookStart(newInstance)
            else -> XplerLog.i("no hook entrance!!")
        }
    }

    // module status hook!!
    private fun initModule(lpparam: XC_LoadPackage.LoadPackageParam) {
        lpparam.hookClass(HookState::class.java)
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
    private fun applicationHookStart(start: ApplicationHookStart) {
        val scopes = start.scopes
        val scopePackageNames = scopes.map { it.packageName }

        // compare package name
        if (!scopePackageNames.contains(lpparam.packageName)) {
            return
        }

        // host application
        scopes.forEach { scope ->
            if (scope.packageName != lpparam.packageName) {
                return@forEach
            }

            if (scope.applicationClassName.trim().isEmpty()) {
                XplerLog.e(IllegalArgumentException("This scope provided application class name it's empty."))
                return@forEach
            }

            lpparam.hookClass(scope.applicationClassName)
                .method("onCreate") {
                    onBefore {
                        val application = thisApplication
                        injectClassLoader(lpparam, application.classLoader)
                        start.onCreateBefore(lpparam, application)
                    }
                    onAfter {
                        val application = thisApplication
                        start.onCreateAfter(lpparam, application)
                    }
                }
        }
    }

    // DefaultHookStart
    private fun defaultHookStart(start: DefaultHookStart) {
        start.loadPackage(lpparam)
    }
}

