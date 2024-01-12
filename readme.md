# Xpler



Xposed Kotlin 开发模板，更适合Kotlin编码风格。

将本项目作为 `module` 引入项目，然后正常Xposed编写流程操作即可。

`Xpler` 在原 `Xposed Api` 基础上进一步封装，使其支持Kotlin的DSL特性，更简洁的编写Hook逻辑。

注意：使用本模板，你仍需要手动创建和配置 `xposed_init`、`application meta-data`。



## Xpler Api

作为原 `Xposed Api` 的封装项目，`Xpler` 提供了部分基本Api。



### HookEntrance.kt

作为 `Xpler` 给 `Xposed` 提供的抽象入口类，你只需要继承`HookEntrance<T : HookStart>`，然后实现相应接口即可。

- ApplicationHookStart

  ```kotlin
  class HookInit : HookEntrance<HookInit>(), ApplicationHookStart {
      override val modulePackage: String
          get() = "com.example.module"
  
      override val scopes: Array<ApplicationHookStart.Scope>
          get() = arrayOf(
              "packageName" at "applicationClassName",
          )
  
      override fun onCreateBefore(lpparam: XC_LoadPackage.LoadPackageParam, hostApp: Application) {
          // Do not write or call the same Hook logic in onBefore and onAfter, as this is meaningless
      }
  
      override fun onCreateAfter(lpparam: XC_LoadPackage.LoadPackageParam, hostApp: Application) {
          HActivity()
      }
  }
  ```

  实现该接口后，将自动为宿主注入类加载器，你只需要在 `onCreateBefore` 或 `onCreateAfter` 中书写 Hook逻辑即可。

  `modulePackage` 为模块包名，必须提供，`Xpler` 会用它去加载 `HookState`，以便对于模块启用/未启用状态的获取。

  `scopes` 为宿主列表，需提供 `宿主包名` 和 `宿主启动应用程序(Application)`，不在 `scopes` 列表中的包名，尽管在`Xposed`中加入生效列表，`Xpler`也不会对该宿主生效。

  而如果，你只是需要一个简单的Hook，并不需要复杂操作，可以试试 `DefaultHookStart` 接口。

  

- DefaultHookStart

  ```kotlin
  class HookInit() : HookEntrance<HookInit>(), DefaultHookStart {
      override val modulePackage: String
          get() = "com.example.module"
  
      override fun loadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
          // the original calling logic
      }
  }
  ```

  该接口提供的 `loadPackage` 方法就是原始的 `handleLoadPackage` 操作。



> 不过，记得修改`xposed_init` 中的入口类，如上述的类名为：`com.example.module.HookInit`。
>
> 还有，如果有混淆优化，记得保留 `HookInit` 入口类。



### HookState.kt

该类汇总了框架状态，如果你想要判断模块是否生效、框架类型，可使用该类。



### KtXposedHelpers.kt

区别于原 `XposedHelpers` 该类提供了更符合Kotlin的编码风格：

- `XposedHelpers` 写法

  ```java
  XposedHelpers.findAndHookMethod(
          Activity.class,
          "onCreate",
          Bundle.class,
          new XC_MethodHook() {
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                  XposedBridge.log(param.method.getName() + " Before!");
              }
  
              @Override
              protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                  XposedBridge.log(param.method.getName() + " After!");
              }
          }
  );
  ```

- `KtXposedHelpers` 写法

  ```kotlin
  KtXposedHelpers
      .hookClass(Activity::class.java)
      .method("onCreate", Bundle::class.java) {
          onBefore {
              XposedBridge.log("${this.method.name} Before!")
          }
  
          onAfter {
              XposedBridge.log("${this.method.name} After!")
          }
      }
  ```

  并且 `KtXposedHelpers` 在原基础上缓存了目标 `Class` ，使同一个 `Class` 支持链式 `Hook`，如：

  ```kotlin
  KtXposedHelpers
      .hookClass(Activity::class.java)
      .method("onCreate", Bundle::class.java) {
          onBefore {
              XposedBridge.log("${this.method.name} Before!")
          }
  
          onAfter {
              XposedBridge.log("${this.method.name} After!")
          }
      }
      .method("onResume") {
          onBefore {
              XposedBridge.log("${this.method.name} Before!")
          }
  
          onAfter {
              XposedBridge.log("${this.method.name} After!")
          }
          
          // 当 onReplace 出现时, onBefore、onAfter 将失去意义，它们将不会被执行
          onReplace {
              XposedBridge.log("${this.method.name} Replace!")
          }
          
          // 在Hook逻辑被执行一次之后立即解除
          onUnhook { hookMethod, callback -> 
              
          }
      }
  ```

  而对于 `XC_MethodHook.MethodHookParam` 的使用，相信通过以上例子已经很明显了。

  得益于Kotlin的扩展特性，在 `onBefoe{..}`、`onAfter{..}` 、`onReplace{..}`作用域内，都属于 `XC_MethodHook.MethodHookParam`；故此，你可以使用 `this` 来表示 `param` 参数。

  不过，值得注意的是，`onBefore{..}` 和 `onAfter{..}` 属于同类方法，它们允许同时出现，并分别响应其对应执行周期；而 `onReplace{..}` 出现时，则代表了Hook方法会被直接替换，因此 `onBefore{..}` 和 `onAfter{..}` 不会被执行。

  `onUnhook{hookMethod, callback -> ..}` 属于选写(可写/可不写)方法，当它被书写之后，该Hook 方法都会在执行一次之后被立即解开(即Hook逻辑至少被执行一次)；或许你并未在 `onUnhook` 作用域内书写任何代码，但只要它出现了都会立即解开Hook，而 `KtXposedHelpers` 为 `onUnhook` 提供的作用域只是为了善后处理，仅此而已。

  

