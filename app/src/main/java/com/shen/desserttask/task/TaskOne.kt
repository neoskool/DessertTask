package com.shen.desserttask.task

import android.util.Log
import com.shen.dessert_task.DessertTask

/**
 *  created at 2020.2020/1/14.13:46
 *  @author shen
 */
class TaskOne : DessertTask() {
    override val dependOnByName: MutableList<String> = mutableListOf("one")

    override fun run() {
        Log.d("Wow One", "start: ${Thread.currentThread().name}")
    }
}