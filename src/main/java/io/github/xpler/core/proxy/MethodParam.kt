package io.github.xpler.core.proxy

import de.robv.android.xposed.XC_MethodHook
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Member

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
        return JSONObject().apply {
            putOpt("method", "$method")
            putOpt("thisObj", JSONObject().apply {
                putOpt("value", "$thisObject")
                putOpt("type", "${thisObject?.javaClass?.name}")
            })
            putOpt("args", JSONArray().apply {
                args.forEachIndexed { index, any ->
                    put(JSONObject().apply {
                        putOpt("index", index)
                        putOpt("value", "$any")
                        putOpt("type", "${any?.javaClass?.name}")
                    })
                }
            })
            putOpt("throwable", JSONObject().apply {
                putOpt("value", "$throwable")
                putOpt("type", "${throwable?.javaClass?.name}")
            })
            putOpt("result", JSONObject().apply {
                putOpt("value", "$result")
                putOpt("type", "${result?.javaClass?.name}")
            })
        }.toString(2)
    }
}