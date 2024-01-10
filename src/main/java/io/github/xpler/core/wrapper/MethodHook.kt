package io.github.xpler.core.wrapper

/// 对普通方法的 Hook 封装
interface MethodHook : IHook {

    /**
     * [onBefore]会在某个Hook方法执行之前被调用，
     * 等价于[de.robv.android.xposed.XC_MethodHook.beforeHookedMethod]方法。
     *
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun onBefore(block: OnBeforeBlock)

    /**
     * [onAfter]会在某个Hook方法执行之后被调用，
     * 等价于[de.robv.android.xposed.XC_MethodHook.afterHookedMethod]方法。
     *
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun onAfter(block: OnAfterBlock)

    /**
     * 该方法会直接 替换 某个Hook方法, 一旦[onReplace]被显示调用, [onBefore]和[onAfter]将不会被响应，
     * 等价于[de.robv.android.xposed.XC_MethodReplacement.replaceHookedMethod]方法。
     *
     * @param block hook代码块, 可在内部书写hook逻辑
     */
    fun onReplace(block: OnReplaceBlock)

    /**
     * 该方法会解开某个Hook方法, 被书写后在每次[onReplace]、[onBefore]、[onAfter]执行完毕, 都会被调用。
     *
     * 如果不需要在某个Hook方法执行之后解Hook (即表示当前进程下, hook逻辑只被执行一次), 请不要书写该方法。
     *
     * @param block deHook代码块
     */
    fun onUnhook(block: OnUnhookBlock)
}