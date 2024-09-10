package io.github.xpler.core.hook

import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Constructor


/// 实现类
class ConstructorHookImpl(constructor: Constructor<*>) :
    MethodHookImpl(constructor), ConstructorHook {

    constructor(clazz: Class<*>, vararg argsTypes: Any)
            : this(XposedHelpers.findConstructorExact(clazz, *argsTypes))
}