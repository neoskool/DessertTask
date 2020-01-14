package com.shen.desserttask.task

import android.util.Log
import com.shen.dessert_task.DessertTask

/**
 *  created at 2020.2020/1/14.13:47
 *  @author shen
 */
class TaskTwo : DessertTask() {
    override fun run() {
        Log.d("two", "start: ${Thread.currentThread().name}")
    }
}