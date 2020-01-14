package com.shen.desserttask.task

import android.util.Log

/**
 *  created at 2020.2020/1/14.13:45
 *  @author shen
 */
class TaskImpl : ITask {
    override fun one() {
        Log.d("one", "start: ${Thread.currentThread().name}")
    }

    override fun two() {
        Log.d("two", "start: ${Thread.currentThread().name}")
    }

    override fun three() {
        Log.d("three", "start: ${Thread.currentThread().name}")
    }

    override fun threeCallback() {
        Log.d("threeCallback", "start: ${Thread.currentThread().name}")
    }
}