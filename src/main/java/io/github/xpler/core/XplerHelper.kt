package io.github.xpler.core

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.xpler.core.hook.ConstructorHookImpl
import io.github.xpler.core.hook.MethodHookImpl
import io.github.xpler.core.proxy.LoadParam
import io.github.xpler.core.hook.ConstructorHook
import io.github.xpler.core.hook.MethodHook
import io.github.xpler.utils.XplerUtils
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class XplerHelper private constructor() {
    private lateinit var targetClazz: Class<*>
    private lateinit var loadParam: LoadParam

    /**
     * Hook某个方法
     *
     * @param argsTypes 参数类型列表
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun constructor(vararg argsTypes: Any, block: ConstructorHook.() -> Unit): XplerHelper {
        ConstructorHookImpl(targetClazz, *argsTypes).apply(block).startHook()
        return this
    }

    /**
     * Hook某个类中的所有构造方法
     *
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun constructorAll(block: MethodHook.() -> Unit): XplerHelper {
        val constructors = targetClazz.declaredConstructors
        for (c in constructors) {
            c.isAccessible = true
            MethodHookImpl(c).apply(block).startHook()
        }
        return this
    }

    /**
     * Hook某个方法
     *
     * @param method 方法
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun method(method: Method, block: MethodHook.() -> Unit): XplerHelper {
        MethodHookImpl(method).apply(block).startHook()
        return this
    }

    /**
     * Hook某个方法
     *
     * @param methodName 方法名
     * @param argsTypes 参数类型列表
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun method(
        methodName: String,
        vararg argsTypes: Any,
        block: MethodHook.() -> Unit,
    ): XplerHelper {
        MethodHookImpl(targetClazz, methodName, *argsTypes).apply(block).startHook()
        return this
    }

    /**
     * Hook某个类中的所有方法(构造方法除外)
     *
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun methodAll(block: MethodHook.() -> Unit): XplerHelper {
        val methods = targetClazz.declaredMethods
        for (method in methods) {
            if (Modifier.isAbstract(method.modifiers))
                continue

            method.isAccessible = true
            MethodHookImpl(method).apply(block).startHook()
        }
        return this
    }

    /**
     * Hook某个类中所有[methodName]同名方法,
     *
     * 不在乎参数类型、数量
     *
     * @param methodName 方法名
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun methodAllByName(
        methodName: String,
        block: MethodHook.() -> Unit,
    ): XplerHelper {
        val methods = targetClazz.declaredMethods
        for (method in methods) {
            if (Modifier.isAbstract(method.modifiers))
                continue

            if (method.name != methodName)
                continue

            method.isAccessible = true
            MethodHookImpl(method).apply(block).startHook()
        }
        return this
    }

    /**
     * Hook某个类中所有参数类型[argsTypes]相同的方法,
     *
     * 不在乎方法名、返回类型
     *
     * @param argsTypes 方法名
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun methodAllByParamTypes(
        vararg argsTypes: Class<*>,
        block: MethodHook.() -> Unit,
    ): XplerHelper {
        if (argsTypes.isEmpty()) {
            XplerLog.e("argsTypes is empty!")
            return this
        }

        val methods = targetClazz.declaredMethods
        for (method in methods) {
            if (Modifier.isAbstract(method.modifiers))
                continue

            if (!XplerUtils.compareParamTypes(method, argsTypes))
                continue

            method.isAccessible = true
            MethodHookImpl(method).apply(block).startHook()
        }
        return this
    }

    /**
     * Hook某个类中所有方法返回类型为[returnType]的方法,
     *
     * 不在乎方法名, 参数类型
     *
     * @param returnType 返回类型
     */
    fun methodAllByReturnType(
        returnType: Class<*>,
        block: MethodHook.() -> Unit,
    ): XplerHelper {
        val methods = targetClazz.declaredMethods
        for (method in methods) {
            if (method.returnType != returnType)
                continue

            method.isAccessible = true
            MethodHookImpl(method).apply(block).startHook()
        }
        return this
    }

    companion object {
        private val instance = XplerHelper()

        /**
         * 初始化全局param实例
         *
         * @param param XplerParam
         */
        fun initLoadParam(param: LoadParam) {
            instance.loadParam = param
        }

        /**
         * 获取param实例
         */
        val lparam: LoadParam
            get() = instance.loadParam

        /**
         * Hook某个类
         *
         * @param clazz 类
         */
        fun hookClass(clazz: Class<*>): XplerHelper {
            instance.targetClazz = clazz
            return instance
        }

        /**
         * Hook某个类
         *
         * @param className 类名
         * @param loader 类加载器
         */
        fun hookClass(
            className: String,
            loader: ClassLoader? = lparam.classLoader,
        ): XplerHelper {
            instance.targetClazz = XposedHelpers.findClass(XplerUtils.simpleName(className), loader)
            return instance
        }

        /**
         * 查找某个类
         *
         * @param className 类名
         * @param loader 类加载器
         */
        fun findClass(
            className: String,
            loader: ClassLoader? = lparam.classLoader,
        ): Class<*> {
            return XposedHelpers.findClass(XplerUtils.simpleName(className), loader)
        }

        /**
         * 查找某个字段
         *
         * @param className 类名
         * @param fieldName 字段名
         */
        fun findField(
            className: String,
            fieldName: String,
        ): Field {
            return XposedHelpers.findField(findClass(className), fieldName)
        }

        /**
         * 查找某个方法
         *
         * @param className 类名
         * @param methodName 方法名
         * @param argsTypes 参数类型列表
         */
        fun findMethod(
            className: String,
            methodName: String,
            vararg argsTypes: Class<*>,
        ): Method {
            return XposedHelpers.findMethodExact(findClass(className), methodName, *argsTypes)
        }

        /**
         * 获取字段值
         *
         * @param any 对象实例
         * @param fieldName 字段名
         */
        fun getFieldValue(
            any: Any,
            fieldName: String,
        ): Any? {
            return XposedHelpers.getObjectField(any, fieldName)
        }

        /**
         * 设置字段值
         *
         * @param any 对象实例
         * @param fieldName 字段名
         * @param value 值
         */
        fun setFieldValue(
            any: Any,
            fieldName: String,
            value: Any?,
        ) {
            XposedHelpers.setObjectField(any, fieldName, value)
        }

        /**
         * 获取静态字段值
         *
         * @param clazz 类
         * @param fieldName 字段名
         */
        fun getStaticFieldValue(
            clazz: Class<*>,
            fieldName: String,
        ): Any? {
            return XposedHelpers.getStaticObjectField(clazz, fieldName)
        }

        /**
         * 设置静态字段值
         *
         * @param clazz 类
         * @param fieldName 字段名
         * @param value 值
         */
        fun setStaticFieldValue(
            clazz: Class<*>,
            fieldName: String,
            value: Any?,
        ) {
            XposedHelpers.setStaticObjectField(clazz, fieldName, value)
        }

        /**
         * 调用方法
         *
         * @param any 对象实例
         * @param methodName 方法名
         * @param args 参数列表实例对象
         */
        fun invokeMethod(
            any: Any,
            methodName: String,
            vararg args: Any?,
        ): Any? {
            return XposedHelpers.callMethod(any, methodName, *args)
        }

        /**
         * 调用方法
         *
         * @param any 对象实例
         * @param methodName 方法名
         * @param argTypes 参数类型列表
         * @param args 参数实例对象列表
         */
        fun invokeMethod(
            any: Any,
            methodName: String,
            argTypes: Array<Class<*>>,
            vararg args: Array<Any?>,
        ): Any? {
            return XposedHelpers.callMethod(any, methodName, argTypes, *args)
        }

        /**
         * 调用静态方法
         *
         * @param clazz 类
         * @param methodName 方法名
         * @param args 参数实例对象列表
         */
        fun invokeStaticMethod(
            clazz: Class<*>,
            methodName: String,
            vararg args: Any?,
        ): Any? {
            return XposedHelpers.callStaticMethod(clazz, methodName, *args)
        }

        /**
         * 调用静态方法
         *
         * @param clazz 类
         * @param methodName 方法名
         * @param argTypes 参数类型列表
         * @param args 参数实例对象列表
         */
        fun invokeStaticMethod(
            clazz: Class<*>,
            methodName: String,
            argTypes: Array<Class<*>>,
            vararg args: Array<Any?>,
        ): Any? {
            return XposedHelpers.callStaticMethod(clazz, methodName, argTypes, *args)
        }

        /**
         * 调用原始方法
         *
         * @param member 方法
         * @param any 对象实例
         * @param args 参数实例对象列表
         */
        fun invokeOriginalMethod(
            member: Member,
            any: Any,
            vararg args: Array<out Any?>,
        ): Any? {
            return XposedBridge.invokeOriginalMethod(member, any, args)
        }
    }
}