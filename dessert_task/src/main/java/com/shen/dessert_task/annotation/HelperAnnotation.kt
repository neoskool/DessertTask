package com.shen.dessert_task.annotation

import androidx.annotation.IntDef
import androidx.annotation.StringDef


@StringDef(value = [Executors.IO, Executors.CPU])
@Retention(AnnotationRetention.SOURCE)
annotation class Executors {
    companion object {
       const val IO: String = "io"
       const val CPU: String = "cpu"
    }
}

@IntDef(value = [
    Priorities.THREAD_PRIORITY_FOREGROUND,
    Priorities.THREAD_PRIORITY_BACKGROUND,
    Priorities.THREAD_PRIORITY_LOWEST
])
@Retention(AnnotationRetention.SOURCE)
annotation class Priorities {
    companion object {
        const val THREAD_PRIORITY_FOREGROUND = -2
        const val THREAD_PRIORITY_BACKGROUND = 10
        const val THREAD_PRIORITY_LOWEST = 19
    }
}