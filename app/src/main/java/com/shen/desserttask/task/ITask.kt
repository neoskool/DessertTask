package com.shen.desserttask.task

import com.shen.dessert_task.annotation.Task
import com.shen.dessert_task.annotation.TaskCallback
import com.shen.dessert_task.annotation.TaskConfig
import com.shen.dessert_task.annotation.TaskTailRunnable

/**
 *  created at 2020.2020/1/14.13:44
 *  @author shen
 */
interface ITask {
    @Task
    fun one()

    @Task
    @TaskConfig(dependOn = ["one", "three", "TaskOne"])
    fun two()

    @Task
    @TaskConfig(targetCallback = "callback", needCall = true, tailRunnable = "threeTailRunnable")
    fun three()

    @TaskCallback("callback")
    fun threeCallback()

    @TaskTailRunnable("threeTailRunnable")
    fun threeTailRunnable()
}