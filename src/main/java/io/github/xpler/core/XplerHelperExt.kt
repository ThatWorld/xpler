package io.github.xpler.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import io.github.xpler.core.hook.ConstructorHook
import io.github.xpler.core.hook.ConstructorHookImpl
import io.github.xpler.core.hook.MethodHook
import io.github.xpler.core.hook.MethodHookImpl
import io.github.xpler.core.proxy.LoadParam
import io.github.xpler.core.proxy.MethodParam
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/// LoadParam
fun LoadParam.hookClass(
    className: String,
    loader: ClassLoader? = null,
): XplerHelper {
    return XplerHelper.hookClass(className, loader ?: classLoader)
}

fun LoadParam.hookClass(
    clazz: Class<*>,
): XplerHelper {
    return XplerHelper.hookClass(clazz.name, classLoader)
}

fun LoadParam.findClass(
    className: String,
    loader: ClassLoader? = null,
): Class<*> {
    return XplerHelper.findClass(className, loader ?: classLoader)
}

fun LoadParam.findField(
    className: String,
    fieldName: String,
): Field {
    return XplerHelper.findField(className, fieldName)
}

fun LoadParam.findMethod(
    className: String,
    methodName: String,
    vararg argsTypes: Class<*>,
): Method {
    return XplerHelper.findMethod(className, methodName, *argsTypes)
}


/// Context
val Context.modulePackageInfo: PackageInfo?
    get() = XplerModule.modulePackageInfo(this)

val Context.moduleVersionName: String?
    get() = XplerModule.moduleVersionName(this)

val Context.moduleVersionCode: Long?
    get() = XplerModule.moduleVersionCode(this)

fun Context.inflate(
    @LayoutRes res: Int,
    root: ViewGroup?,
    attachToRoot: Boolean = root != null,
): View {
    return LayoutInflater.from(this).inflate(res, root, attachToRoot)
}


/// Any
val Any.stackTraceString: String
    get() = Log.getStackTraceString(Throwable("Stack trace"))

val Any.lparam: LoadParam
    get() = XplerHelper.lparam


/// MethodParam
@get:Throws(TypeCastException::class)
val MethodParam.thisApplication: Application
    get() = thisObject as Application

val MethodParam.thisApplicationOrNull: Application?
    get() {
        if (thisObject == null || thisObject !is Application)
            return null

        return thisObject as Application
    }

@get:Throws(TypeCastException::class)
val MethodParam.thisActivity: Activity
    get() = thisObject as Activity

val MethodParam.thisActivityOrNull: Activity?
    get() {
        if (thisObject == null || thisObject !is Activity)
            return null

        return thisObject as Activity
    }

@get:Throws(TypeCastException::class)
val MethodParam.thisContext: Context
    get() = thisObject as Context

val MethodParam.thisContextOrNull: Context?
    get() {
        if (thisObject == null || thisObject !is Context)
            return null

        return thisObject as Context
    }

@get:Throws(TypeCastException::class)
val MethodParam.thisView: View
    get() = thisObject as View

val MethodParam.thisViewOrNull: View?
    get() {
        if (thisObject == null || thisObject !is View)
            return null

        return thisObject as View
    }

@get:Throws(TypeCastException::class)
val MethodParam.thisViewGroup: ViewGroup
    get() = thisObject as ViewGroup

val MethodParam.thisViewGroupOrNull: ViewGroup?
    get() {
        if (thisObject == null || thisObject !is ViewGroup)
            return null

        return thisObject as ViewGroup
    }

inline fun <R> hookBlockRunning(
    params: MethodParam,
    block: MethodParam.() -> R,
): Result<R> {
    return runCatching {
        block.invoke(params)
    }
}


/// more
inline fun constructorHook(constructor: Constructor<*>, block: ConstructorHook.() -> Unit) {
    ConstructorHookImpl(constructor).apply(block).startHook()
}

inline fun methodHook(method: Method, block: MethodHook.() -> Unit) {
    MethodHookImpl(method).apply(block).startHook()
}
