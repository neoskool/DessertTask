package com.shen.dessert_task

import android.content.Context
import android.os.Process
import androidx.annotation.IntRange
import com.shen.dessert_task.annotation.Priorities
import com.shen.dessert_task.utils.DebugLog
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

/**
 * created by shen on 2019/10/24
 * at 22:13
 **/
abstract class DessertTask : IDessertTask {

    protected val Tag by lazy {
        javaClass.simpleName
    }

    protected val context by lazy { DessertDispatcher.getContext() }

    protected val isMainProcess by lazy { DessertDispatcher.isMainProcess() }

    @Volatile
    var isWaiting = false

    @Volatile
    var isRunning = false

    @Volatile
    var isFinish = false

    @Volatile
    var isSend = false


    private val depends by lazy {
        CountDownLatch(dependOn.size + dependOnByName.size)
    }

    /**
     * 当使用接口创建时会使用到
     */
    internal open val methodName: String = ""

    ///当前Task等待，让依赖的Task先执行
    fun waitToSatisfy() {
        try {
            depends.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    ///依赖的Task执行完一个
    fun satisfy() {
        DebugLog.logD("${if (methodName.isEmpty()) this.javaClass.simpleName else methodName} Pre task satisfy", depends.count)
        depends.countDown()
        DebugLog.logD("${if (methodName.isEmpty()) this.javaClass.simpleName else methodName} After task satisfy", depends.count)
    }

    /**
     * 是否需要优先执行，解决特殊场景的问题：一个 Task 耗时非常多，但是优先级却一般，很有可能开始的时间比较晚
     * 只适合可以把它尽早开始
     */
    override fun needRunAsSoon(): Boolean = false

    ///Task的优先级，运行在主线程则不要去改优先级
    override fun priority(): Int = Process.THREAD_PRIORITY_BACKGROUND


    /**
     * Task执行在哪个线程池，默认在IO的线程池；
     * CPU 密集型的一定要切换到DispatcherExecutor.getCPUExecutor();
     */
    override val runOn: ExecutorService = getIOExecute()

    /**
     * 异步线程执行的Task是否需要在被调用await的时候等待，默认不需要
     */
    override val needWait: Boolean = false

    /**
     * 当前Task依赖的Task集合（需要等待被依赖的Task执行完毕才能执行自己），默认没有依赖
     */
    override val dependOn: MutableList<Class<out DessertTask>> = mutableListOf()

    override val dependOnByName: MutableList<String> = mutableListOf()

    /**
     * 是否在主线程进行，默认不在
     */
    override val runOnMainThread: Boolean = false

    override var tailRunnable: Runnable? = null

    override var needCall: Boolean = false

    /**
     * 是否只在主进程，默认是
     */
    override val onlyInMainProcess: Boolean = true

    override var callback: (() -> Unit)? = null
}

abstract class MainTask : DessertTask() {
    override val runOnMainThread: Boolean = true
}

interface IDessertTask {

    ///优先级范围, 可以根据 Task 重要程度和工作量指定；之后根据实际情况决定是否有必要放更大
    @Priorities
    fun priority(): Int

    fun needRunAsSoon(): Boolean

    fun run()

    ///Task 执行所在的线程池，可以指定，一般默认
    val runOn: Executor

    ///依赖关系
    val dependOn: List<Class<out DessertTask>>?

    val dependOnByName: List<String>?

    ///异步线程执行的 Task 是否需要在被调用 await 时进行等待，默认不需要
    val needWait: Boolean

    ///是否在主线程执行
    val runOnMainThread: Boolean

    ///只是在主进程进行
    val onlyInMainProcess: Boolean

    ///Task 主任务执行完成之后需要执行的任务
    val tailRunnable: Runnable?

    val needCall: Boolean

    val callback: (() -> Unit)?
}

fun easyTask(block: (context: Context?) -> Unit) = object : DessertTask() {
    override val methodName: String = "easyTask"

    override fun run() {
        block(context)
    }
}
