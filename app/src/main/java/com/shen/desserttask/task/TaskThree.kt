package com.shen.desserttask.task

import android.util.Log
import com.shen.dessert_task.DessertTask

/**
 *  created at 2020.2020/1/14.15:40
 *  @author shen
 */
class TaskThree : DessertTask() {
    override fun run() {
        Log.d("Wow Three", "start: ${Thread.currentThread().name}")
    }
}