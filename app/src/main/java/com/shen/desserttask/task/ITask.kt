package com.shen.desserttask.task

import com.shen.dessert_task.annotation.Task
import com.shen.dessert_task.annotation.TaskCallback
import com.shen.dessert_task.annotation.TaskConfig

/**
 *  created at 2020.2020/1/14.13:44
 *  @author shen
 */
interface ITask {
    @Task
    fun one()

    @Task
    @TaskConfig(dependOn = ["one"])
    fun two()

    @Task
    @TaskConfig(targetCallback = "callback", needCall = true)
    fun three()

    @TaskCallback("callback")
    fun threeCallback()
}