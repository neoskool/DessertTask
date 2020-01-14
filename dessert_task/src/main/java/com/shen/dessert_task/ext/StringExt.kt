package com.shen.dessert_task.ext


/**
 * created by shen on 2019/10/24
 * at 22:57
 **/
fun String?.isNotEmpty() = !(this == null || this.isEmpty())