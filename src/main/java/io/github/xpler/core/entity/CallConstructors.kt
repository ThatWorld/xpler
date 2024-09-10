package io.github.xpler.core.entity

import io.github.xpler.core.proxy.MethodParam

interface CallConstructors {
    /**
     * 该方法会在Hook目标类所有构造方法调用之前，都被执行
     *
     * @param params MethodParam
     */
    fun callOnBeforeConstructors(params: MethodParam)

    /**
     * 该方法会在Hook目标类所有构造方法调用之后，都被执行
     *
     * @param params MethodParam
     */
    fun callOnAfterConstructors(params: MethodParam)
}