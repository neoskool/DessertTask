package com.shen.dessert_task.annotation_tools

import com.shen.dessert_task.DessertTask
import java.lang.reflect.Method

/**
 *  created by shen
 *  at 2019.2019/12/30.17:22
 *  @author shen
 */
abstract class DessertMethod <T> (val taskFactory: TaskFactory) {
    companion object {
        fun <T> parseAnnotations(convert: AnnotationConvertTools, method: Method, args: Array<Any>): DessertMethod<T> {
            val taskFactory = TaskFactory.parseAnnotations(convert, method, args)

            return DessertMethodImpl(taskFactory)
        }
    }

    abstract fun addDependOn(allTask: MutableList<DessertTask>)

    abstract fun addDependOnByName(tasksMethod: List<DessertMethod<*>>, allTask: MutableList<DessertTask>)

    abstract fun addTailRunnable(tasksMethod: List<DessertMethod<*>>)

    abstract fun addCallback(tasksMethod: List<DessertMethod<*>>)
}