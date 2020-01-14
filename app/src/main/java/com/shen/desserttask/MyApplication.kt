package com.shen.desserttask

import android.app.Application
import android.util.Log
import com.shen.dessert_task.DessertDispatcher
import com.shen.dessert_task.easyTask
import com.shen.desserttask.task.ITask
import com.shen.desserttask.task.TaskImpl
import com.shen.desserttask.task.TaskOne
import com.shen.desserttask.task.TaskTwo

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DessertDispatcher.init(this)

        //Method 1
        DessertDispatcher.getInstance()
            .create(ITask::class.java, TaskImpl())
            .start()

        //Method 2
//        DessertDispatcher.getInstance()
//            .addTask(TaskOne())
//            .addTask(TaskTwo())

        //Method 3
//        DessertDispatcher.getInstance()
//            .addTask(easyTask {
//                Log.d("one", "start: ${Thread.currentThread().name}")
//            })
//            .addTask(easyTask {
//                Log.d("two", "start: ${Thread.currentThread().name}")
//            })
    }
}