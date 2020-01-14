package com.shen.dessert_task

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

/**
 * created by shen on 2019/10/24
 * at 23:51
 **/
fun getCPUExecute() = cpuThreadPoolExecute

fun getIOExecute() = ioThreadPoolExecute

// cpu 密集型任务的线程池
private val cpuThreadPoolExecute: ThreadPoolExecutor by lazy {
    ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE_SECONDS.toLong(),
        TimeUnit.SECONDS,
        poolWorkQueue,
        defaultThreadFactor,
        handler
    ).apply {
        allowCoreThreadTimeOut(true)
    }
}

// io 密集型任务的线程池
private val ioThreadPoolExecute: ExecutorService by lazy {
    Executors.newCachedThreadPool(defaultThreadFactor)
}

// cpu 核数
private val CPU_COUNT by lazy { Runtime.getRuntime().availableProcessors() }

//线程池的数量
val CORE_POOL_SIZE by lazy { max(2, CPU_COUNT.coerceAtMost(5)) }

//线程池线程数的最大值
val MAXIMUM_POOL_SIZE by lazy { CORE_POOL_SIZE }

private const val KEEP_ALIVE_SECONDS = 5

private val poolWorkQueue by lazy { LinkedBlockingQueue<Runnable>() }

//默认线程公厂
private val defaultThreadFactor by lazy { DefaultThreadFactor() }

private val handler: RejectedExecutionHandler = RejectedExecutionHandler { runnable, _ ->
    Executors.newCachedThreadPool().execute(runnable)
}

//default thread factory
private class DefaultThreadFactor : ThreadFactory {
    private val group: ThreadGroup by lazy {
        val manager = System.getSecurityManager()
        if (manager != null) manager.threadGroup else Thread.currentThread().threadGroup
    }
    private val threadNumber by lazy { AtomicInteger(1) }
    private val namePrefix by lazy {
        "TaskDispatcherPool-${poolNumber.getAndIncrement()}-Thread-"
    }

    override fun newThread(runnable: Runnable): Thread = Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0)
        .apply {
            if (isDaemon) isDaemon = false
            if (priority != Thread.NORM_PRIORITY) priority = Thread.NORM_PRIORITY
        }


    companion object {
        @JvmStatic private val poolNumber = AtomicInteger(1)
    }
}