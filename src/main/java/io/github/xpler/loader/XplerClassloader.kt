package io.github.xpler.loader

import android.content.Context
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.xpler.HookEntrance
import java.net.URL

class XplerClassloader(
    private val host: ClassLoader,
    private val parent: ClassLoader,
) : ClassLoader() {
    private val bootClassloader = Context::class.java.classLoader

    private fun skipLoad(name: String): Boolean {
        return name.startsWith("android.") || name.startsWith("androidx.")
                || name.startsWith("kotlin.") || name.startsWith("kotlinx.")
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        try {
            return bootClassloader?.loadClass(name) ?: throw ClassNotFoundException(name)
        } catch (e: ClassNotFoundException) {
            // e.printStackTrace()
        }

        /// bootClassloader优先加载, 然后跳过冲突类。fix: 太极框架崩溃
        if (skipLoad(name))
            throw ClassNotFoundException(name)

        ///
        var clazz = findLoadedClass(name)
        if (clazz != null)
            return clazz

        try {
            clazz = parent.loadClass(name)
        } catch (e: ClassNotFoundException) {
            // e.printStackTrace()
        }

        try {
            clazz = host.loadClass(name)
        } catch (e: ClassNotFoundException) {
            // e.printStackTrace()
        }

        // Log.d("XplerLog", "name: $name\nload: ${clazz?.classLoader}")

        return clazz ?: throw ClassNotFoundException(name)
    }

    override fun getResource(name: String?): URL {
        return parent.getResource(name) ?: host.getResource(name)
    }
}

fun injectClassLoader(
    lpparam: XC_LoadPackage.LoadPackageParam,
    loader: ClassLoader,
) {
    val fParent = ClassLoader::class.java.declaredFields.first { it.name == "parent" }.apply { isAccessible = true }
    val mine = HookEntrance::class.java.classLoader
    val parent = fParent.get(mine) as ClassLoader
    if (parent::class.java != XplerClassloader::class.java) {
        hostClassloader = loader
        moduleClassloader = mine
        fParent.set(mine, XplerClassloader(loader, parent).also { lpparam.classLoader = it })
    } else {
        lpparam.classLoader = parent
    }
}

var hostClassloader: ClassLoader? = null
var moduleClassloader: ClassLoader? = null