- `KtXposedHelpers` 的其他操作：

  获取模块中的Layout：

  ```kotlin
  val moduleLayout = KtXposedHelpers.inflateView<LinearLayout>(R.id.module_layout)
  ```

  获取模块中的资源：

  ```kotlin
  val moduleDrawable = KtXposedHelpers.getDrawable(R.drawable.module_background)
  ```

  更多：`getString`、`getColor`、`getAnimation`  等，请自行阅读方法注释。



### KtXposedMore.kt

得益于Kotlin的扩展特性，`Xpler `在部分类型的基础上增加系列扩展，以下是常用Api示例：

- 对  `XC_LoadPackage.LoadPackageParam` 类增加的扩展：

  ```kotlin
  lpparam.hookClass(Activity::class.java)
      .method("onCreate", Bundle::class.java) { ... }
      .method("onResume") { ... }
      .method("onStart") { ... }
  ```

- 对 `XC_MethodHook.MethodHookParam` 类增加的扩展：

  ```kotlin
  ...
  onAfter {
      thisContext //将当前hook对象转换为context, 如果转换失败抛出异常
      thisActivity //将当前hook对象转换为ctivity, 如果转换失败抛出异常
  }
  ...
  ```
  
- 对 `Context` 类增加的扩展：

  ```kotlin
  val moduleLayout = context.inflateModuleView<LinearLayout>(R.id.module_layout)
  
  val moduleDrawable = context.getModuleDrawable(R.drawable.module_background)
  ```

- 对 `Any `增加 `lpparam` 的扩展，可在任意对象中直接使用 `lpparam` 实例。

- 其他扩展，请自行阅读方法注释。



### XplerLog.kt

在模块开发中更具通俗的Log工具类，与Log类的调用基本一致，支持LogCat面板等级输出日志。



### HookEntity.kt

为了更合适通俗的编码方式，对于需要被Hook的目标类及其方法 `HookEntity` 支持以传统类定义的方式来书写Hook逻辑，下称`Hook逻辑类`。

对于某个Class目标的Hook，Hook逻辑类需要继承 `HookEntity<T>` 并将泛型 `<T>` 修改为目标Class，然后通过系列注解完成Hook逻辑的编写，最后在 `主逻辑` 中完成实例化，即可注入相关方法的Hook逻辑，以下是简单示例：

```kotlin
// 目标类 Activity
class HActivity : HookEntity<Activity>(){
   
    @OnBefore("onCreate")
    fun onCreateBefore(params: XC_MethodHook.MethodHookParam, savedInstanceState: Bundle?) {
        hookBlockRunning(params) { // this: XC_MethodHook.MethodHookParam
            XplerLog.d(
                "savedInstanceState: $savedInstanceState",
                "method: ${this.method}"
            )
        }
    }
    
    @OnAfter("onResume")
    fun onCreateBefore(params: XC_MethodHook.MethodHookParam) {
        hookBlockRunning(params) { // this: XC_MethodHook.MethodHookParam
            XplerLog.d(
                "thisObject: ${this.thisObject}",
                "method: ${this.method}"
            )
        }
    }
    
    ...
}

//////////////////////////

// HookInit
override fun onCreateAfter(lpparam: XC_LoadPackage.LoadPackageParam, hostApp: Application) {
    HActivity()
}

```

没错，参数 `params: XC_MethodHook.MethodHookParam` 不能被省略，并且它只能被放在首位。

以下是一个稍复杂的写法，自行体会：

```kotlin
class HMainActivity : HookEntity<MainActivity>(){
    
    @OnConstructorBefore
    fun constructorBefore(params: XC_MethodHook.MethodHookParam){
        hookBlockRunning(params) {
            XplerLog.d("thisObject: $thisObject")
        }.onFailure {
            XplerLog.e(it)
        }
    }
    
    @OnAfter("getUser")
    @ReturnType(name = "com.example.bean.User")
    fun getUserAfter(name: String, @Param("com.example.config.UserConfig") config: Any?){
        hookBlockRunning(params) { // this: XC_MethodHook.MethodHookParam
            XplerLog.d(
                "name: ${name}",
                "config: ${config}",
                "result: ${this.result}"
            )
        }
    }
}
```

和前文一样 `Xpler` 提供的时机注解 `@..Before`、`@..After`、`@..Replace`，中的 `@..Replace` 仍然会替换对应目标方法的逻辑，而这时对于 `@..Before`、`@..After` 则不会生效。



> Xpler 在 [FreedomPlus](https://github.com/GangJust/FreedomPlus) 中被很好的实践运用，如果你想要更多示例，请点击[这里](https://github.com/GangJust/FreedomPlus/tree/master/core/src/main/java/io/github/fplus/core/hook)。