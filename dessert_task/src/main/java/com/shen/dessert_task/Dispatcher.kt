package com.shen.dessert_task

import android.content.Context
import android.os.Looper
import androidx.annotation.UiThread
import com.shen.dessert_task.annotation.Task
import com.shen.dessert_task.annotation_tools.AnnotationConvertTools
import com.shen.dessert_task.annotation_tools.TaskFactory
import com.shen.dessert_task.ext.isMainProcess
import com.shen.dessert_task.sort.getSortResult
import com.shen.dessert_task.state.markTaskDone
import com.shen.dessert_task.utils.DebugLog
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

/**
 * created by shen on 2019/10/24
 * at 22:30
 **/
class DessertDispatcher {

    private var startTime = 0L

    private val futures: MutableList<Future<*>> by lazy { mutableListOf<Future<*>>() }

    private var allTasks = mutableListOf<DessertTask>()

    private val dependOnTasks by lazy { mutableListOf<Class<out DessertTask>>() }

    private val dependOnTasksByName by lazy { mutableListOf<String>() }

    @Volatile
    private var mainThreadTask: MutableList<DessertTask> = mutableListOf()

    private var countDownLatch: CountDownLatch? = null

    ///需要的等待的任务数
    private val needWaitCount by lazy { AtomicInteger() }

    private val needWaitTasks by lazy { mutableListOf<DessertTask>() }

    private val needCallTasks by lazy { mutableListOf<DessertTask>() }

    ///已经结束的 Task
    @Volatile
    private var finishTasks: ArrayList<Class<out DessertTask>> = ArrayList(100)

    private val dependedHasMap by lazy { hashMapOf<Class<out DessertTask>?, ArrayList<DessertTask>>() }

    private val dependedNameHashMap by lazy { hashMapOf<String, ArrayList<DessertTask>>() }

    private var interfaceCreate: Boolean = false

    /**
     * 启动器分析的次数，统计下分析的耗时；
     */
    private val analyseCount = AtomicInteger()

    fun addTask(task: DessertTask?): DessertDispatcher {
        task?.let {
            it.collectDepends()
            allTasks.add(it)

            if (task is TaskFactory.Companion.Builder.EasyCreateTask || task is TaskFactory.Companion.Builder.FactoryCreateTask) {
                dependOnTasksByName.add(task.methodName)
            } else {
                dependOnTasks.add(task.javaClass)
            }

            it.ifNeedWait {
                needWaitTasks.add(this)
                needWaitCount.getAndIncrement()
            }

            it.ifNeedCall {
                needCallTasks.add(this)
            }
        }

        return this
    }

    fun <T> create(
        interfaceObj : Class<T>,
        interfaceObjImpl: T
    ) : DessertDispatcher {
        AnnotationConvertTools.instance
            .dispatcher(this)
            .create(interfaceObj, interfaceObjImpl).also {
                this.interfaceCreate = true
            }
        return this
    }


    @UiThread
    fun start() {
        if (interfaceCreate) {
            AnnotationConvertTools.instance.autoAdd(allTasks)
        }

        startTime = System.currentTimeMillis()
        require(Looper.getMainLooper() == Looper.myLooper()) { "must be called from UiThread" }

        if (allTasks.isNotEmpty()) {
            analyseCount.getAndIncrement()
            printDependedMsg()
            allTasks = getSortResult(allTasks, dependOnTasks) as MutableList<DessertTask>
            printSortResultMsg()
            countDownLatch = CountDownLatch(needWaitCount.get())

            sendAndExecuteAsyncTask()
            DebugLog.logD("task analyse duration", "${System.currentTimeMillis() - startTime} begin main ")
            executeTaskMain()
        }
        DebugLog.logD("task analyse duration startTime cost ", System.currentTimeMillis() - startTime)
    }

    fun cancel() {
        futures.forEach {
            it.cancel(true)
        }
    }

    fun await() {
        try {
            if (DebugLog.isDebug) {
                DebugLog.logD("still has", needWaitCount.get())
                needWaitTasks.forEach {
                    DebugLog.logD("needWait", it.javaClass.simpleName)
                }
            }

            if (needWaitCount.get() > 0) {
                require(countDownLatch != null) { "You have to call start() before call await()" }
                countDownLatch?.await(WAITING_TIME.toLong(), TimeUnit.MILLISECONDS)
            }
        } catch (e: Throwable) {
            //ignore
        }
    }

    fun wakeAll() {
        needCallTasks.forEach {
            it.sendTaskReal()
        }
    }

    fun wake(name: String) {
        needCallTasks.filter { it.methodName == name }
            .forEach { it.sendTaskReal() }
    }

    fun <T> wake(clazz: Class<T>) {
        needCallTasks.filter { it.javaClass == clazz }
            .forEach { it.sendTaskReal() }
    }

    /**
     * 通知Children一个前置任务已完成
     */
    internal fun DessertTask.satisfyChildren() {
        if (this is TaskFactory.Companion.Builder.EasyCreateTask || this is TaskFactory.Companion.Builder.FactoryCreateTask) {
            val arrayDepend = dependedNameHashMap[this.methodName]
            arrayDepend?.forEach {
                it.satisfy()
            }
        }

        val arrayDepended = dependedHasMap[javaClass]
        arrayDepended?.forEach { it.satisfy() }
    }

