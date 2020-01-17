package com.shen.desserttask.task

import android.util.Log

/**
 *  created at 2020.2020/1/14.13:45
 *  @author shen
 */
class TaskImpl : ITask {
    override fun one() {
        Log.d("Wow One", "start: ${Thread.currentThread().name}")
    }

    override fun two() {
        Log.d("Wow Two", "start: ${Thread.currentThread().name}")
    }

    override fun three() {
        Log.d("Wow Three", "start: ${Thread.currentThread().name}")
    }

    override fun threeCallback() {
        Log.d("Wow threeCallback", "start: ${Thread.currentThread().name}")
    }

    override fun threeTailRunnable() {
        Log.d("Wow threeTailRunnable", "start: ${Thread.currentThread().name}")
    }
}