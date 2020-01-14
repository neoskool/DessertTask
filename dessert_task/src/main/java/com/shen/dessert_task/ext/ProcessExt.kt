package com.shen.dessert_task.ext


import android.app.ActivityManager
import android.content.Context
import android.os.Process
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 * created by shen on 2019/10/24
 * at 22:53
 **/
private var currentProcessName: String? = null

fun Context.isMainProcess(): Boolean {
    val processName = getCurrentProcessName()
    if (processName != null && processName.contains(":")) {
        return false
    }

    return (processName != null && processName == packageName)
}

fun Context.getCurrentProcessName(): String? {
    val processName = currentProcessName
    if (processName != null && processName.isNotEmpty()) {
        return processName
    }

    try {
        val pid = Process.myPid()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.runningAppProcesses.forEach {
            if (it.pid == pid) {
                currentProcessName = it.processName
                return currentProcessName
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    currentProcessName = getCurrentProcessNameFromPrc()
    return currentProcessName ?: ""
}

private fun getCurrentProcessNameFromPrc(): String? {
    var reader: BufferedReader? = null

    try {
        reader = BufferedReader(InputStreamReader(FileInputStream("/proc/${Process.myPid()}/cmdline"), "iso-8859-1"))
        val processName = StringBuilder()
        while (reader.read() > 0) {
            processName.append(reader.read())
        }
        return processName.toString()
    } catch (e: Throwable) {
        //ignore
    } finally {
        reader?.close()
    }

    return null
}
