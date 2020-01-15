package com.shen.dessert_task.sort

import com.shen.dessert_task.DessertTask
import com.shen.dessert_task.utils.DebugLog
import org.jetbrains.annotations.Contract

/**
 * created by shen on 2019/10/27
 * at 17:31
 **/

private val newTaskHigh by lazy { arrayListOf<DessertTask>() }

@Contract(pure = true)
fun getTaskHigh() = newTaskHigh

@Synchronized
fun getSortResult(
    originTask: List<DessertTask>,
    clsLaunchTask: List<Class<out DessertTask>>
): List<DessertTask> {
    val makeTime = System.currentTimeMillis()

    val dependSet: MutableSet<Int> = mutableSetOf()
    val graph = Graph(originTask.size)
    originTask.forEachIndexed { index, dessertTask ->
        if (dessertTask.isSend || (dessertTask.dependOn.isNullOrEmpty() && dessertTask.dependOnByName.isNullOrEmpty())) {
            return@forEachIndexed
        }

        dessertTask.dependOn.forEach {
            val indexOfDepend = getIndexOfTask(originTask, clsLaunchTask, it)
            check(indexOfDepend >= 0) { " depends on ${it.simpleName} can not be found in task list at $indexOfDepend" }
            dependSet.add(indexOfDepend)
            graph.addEdge(indexOfDepend, index)
        }

        dessertTask.dependOnByName.forEach {
            val indexOfDepend = getIndexOfTask(originTask, it) ?: return@forEach
            check(indexOfDepend >= 0) { " depends on $it can not be found in task list at $indexOfDepend" }
            dependSet.add(indexOfDepend)
            graph.addEdge(indexOfDepend, index)
        }
    }

    val indexList = graph.topoLogicSort()
    val newTaskAll = getResultTasks(originTask, dependSet, indexList)
    DebugLog.logD("task analyse duration makeTime", System.currentTimeMillis() - makeTime)

    newTaskAll.printAllTaskName()
    return newTaskAll
}

private fun getResultTasks(
    originTask: List<DessertTask>,
    dependOn: MutableSet<Int>,
    indexList: List<Int>
): List<DessertTask> {
    val newTaskAll = ArrayList<DessertTask>(originTask.size)
    val newTaskDepended = arrayListOf<DessertTask>()
    val newTaskWithOutDepended = arrayListOf<DessertTask>()
    val newTaskRunAsSoon = arrayListOf<DessertTask>()

    indexList.forEach {
        if (dependOn.contains(it))
            newTaskDepended.add(originTask[it])
        else {
            val dessertTask = originTask[it]
            if (dessertTask.needRunAsSoon())
                newTaskRunAsSoon.add(dessertTask)
            else
                newTaskWithOutDepended.add(dessertTask)
        }
    }

    newTaskHigh.apply {
        addAll(newTaskDepended)
        addAll(newTaskRunAsSoon)
    }

    return newTaskAll.apply {
        addAll(newTaskHigh)
        addAll(newTaskWithOutDepended)
    }
}

private fun getIndexOfTask(
    originTask: List<DessertTask>,
    clsLaunchTask: List<Class<out DessertTask>>,
    cls: Class<*>
): Int {
    val index = clsLaunchTask.indexOf(cls)
    if (index >= 0) return index

    originTask.forEachIndexed { position, dessertTask ->
        if (cls.simpleName == dessertTask.javaClass.simpleName) {
            return position
        }
    }

    return index
}

private fun getIndexOfTask(
    originTask: List<DessertTask>,
    methodName: String
) : Int? {
    originTask.forEachIndexed { position, dessertTask ->
        if (methodName == dessertTask.methodName || methodName == dessertTask::class.java.simpleName) {
            return position
        }
    }

    return null
}

private fun List<DessertTask>.printAllTaskName() {
    if (!DebugLog.isDebug) {
        return
    }

    forEach {
        DebugLog.logD("Task name", it.javaClass.simpleName)
    }
}