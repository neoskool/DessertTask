package com.shen.dessert_task.annotation_tools

/**
 *  created by shen
 *  at 2020.2020/1/2.10:08
 *  @author shen
 */
class DessertMethodImpl <T> (taskFactory: TaskFactory) : DessertMethod <T> (taskFactory) {
    override fun addDependOn(tasksMethod: List<DessertMethod<*>>) {
        if (taskFactory.type != TaskFactory.Companion.Builder.FactoryType.TASK) {
            return
        }

        if (taskFactory.task == null || taskFactory.taskConfig == null) {
            return
        }

        if (taskFactory.taskConfig!!.dependOn.isEmpty()) {
            return
        }

        tasksMethod.forEach {
            if (it.taskFactory.task != null && it.taskFactory.taskConfig != null) {
                if (taskFactory.taskConfig!!.dependOn.contains(it.taskFactory.taskConfig?.methodName)) {
                    taskFactory.task!!.dependOn.add(it.taskFactory.task!!.javaClass)
                }
            }
        }

    }

    override fun addTailRunnable(tasksMethod: List<DessertMethod<*>>) {
        if (taskFactory.type != TaskFactory.Companion.Builder.FactoryType.TASK) {
            return
        }

        if (taskFactory.task == null || taskFactory.taskConfig == null) {
            return
        }

        if (taskFactory.taskConfig!!.tailRunnable.isEmpty()) {
            return
        }

        tasksMethod.forEach {
            if (it.taskFactory.tailRunnable != null && it.taskFactory.tailRunnableName.isNotEmpty()) {
                if (taskFactory.taskConfig!!.tailRunnable == it.taskFactory.tailRunnableName) {
                    taskFactory.task!!.tailRunnable = it.taskFactory.tailRunnable
                    return@forEach
                }
            }
        }
    }

    override fun addCallback(tasksMethod: List<DessertMethod<*>>) {
        if (taskFactory.type != TaskFactory.Companion.Builder.FactoryType.TASK) {
            return
        }

        if (taskFactory.task == null || taskFactory.taskConfig == null) {
            return
        }

        if (!taskFactory.taskConfig!!.needCall || taskFactory.taskConfig!!.targetCallback.isEmpty()) {
            return
        }

        tasksMethod.forEach {
            if (it.taskFactory.callback != null && it.taskFactory.callbackName.isNotEmpty()) {
                if (taskFactory.taskConfig!!.targetCallback == it.taskFactory.callbackName) {
                    taskFactory.task!!.callback = it.taskFactory.callback
                }
            }
        }
    }
}