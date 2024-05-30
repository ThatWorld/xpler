# 模块核心
-keepclassmembers,allowobfuscation class io.github.xpler.** { *; }
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.KeepParam
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.Param
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.ReturnType
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookOnce
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.OnBefore
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.OnAfter
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.OnReplace
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.OnConstructorBefore
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.OnConstructorAfter
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.OnConstructorReplace
-keepclassmembers,allowobfuscation class * extends io.github.xpler.core.entity.HookEntity
-keepclassmembers,allowobfuscation class * {
    @io.github.xpler.core.entity.KeepParam <methods>;
    @io.github.xpler.core.entity.Param <methods>;
    @io.github.xpler.core.entity.ReturnType <methods>;
    @io.github.xpler.core.entity.HookOnce <methods>;
    @io.github.xpler.core.entity.OnBefore <methods>;
    @io.github.xpler.core.entity.OnAfter <methods>;
    @io.github.xpler.core.entity.OnReplace <methods>;
    @io.github.xpler.core.entity.OnConstructorBefore <methods>;
    @io.github.xpler.core.entity.OnConstructorAfter <methods>;
    @io.github.xpler.core.entity.OnConstructorReplace <methods>;
}
-keep,allowobfuscation class io.github.xpler.HookEntrance
-keepclassmembers class io.github.xpler.HookState
