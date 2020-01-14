package com.shen.dessert_task.utils

import android.util.Log

object DebugLog {
    var isDebug = true
    fun logE(tag: String?, value: String?) {
        if (!isDebug) {
            return
        } else {
            Log.e(tag, value)
        }
    }

    fun logD(tag: String?, value: String?) {
        if (!isDebug) {
            return
        } else {
            Log.d(tag, value)
        }
    }

    fun logI(tag: String?, value: String?) {
        if (!isDebug) {
            return
        } else {
            Log.i(tag, value)
        }
    }

    fun logW(tag: String?, value: String?) {
        if (!isDebug) {
            return
        } else {
            Log.w(tag, value)
        }
    }

    fun logE(tag: String?, value: Int) {
        if (!isDebug) {
            return
        } else {
            Log.e(tag, value.toString())
        }
    }

    fun logD(tag: String?, value: Int) {
        if (!isDebug) {
            return
        } else {
            Log.d(tag, value.toString())
        }
    }

    fun logI(tag: String?, value: Int) {
        if (!isDebug) {
            return
        } else {
            Log.i(tag, value.toString())
        }
    }

    fun logW(tag: String?, value: Int) {
        if (!isDebug) {
            return
        } else {
            Log.w(tag, value.toString())
        }
    }

    fun logE(tag: String?, value: Long) {
        if (!isDebug) {
            return
        } else {
            Log.e(tag, value.toString())
        }
    }

    fun logD(tag: String?, value: Long) {
        if (!isDebug) {
            return
        } else {
            Log.d(tag, value.toString())
        }
    }

    fun logI(tag: String?, value: Long) {
        if (!isDebug) {
            return
        } else {
            Log.i(tag, value.toString())
        }
    }

    fun logW(tag: String?, value: Long) {
        if (!isDebug) {
            return
        } else {
            Log.w(tag, value.toString())
        }
    }
}