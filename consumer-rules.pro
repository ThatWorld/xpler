# 模块核心
-keepclassmembers class io.github.xpler.** { *; }
-keep,allowobfuscation class io.github.xpler.core.entity.KeepParam
-keep,allowobfuscation class io.github.xpler.core.entity.Param
-keep,allowobfuscation class io.github.xpler.core.entity.FutureHook
-keep,allowobfuscation class io.github.xpler.core.entity.ReturnType
-keep,allowobfuscation class io.github.xpler.core.entity.HookOnce
-keep,allowobfuscation class io.github.xpler.core.entity.OnBefore
-keep,allowobfuscation class io.github.xpler.core.entity.OnAfter
-keep,allowobfuscation class io.github.xpler.core.entity.OnReplace
-keep,allowobfuscation class io.github.xpler.core.entity.OnConstructorBefore
-keep,allowobfuscation class io.github.xpler.core.entity.OnConstructorAfter
-keep,allowobfuscation class io.github.xpler.core.entity.OnConstructorReplace
-keepclassmembers class * {
    @io.github.xpler.core.entity.KeepParam <methods>;
    @io.github.xpler.core.entity.Param <methods>;
    @io.github.xpler.core.entity.FutureHook <methods>;
    @io.github.xpler.core.entity.ReturnType <methods>;
    @io.github.xpler.core.entity.HookOnce <methods>;
    @io.github.xpler.core.entity.OnBefore <methods>;
    @io.github.xpler.core.entity.OnAfter <methods>;
    @io.github.xpler.core.entity.OnReplace <methods>;
    @io.github.xpler.core.entity.OnConstructorBefore <methods>;
    @io.github.xpler.core.entity.OnConstructorAfter <methods>;
    @io.github.xpler.core.entity.OnConstructorReplace <methods>;
}
-keep,allowobfuscation class io.github.xpler.HookState
-keep,allowobfuscation class io.github.xpler.HookEntrance