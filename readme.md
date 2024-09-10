# Xpler

Xposed Kotlin 开发模板，更适合Kotlin编码风格。

`Xpler` 在原 `Xposed Api` 基础上进一步封装，使其支持Kotlin的DSL特性，更简洁的编写Hook逻辑。

注意：使用本模板，你仍需要手动创建和配置 `xposed_init`、`application meta-data`。

## Xpler Api

作为原 `Xposed Api` 的封装项目，`Xpler` 提供了部分基本Api。

### HookEntrance.kt

作为 `Xpler` 给 `Xposed` 提供的抽象入口类，你只需要继承 `XplerEntrance`，然后实现相应接口即可。

- ApplicationHookStart

  ```kotlin
  class HookInit : XplerEntrance(), ApplicationHookStart {
      override val modulePackage: String
          get() = "com.example.module"
  
      override val scopes: Set<ApplicationHookStart.Scope>
          get() = setOf(
              "packageName" at "applicationClassName",
              "packageName1" at ("applicationClassName1" to "processName"),
          )
  
      override fun onCreateBefore(lparam: MethodParam, hostApp: Application) {
          // Do not write or call the same Hook logic in onBefore and onAfter, as this is meaningless
      }
  
      override fun onCreateAfter(lparam: MethodParam, hostApp: Application) {
          HActivity()
      }
  }
  ```

  实现该接口后，将自动为宿主注入类加载器，你只需要在 `onCreateBefore` 或 `onCreateAfter` 中书写
  Hook逻辑即可。

  `modulePackage` 为模块包名，必须提供，`Xpler` 会用它去加载 `HookState`，以便对于模块启用/未启用状态的获取。

  `scopes` 为宿主列表，需提供 `宿主包名` 和 `宿主启动应用程序(Application)`、 `宿主进程名(prossName)可选`
  ，不在 `scopes` 列表中的包名，尽管在`Xposed`中加入生效列表，`Xpler`也不会对该宿主生效。

  而如果，你只是需要一个简单的Hook，并不需要复杂操作，可以试试 `DefaultHookStart` 接口。

- DefaultHookStart

  ```kotlin
  class HookInit : XplerEntrance(), DefaultHookStart {
      override val modulePackage: String
          get() = "com.example.module"
  
      override fun loadPackage(lparam: LoadParam) {
          // the original calling logic
      }
  }
  ```

  该接口提供的 `loadPackage` 方法就是原始的 `handleLoadPackage` 操作。

> 记得修改`xposed_init` 中的入口类，如上述的入口类名为：`com.example.module.HookInit`。
>
> 还有，如果有混淆优化，记得保留 `HookInit` 入口类。
>
> ```protobuf
> //proguard-rules.pro
> -keep class com.example.module.HookInit
> ```

### XplerState.kt

该类汇总了框架状态，如果你想要判断模块是否生效、框架类型，可使用该类。

### XplerHelper.kt

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

- `XplerHelper` 写法

  ```kotlin
  XplerHelper
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

  并且 `XplerHelper` 在原基础上缓存了目标 `Class` ，使同一个 `Class` 支持链式 `Hook`，如：

  ```kotlin
  XplerHelper
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
          
          // 解除hook逻辑
          onUnhook {
              unhook()
          }
      }
  ```

  而对于 `XC_MethodHook.MethodHookParam` 的使用，相信通过以上例子已经很明显了。

  得益于Kotlin的扩展特性，在 `onBefoe{..}`、`onAfter{..}` 、`onReplace{..}`作用域内，都属于
  `XC_MethodHook.MethodHookParam`；故此，你可以使用 `this` 来表示 `param` 参数。

  不过，值得注意的是，`onBefore{..}` 和 `onAfter{..}` 属于同类方法，它们允许同时出现，并分别响应其对应执行周期；而
  `onReplace{..}` 出现时，则代表了Hook方法会被直接替换，因此 `onBefore{..}` 和 `onAfter{..}` 不会被执行。

### XplerModule.kt

模块信息汇总：版本号、模块路径、模块资源

### XplerLog.kt

在模块开发中更具通俗的Log工具类，与Log类的调用基本一致，支持LogCat面板等级输出日志。

### HookEntity.kt

为了更合适通俗的编码方式，对于需要被Hook的目标类及其方法 `HookEntity` 支持以传统类定义的方式来书写Hook逻辑，下称
`Hook逻辑类`。

对于某个Class目标的Hook，Hook逻辑类需要继承 `HookEntity` 并实现抽象方法`setTargetClass`
主动设置目标Class，然后通过系列注解完成Hook逻辑的编写，最后在 `主逻辑` 中完成实例化，即可注入相关方法的Hook逻辑，以下是简单示例：

```kotlin
// 目标类 Activity
class HActivity : HookEntity() {

    override fun setTargetClass(): Class<*> {
        return findClass("android.app.Activity")
    }

    @OnBefore("onCreate")
    fun onCreateBefore(params: MethodParam, savedInstanceState: Bundle?) {
        hookBlockRunning(params) { // this: MethodParam
            XplerLog.d(
                "savedInstanceState: $savedInstanceState",
                "method: ${this.method}"
            )
        }
    }

    @OnAfter("onResume")
    fun onCreateBefore(params: MethodParam) {
        hookBlockRunning(params) { // this: MethodParam
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
override fun onCreateAfter(lparam: LoadParam, hostApp: Application) {
    HActivity()
}

```

没错，参数 `params: MethodParam` 不能被省略，并且它只能被放在首位。

以下是一个稍复杂的写法，自行体会：

```kotlin
class HMainActivity : HookEntity() {

    override fun setTargetClass(): Class<*> {
        return findClass("android.app.Activity")
    }

    @OnConstructorBefore
    fun constructorBefore(params: MethodParam) {
        hookBlockRunning(params) {
            XplerLog.d("thisObject: $thisObject")
        }.onFailure {
            XplerLog.e(it)
        }
    }

    @OnAfter("getUser")
    @ReturnType(name = "com.example.bean.User")
    fun getUserAfter(
        params: MethodParam,
        name: String,
        @Param("com.example.config.UserConfig") config: Any?,
    ) {
        hookBlockRunning(params) { // this: MethodParam
            XplerLog.d(
                "name: ${name}",
                "config: ${config}",
                "result: ${this.result}"
            )
        }
    }
}
```

和前文一样 `Xpler` 提供的时机注解 `@..Before`、`@..After`、`@..Replace`，中的 `@..Replace`
仍然会替换对应目标方法的逻辑，而这时对于 `@..Before`、`@..After` 则不会生效。

> Xpler 在 [FreedomPlus](https://github.com/GangJust/FreedomPlus)
> 中被很好的实践运用，如果你想要更多示例，请点击 [这里](https://github.com/GangJust/FreedomPlus/tree/master/core/src/main/java/io/github/fplus/core/hook)。
