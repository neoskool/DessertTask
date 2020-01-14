package com.shen.dessert_task.ext

/**
 * created by shen on 2019/10/27
 * at 17:39
 **/
fun <T> List<T>?.isNullOrEmpty() = (this == null) || (this.isEmpty())