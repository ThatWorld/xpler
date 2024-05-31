package io.github.xpler.core.entity

/**
 * 当某个 HookEntity 未知时, 可以通过泛型该类进行占位。
 *
 * ```
 * class HMainActivity : HookEntity{
 *
 *  fun setTargetClass():Class<*>{
 *      return EmptyHook::class.java
 *  }
 *
 *  fun onInit(){
 *      // some hook logic..
 *  }
 * }
 * ```
 */
class EmptyHook private constructor()