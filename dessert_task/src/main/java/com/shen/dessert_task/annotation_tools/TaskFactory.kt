package com.shen.dessert_task.annotation_tools

import android.util.Log
import com.shen.dessert_task.DessertTask
import com.shen.dessert_task.annotation.*
import com.shen.dessert_task.easyTask
import com.shen.dessert_task.getCPUExecute
import com.shen.dessert_task.getIOExecute
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ExecutorService

class TaskFactory(private val builder: Builder) {

    internal val type: Builder.FactoryType
        get() = builder.annotationType

    internal val task: DessertTask?
        get() = builder.methodTask

    internal val taskConfig: TaskConfigModel?
        get() = builder.methodTaskConfig

    internal val tailRunnable: Runnable?
        get() = builder.methodRunnable

    internal val tailRunnableName: String
        get() = builder.tailRunnableName

    internal val callback: (() -> Unit)?
        get() = builder.methodCallback

    internal val callbackName: String
        get() = builder.targetCallbackName

    companion object {
        @JvmStatic
        fun parseAnnotations(convert: AnnotationConvertTools, method: Method, args: Array<Any>): TaskFactory = Builder(convert, method, args).build()

        class Builder(private val convert: AnnotationConvertTools, private val method: Method, private val args: Array<Any>) {
            private val methodAnnotations: Array<Annotation> = method.annotations
            internal var annotationType: FactoryType = FactoryType.TASK

            internal var methodTask: DessertTask? = null
            internal var methodTaskConfig: TaskConfigModel? = null

            internal var methodRunnable: Runnable? = null
            internal var methodCallback: (() -> Unit)? = null

            internal var tailRunnableName: String = ""
            internal var targetCallbackName: String = ""

            private var taskConfig: TaskConfig? = null


            fun build(): TaskFactory {
                methodAnnotations.forEach {
                    parseMethodAnnotation(it)
                }

                return TaskFactory(this)
            }

            private fun parseMethodAnnotation(annotation: Annotation) {
                when(annotation) {
                    is Task -> {
                        parseTaskAnnotation()
                        annotationType = FactoryType.TASK
                    }

                    is TaskCallback -> {
                        parseTaskCallback(annotation)
                        annotationType = FactoryType.CALLBACK
                    }

                    is TaskTailRunnable -> {
                        parseTaskTailRunnable(annotation)
                        annotationType = FactoryType.TAIL_RUNNABLE
                    }
                }
            }

            private fun parseTaskAnnotation() {

                for (annotation in method.annotations) {
                    if (annotation is TaskConfig) {
                        taskConfig = annotation
                        break
                    }
                }

                val dessertTask = if (taskConfig == null) {
                    EasyCreateTask()
                } else {
                    FactoryCreateTask()
                }

                methodTask = dessertTask
                methodTaskConfig = TaskConfigModel(
                    methodName = method.name,
                    priority = dessertTask.priority(),
                    needRunAsSoon = dessertTask.needRunAsSoon(),
                    runOnExecute = taskConfig?.runOnExecute ?: Executors.IO,
                    needWait = dessertTask.needWait,
                    dependOn = taskConfig?.dependOn ?: emptyArray(),
                    runOnMainThread = dessertTask.runOnMainThread,
                    needCall = dessertTask.needCall,
                    onlyInMainProcess = dessertTask.onlyInMainProcess,
                    targetCallback = taskConfig?.targetCallback ?: "",
                    tailRunnable = taskConfig?.tailRunnable ?: ""
                )
            }

            private fun parseTaskCallback(annotation: TaskCallback) {
                targetCallbackName = annotation.name

                methodCallback = {
                    invokeMethod()
                }
            }

            private fun parseTaskTailRunnable(annotation: TaskTailRunnable) {
                tailRunnableName = annotation.name

                methodRunnable = Runnable {
                    invokeMethod()
                }
            }

            internal inner class EasyCreateTask : DessertTask() {
                override val methodName: String
                    get() = this@Builder.method.name

                override fun run() {
                    invokeMethod()
                }
            }

            internal inner class FactoryCreateTask : DessertTask() {

                override val methodName: String
                    get() = this@Builder.method.name

                override fun needRunAsSoon(): Boolean = taskConfig!!.needRunAsSoon

                override fun priority(): Int = taskConfig!!.priority

                override val runOn: ExecutorService = if (taskConfig!!.runOnExecute == Executors.IO) getIOExecute() else getCPUExecute()

                override val needWait: Boolean = taskConfig!!.needWait

                override val runOnMainThread: Boolean = taskConfig!!.runOnMainThread

                override var needCall: Boolean = taskConfig!!.needCall

                override val onlyInMainProcess: Boolean = taskConfig!!.onlyInMainProcess

                override fun run() {
                    invokeMethod()
                }
            }

            private fun invokeMethod() {
                if (args.isEmpty()) {
                    method.invoke(convert.createCache)
                } else {
                    method.invoke(convert.createCache, args)
                }
            }

            internal enum class FactoryType {
                TASK, CALLBACK, TAIL_RUNNABLE;
            }
        }
    }
}

data class TaskConfigModel(
    val methodName: String,
    val priority: Int,
    val needRunAsSoon: Boolean,
    val runOnExecute: String,
    val needWait: Boolean,
    val dependOn: Array<String>,
    val runOnMainThread: Boolean,
    val needCall: Boolean,
    val onlyInMainProcess: Boolean,
    val targetCallback: String,
    val tailRunnable: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskConfigModel

        if (priority != other.priority) return false
        if (needRunAsSoon != other.needRunAsSoon) return false
        if (runOnExecute != other.runOnExecute) return false
        if (needWait != other.needWait) return false
        if (!dependOn.contentEquals(other.dependOn)) return false
        if (runOnMainThread != other.runOnMainThread) return false
        if (needCall != other.needCall) return false
        if (onlyInMainProcess != other.onlyInMainProcess) return false
        if (targetCallback != other.targetCallback) return false
        if (tailRunnable != other.tailRunnable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = priority
        result = 31 * result + needRunAsSoon.hashCode()
        result = 31 * result + runOnExecute.hashCode()
        result = 31 * result + needWait.hashCode()
        result = 31 * result + dependOn.contentHashCode()
        result = 31 * result + runOnMainThread.hashCode()
        result = 31 * result + needCall.hashCode()
        result = 31 * result + onlyInMainProcess.hashCode()
        result = 31 * result + targetCallback.hashCode()
        result = 31 * result + tailRunnable.hashCode()
        return result
    }
}