package com.shen.dessert_task.annotation

import androidx.annotation.StringDef


@StringDef(value = [Executors.IO, Executors.CPU])
@Retention(AnnotationRetention.SOURCE)
annotation class Executors {
    companion object {
       const val IO: String = "io"
       const val CPU: String = "cpu"
    }
}