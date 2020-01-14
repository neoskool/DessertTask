package com.shen.dessert_task.state

import com.shen.dessert_task.utils.DebugLog
import java.util.concurrent.atomic.AtomicInteger

/**
 * created by shen on 2019/10/27
 * at 19:57
 **/
@Volatile var currentSituation: String = ""
    set(value) {
        if (!openLaunchState) {
            return
        }
        DebugLog.logD("currentSituation", value)
        field = value
        setLaunchState()
    }

private val stateDatas: MutableList<DessertTaskStateData> by lazy { mutableListOf<DessertTaskStateData>() }
private var taskDoneCount = AtomicInteger()
private var openLaunchState = false

fun markTaskDone() {
    taskDoneCount.getAndIncrement()
}

fun setLaunchState() {
    val data = DessertTaskStateData(situation = currentSituation, count = taskDoneCount.get())
    stateDatas.add(data)
    taskDoneCount = AtomicInteger(0)
}

data class DessertTaskStateData(
    var situation: String,
    var count: Int
)