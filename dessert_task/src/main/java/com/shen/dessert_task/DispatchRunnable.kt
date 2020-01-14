package com.shen.dessert_task

import android.os.Looper
import android.os.Process
import androidx.core.os.TraceCompat
import com.shen.dessert_task.state.currentSituation
import com.shen.dessert_task.state.markTaskDone
import com.shen.dessert_task.utils.DebugLog


/**
 * created by shen on 2019/10/27
 * at 20:09
 **/
class DessertDispatchRunnable : Runnable {

    private var task: DessertTask
    private var dispatcher: DessertDispatcher? = null

    constructor(task: DessertTask): this(task, null)

    constructor(task: DessertTask, dispatcher: DessertDispatcher?) {
        this.task = task
        this.dispatcher = dispatcher
    }

    override fun run() {
        TraceCompat.beginSection(task.javaClass.simpleName)
        DebugLog.logD(task.javaClass.simpleName, "begin run situation $currentSituation" )
        Process.setThreadPriority(task.priority())

        var startTime = System.currentTimeMillis()
        task.isWaiting = true
        task.waitToSatisfy()

        val waitTime = System.currentTimeMillis() - startTime
        startTime = System.currentTimeMillis()

        //task 开始执行
        task.isRunning = true
        task.run()

        val tailRunnable = task.tailRunnable
        tailRunnable?.run()

        if (task.needCall) {
            task.callback?.invoke()
            DebugLog.logD(task.javaClass.simpleName, "Callback finish")
            task.needCall = false
        }


        if (!task.needCall || task.runOnMainThread) {
            printTaskLog(startTime, waitTime)

            markTaskDone()
            task.isFinish = true

            dispatcher?.run {
                DebugLog.logE("Satisfy Task", task.toString())
                task.satisfyChildren()
                task.makeTaskDone()
            }
            DebugLog.logD(task.javaClass.simpleName, "finish")
        }
        TraceCompat.endSection()
    }

    private fun printTaskLog(startTime: Long, waitTime: Long) {
        val runTime = System.currentTimeMillis() - startTime
        if (DebugLog.isDebug) {
            DebugLog.logD(task.javaClass.simpleName, " wait " + waitTime + " run "
                        + runTime + " isMain " + (Looper.getMainLooper() == Looper.myLooper())
                        + "  needWait " + (task.needWait || Looper.getMainLooper() == Looper.myLooper())
                        + "  ThreadId " + Thread.currentThread().id
                        + "  ThreadName " + Thread.currentThread().name
                        + "  Situation  " + currentSituation
            )
        }
    }
}