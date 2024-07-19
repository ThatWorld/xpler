# Xpler核心
-keepclassmembers class io.github.xpler.core.impl.MethodHookCallbackImpl { *; }
-keepclassmembers class io.github.xpler.core.impl.MethodReplacementCallbackImpl { *; }
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$KeepParam
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$Param
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$ReturnType
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$HookOnce
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$OnBefore
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$OnAfter
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$OnReplace
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$OnConstructorBefore
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$OnConstructorAfter
-keepclassmembers,allowobfuscation class io.github.xpler.core.entity.HookEntity$OnConstructorReplace
-keepclassmembers,allowobfuscation class * extends io.github.xpler.core.entity.HookEntity {
    @io.github.xpler.core.entity.HookEntity$KeepParam <methods>;
    @io.github.xpler.core.entity.HookEntity$Param <methods>;
    @io.github.xpler.core.entity.HookEntity$ReturnType <methods>;
    @io.github.xpler.core.entity.HookEntity$HookOnce <methods>;
    @io.github.xpler.core.entity.HookEntity$OnBefore <methods>;
    @io.github.xpler.core.entity.HookEntity$OnAfter <methods>;
    @io.github.xpler.core.entity.HookEntity$OnReplace <methods>;
    @io.github.xpler.core.entity.HookEntity$OnConstructorBefore <methods>;
    @io.github.xpler.core.entity.HookEntity$OnConstructorAfter <methods>;
    @io.github.xpler.core.entity.HookEntity$OnConstructorReplace <methods>;
}
-keepclassmembers class io.github.xpler.HookEntrance {
    public void *(de.robv.android.xposed.IXposedHookZygoteInit$StartupParam);
    public void *(de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam);
}
-keepclassmembers class io.github.xpler.HookState { *; }
