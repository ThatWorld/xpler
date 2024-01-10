package io.github.xpler.core.impl

import de.robv.android.xposed.XposedHelpers
import io.github.xpler.core.wrapper.ConstructorHook


/// 实现类
class ConstructorHookImpl(clazz: Class<*>, vararg argsTypes: Any) :
    MethodHookImpl(XposedHelpers.findConstructorExact(clazz, *argsTypes)), ConstructorHook {

}