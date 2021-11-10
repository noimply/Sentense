package net.noimply.sentence.business.support

import android.util.Log
import net.noimply.sentence.business.TAG
import net.noimply.sentence.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter

object Logger {
    fun error(throwable: Throwable? = null, logMessage: () -> String) {
        if (BuildConfig.X_BUILD_ALLOW_LOG) {
            Log.e(TAG, makeLog(throwable, logMessage))
        }
    }

    fun debug(throwable: Throwable? = null, logMessage: () -> String) {
        if (BuildConfig.X_BUILD_ALLOW_LOG) {
            Log.d(TAG, makeLog(throwable, logMessage))
        }
    }

    fun info(throwable: Throwable? = null, logMessage: () -> String) {
        if (BuildConfig.X_BUILD_ALLOW_LOG) {
            Log.i(TAG, makeLog(throwable, logMessage))
        }
    }

    fun verbose(throwable: Throwable? = null, logMessage: () -> String) {
        if (BuildConfig.X_BUILD_ALLOW_LOG) {
            Log.v(TAG, makeLog(throwable, logMessage))
        }
    }

    private fun makeLog(throwable: Throwable? = null, trace: () -> String): String {
        val builder = StringBuilder()
        throwable?.let {
            builder.appendLine("")
            builder.appendLine("Log =>")
            builder.appendLine("[${trace()}]")
            builder.appendLine("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            builder.appendLine("StackTrace =>")
            builder.appendLine("[${getStackTraceString(it)}]")
            builder.appendLine("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        } ?: run {
            builder.append("[Log => ${trace()}]")
        }
        return builder.toString()
    }

    private fun getStackTraceString(t: Throwable): String {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}