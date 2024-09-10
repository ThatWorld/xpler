package io.github.xpler.core

import android.util.Log
import de.robv.android.xposed.XposedBridge

object XplerLog {
    private var mTag = "XplerLog"
    private var mIsXposed: Boolean = true

    private fun println(
        priority: Int,
        msg: String,
    ) {
        Log.println(priority, mTag, msg)

        if (!mIsXposed)
            return
        XposedBridge.log(msg)
    }

    fun setTag(tag: String) {
        mTag = tag
    }

    fun isXposed(b: Boolean) {
        mIsXposed = b
    }

    fun d(vararg msg: String) {
        msg.forEach { println(Log.DEBUG, it) }
    }

    fun i(vararg msg: String) {
        msg.forEach { println(Log.INFO, it) }
    }

    fun e(vararg err: String) {
        err.forEach { println(Log.ERROR, it) }
    }

    fun e(th: Throwable) {
        println(Log.ERROR, th.stackTraceToString())
    }

    fun stackLog() {
        d(Log.getStackTraceString(Throwable("Stack trace")))
    }
}