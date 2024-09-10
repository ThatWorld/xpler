package io.github.xpler.core.entity

import io.github.xpler.core.proxy.MethodParam

interface CallMethods {
    /**
     * 该方法会在Hook目标类所有成员方法调用之前，都被执行
     *
     * @param params MethodParam
     */
    fun callOnBeforeMethods(params: MethodParam)

    /**
     * 该方法会在Hook目标类所有成员方法调用之后，都被执行
     *
     * @param params MethodParam
     */
    fun callOnAfterMethods(params: MethodParam)
}