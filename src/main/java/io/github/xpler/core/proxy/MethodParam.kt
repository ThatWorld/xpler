package io.github.xpler.core.proxy

import de.robv.android.xposed.XC_MethodHook
import org.json.JSONObject
import java.lang.reflect.Member
import kotlin.jvm.Throws

class MethodParam private constructor(
    private val param: XC_MethodHook.MethodHookParam,
) {
    val method: Member
        get() = param.method

    val thisObject: Any?
        get() = param.thisObject

    val args: Array<Any?>
        get() = param.args

    val result: Any?
        get() = param.result

    val hasThrowable: Boolean
        get() = param.hasThrowable()

    val throwable: Throwable?
        get() = param.throwable

    val resultOrThrowable: Any?
        @Throws(Throwable::class)
        get() = param.resultOrThrowable

    fun setMethod(member: Member) {
        param.method = member
    }

    fun setThisObject(value: Any?) {
        param.thisObject = value
    }

    fun setArgs(args: Array<Any?>) {
        param.args = args
    }

    fun setResult(result: Any?) {
        param.result = result
    }

    fun setResultVoid() {
        param.result = Void.TYPE
    }

    fun setThrowable(th: Throwable?) {
        param.throwable = th
    }

    override fun toString(): String {
        return JSONObject()
            .apply {
                putOpt("method", "$method")
                putOpt("thisObject", "$thisObject")
                putOpt("args", args.joinToString { "${it?.javaClass?.name}=${it}" })
                putOpt("hasThrowable", "$hasThrowable")
                putOpt("resultOrThrowable", "$resultOrThrowable")
            }
            .toString(2)
    }
}