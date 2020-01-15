package com.shen.dessert_task.annotation_tools

import com.shen.dessert_task.DelayDessertDispatcher
import com.shen.dessert_task.DessertDispatcher
import com.shen.dessert_task.DessertTask
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 *  created by shen
 *  at 2019.2019/12/30.17:07
 *  @author shen
 */

class AnnotationConvertTools private constructor () {

    companion object {
        val instance by lazy { AnnotationConvertTools() }

        fun <T> validateServiceInterface(service: Class<T>) {
            require(service.isInterface) { "Task declarations must be interfaces." }
            require(service.interfaces.isEmpty()) { "Task interfaces must not extend other interfaces." }
        }
    }

    private val serviceMethodCache: MutableMap<Method, DessertMethod<*>> = ConcurrentHashMap()
    private var dispatcherNormal: DessertDispatcher? = null
    private var dispatcherDelay: DelayDessertDispatcher? = null
    internal lateinit var createCache: Any

    @Suppress("UNCHECKED_CAST")
    fun <T> create(taskObj: Class<T>, taskObjImpl: T) {
        validateServiceInterface(taskObj)

        taskObj.methods.forEach {
            loadTaskMethod(it, emptyArray())
        }
        require(taskObjImpl != null) {
            "The Interface Implement must not be null"
        }
        createCache = taskObjImpl
    }


    fun dispatcher(dispatcher: DessertDispatcher): AnnotationConvertTools {
        dispatcherNormal = dispatcher
        return this
    }

    fun dispatcher(dispatcher: DelayDessertDispatcher): AnnotationConvertTools {
        dispatcherDelay = dispatcher
        return this
    }

    private fun loadTaskMethod(method: Method, args: Array<Any>): DessertMethod<*> {
        var serviceMethod = serviceMethodCache[method]

        serviceMethod?.let { return it }

        synchronized(serviceMethodCache) {
            serviceMethod = serviceMethodCache[method] ?: DessertMethod.parseAnnotations<Any>(this, method, args).also {
                serviceMethodCache[method] = it
            }
        }

        return serviceMethod!!
    }

    internal fun autoAdd(allTask: MutableList<DessertTask>) {
        if (serviceMethodCache.isEmpty()) {
            return
        }

        serviceMethodCache.values.forEach {
            add(it, allTask)
        }
    }

    private fun add(serviceMethod: DessertMethod<*>, allTask: MutableList<DessertTask>) {
        val cacheMethods = serviceMethodCache.values.toList()

        if (cacheMethods.isEmpty()) {
            return
        }

        serviceMethod.run {
            if (taskFactory.type == TaskFactory.Companion.Builder.FactoryType.TASK) {
                addDependOn(allTask)
                addDependOnByName(cacheMethods, allTask)
                addTailRunnable(cacheMethods)
                addCallback(cacheMethods)

                dispatcherNormal?.addTask(taskFactory.task)
                dispatcherDelay?.addTask(taskFactory.task ?: throw IllegalArgumentException("Can't find task by $serviceMethod"))
            }
        }
    }
}