    internal fun DessertTask.executeTask() {
        ifNeedWait {
            needWaitCount.getAndIncrement()
        }

        runOn.execute(DessertDispatchRunnable(this, this@DessertDispatcher))
    }

    internal fun DessertTask.makeTaskDone() {
        ifNeedWait {
            finishTasks.add(javaClass)
            needWaitTasks.remove(this)
            countDownLatch?.countDown()
            needWaitCount.getAndDecrement()
        }
    }

    private fun executeTaskMain() {
        startTime = System.currentTimeMillis()
        mainThreadTask.forEach {
            val time = System.currentTimeMillis()
            DessertDispatchRunnable(it, this).run()
            DebugLog.logD(it.javaClass.simpleName, "real main " + (System.currentTimeMillis() - time))
        }

        DebugLog.logD("main task duration", System.currentTimeMillis() - startTime)
    }

    private fun sendAndExecuteAsyncTask() {
        allTasks.forEach {
            if (it.onlyInMainProcess and !isMainProcess) {
                it.makeTaskDone()
            } else{
                //If it needCall skip it
                if (it.needCall) {
                    DebugLog.logD("ClassName: ${it.javaClass.simpleName} MethodName: ${it.methodName}", "needCall")
                    return@forEach
                }
                it.sendTaskReal()
            }

            it.isSend = true
        }
    }

    companion object {
        const val WAITING_TIME = 10000

        @JvmStatic
        private lateinit var contextWeakRef: WeakReference<Context>

        @JvmStatic
        private var isMainProcess = false

        @JvmStatic
        @Volatile
        private var hasInit: Boolean = false

        @JvmStatic
        fun init(context: Context?): Companion {
            context?.let {
                contextWeakRef = WeakReference(it)
                hasInit = true
                isMainProcess = context.isMainProcess()
            }
            return this
        }

        @JvmStatic
        fun build(): DessertDispatcher {
            return getInstance()
        }

        @JvmStatic
        fun isDebug(isDebug: Boolean): Companion {
            DebugLog.isDebug = isDebug
            return this
        }

        @JvmStatic
        fun getContext() = contextWeakRef.get()

        @JvmStatic
        fun isMainProcess() = isMainProcess

        @JvmStatic
        private val instanceReal by lazy {
            DessertDispatcher()
        }

        @JvmStatic
        fun getInstance(): DessertDispatcher {
            require(hasInit) { throw RuntimeException("must call TaskDispatcher.init first") }
            return instanceReal
        }
    }

    private fun DessertTask.sendTaskReal() {
        if (runOnMainThread) {
            mainThreadTask.add(this)
            if (needCall) {
                callback = {
                    markTaskDone()
                    isFinish = true
                    makeTaskDone()
                    DebugLog.logD(javaClass.simpleName, "finish")
                }
            }
        } else {
            val future = runOn.submit(DessertDispatchRunnable(this, this@DessertDispatcher))
            futures.add(future)
        }
    }

    private fun DessertTask.collectDepends() {
        if (!dependOn.isNullOrEmpty()) {
            dependOn.forEach {
                if (dependedHasMap[it] == null) {
                    dependedHasMap[it] = arrayListOf()
                }

                dependedHasMap[it]?.add(this)
                if (finishTasks.contains(it)) satisfy()
            }
        }

        if (!dependOnByName.isNullOrEmpty()) {
            dependOnByName.forEach {
                if (dependedNameHashMap[it] == null) {
                    dependedNameHashMap[it] = arrayListOf()
                }

                dependedNameHashMap[it]?.add(this)
                for (finishTask in finishTasks) {
                    if (finishTask.simpleName == it) {
                        satisfy()
                        break
                    }
                }
            }
        }
    }

    private fun DessertTask.ifNeedWait(action: DessertTask.() -> Unit) {
        if (!runOnMainThread and needWait) {
            action.invoke(this)
        }
    }

    private fun DessertTask.ifNeedCall(action: DessertTask.() -> Unit) {
        if (!runOnMainThread and needCall) {
            action.invoke(this)
        }
    }

    private fun printDependedMsg() {
        DebugLog.logD(javaClass.simpleName, needWaitCount.get())
        if (DebugLog.isDebug) {
            for ((key, value) in dependedHasMap) {
                DebugLog.logD(key?.simpleName, "size -> ${value.size}")
                value.forEach {
                    DebugLog.logD("dessert task", it.javaClass.simpleName)
                }
            }

            for ((key, value) in dependedNameHashMap) {
                DebugLog.logD(key, "size -> ${value.size}")
                value.forEach {
                    DebugLog.logD("dessert task", it.javaClass.simpleName)
                }
            }
        }
    }

    private fun printSortResultMsg() {
        if (!DebugLog.isDebug) {
            return
        }

        val sortResult = StringBuilder()
        allTasks.forEachIndexed { index, it ->
            if (index == 0) {
                sortResult.append("{\n")
            }

            sortResult.append("ClassName: ${it.javaClass.run { if (simpleName.isEmpty()) "Unknown" else simpleName }}" +
                    ", MethodName: ${it.methodName.run { if (isEmpty()) "Unknown" else this }} \n")

            if (index == allTasks.lastIndex) {
                sortResult.append("}")
            }
        }
        DebugLog.logD("SortTaskResult", sortResult.toString())
    }
}

