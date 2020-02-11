package com.shen.dessert_task

import android.os.Looper
import android.os.MessageQueue
import com.shen.dessert_task.annotation_tools.AnnotationConvertTools
import java.util.*

/**
 * created by shen on 2019/10/27
 * at 21:21
 **/
class DelayDessertDispatcher {
    private val delayTasks: Queue<DessertTask> by lazy { LinkedList<DessertTask>() }

    var interfaceCreate = false

    private val idleHandler = MessageQueue.IdleHandler {
        if (delayTasks.size > 0) {
            val task = delayTasks.poll()
            task?.let {
                DessertDispatchRunnable(task)
            }
        }

        !delayTasks.isEmpty()
    }

    fun addTask(task: DessertTask): DelayDessertDispatcher {
        delayTasks.add(task)
        return this
    }

    fun <T> create(interfaceObj : Class<T>, interfaceImpl: T): DelayDessertDispatcher {
        AnnotationConvertTools.instance
            .dispatcher(this)
            .create(interfaceObj, interfaceImpl).also {
                this.interfaceCreate = true
            }
        return this
    }

    fun start() {
        if (interfaceCreate) {
            AnnotationConvertTools.instance.autoAdd(delayTasks.toMutableList())
        }

        Looper.myQueue().addIdleHandler(idleHandler)
    }
}