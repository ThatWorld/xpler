package io.github.xpler.core.proxy

import android.util.Log
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

    val getStackTraceString: String
        get() = Log.getStackTraceString(Throwable("getStackTraceString"))

    override fun toString(): String {
        return JSONObject().let { json ->
            json.putOpt("method", "$method")
            json.putOpt(
                "thisObj", JSONObject()
                    .putOpt("value", "$thisObject")
                    .putOpt("type", "${thisObject?.javaClass?.name}")
            )
            json.putOpt("args", JSONArray().apply {
                args.forEachIndexed { index, any ->
                    put(
                        JSONObject()
                            .putOpt("index", index)
                            .putOpt("value", "$any")
                            .putOpt("type", "${any?.javaClass?.name}")
                    )
                }
            })
            json.putOpt(
                "throwable", JSONObject()
                    .putOpt("value", "$throwable")
                    .putOpt("type", "${throwable?.javaClass?.name}")
            )
            json.putOpt(
                "result", JSONObject()
                    .putOpt("value", "$result")
                    .putOpt("type", "${result?.javaClass?.name}")
            )
        }.toString()
    }
}