package com.shen.desserttask

import android.app.Application
import android.util.Log
import com.shen.dessert_task.DessertDispatcher
import com.shen.dessert_task.DessertTask
import com.shen.dessert_task.annotation_tools.AnnotationConvertTools
import com.shen.dessert_task.easyTask
import com.shen.desserttask.task.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DessertDispatcher.init(this)
            .build()
            .create(ITask::class.java, TaskImpl())
            .addTask(TaskOne())
            .addTask(easyTask {
                Log.d("EasyTask wow", "Start: ${Thread.currentThread().name}")
            })
            .start()

        //Method 1
//        DessertDispatcher.getInstance()
//            .create(ITask::class.java, TaskImpl())
//            .addTask(TaskOne())
//            .start()

        //Method 2
//        DessertDispatcher.getInstance()
//            .addTask(TaskOne())
//            .addTask(TaskTwo())
//            .addTask(TaskThree())
//            .start()

        //Method 3
//        DessertDispatcher.getInstance()
//            .addTask(easyTask {
//                Log.d("one", "start: ${Thread.currentThread().name}")
//            })
//            .addTask(easyTask {
//                Log.d("two", "start: ${Thread.currentThread().name}")
//            })
//            .start()
    }
}