# DessertTask
在Application里简简单单的启动不同的任务在不同的线程

## Get It

>+ Androidx [![](https://jitpack.io/v/flalaorg/DessertTask.svg)](https://jitpack.io/#flalaorg/DessertTask) <br/>
>+ Android-support [![](https://jitpack.io/v/flalaorg/DessertTask-Android.svg)](https://jitpack.io/#flalaorg/DessertTask-Android)

## Implementation
> AndroidX 请使用 `implementation 'com.github.flalaorg:DessertTask:$version'` <br/>
> Android-support 请使用 `implementation 'com.github.flalaorg:DessertTask-Android:$version'`

## How to use
+ 如何初始化，我们推荐以下方式进行初始化 <br/>
 ```kotlin
 DessertDispatcher.init(this)
 ```
 <br/>
 
+ 获取 DessertDispatcher 实例 <br/>
```kotlin
// 链式调用
DessertDispatcher.init(this)
            .build()
```
或者
```kotlin
// 获取实例
DessertDispatcher.getInstance()
```
<br/>

+ 现在你可以任你喜欢的添加Task了 <br/>
```kotlin
create(Class<Interface>, InterfaceImpl())
// 例如
create(ITask::class.java, TaskImpl())
```
或者
```kotlin
//DessertTask 是抽象的你需要继承他, 然后在 addTask 时使用子类
addTask(DessertTask())
// 例如
addTask(TaskOne())
```
或者
```kotlin
addTask(easyTask { 
    Log.d("EasyTask wow", "Start: ${Thread.currentThread().name}")       
})
```
+ 结束你应该使用 `start()` 去启动Task
+ 全部流程例子
```kotlin
DessertDispatcher.init(this)
            .build()
            .create(ITask::class.java, TaskImpl())
            .addTask(TaskOne())
            .addTask(easyTask {
                Log.d("EasyTask wow", "Start: ${Thread.currentThread().name}")
            })
            .start()
```

## DessertTask 所有属性
1. `priority` 优先级范围, 可以根据 Task 重要程度和工作量指定；之后根据实际情况决定是否有必要放更大
2. `needRunAsSoon` 是否需要尽快运行
3. `runOn` Task 执行所在的线程池，可以指定，一般默认
4. `dependOn` 依赖的Task
5. `dependOnByName` 依赖的Task的方法名或者类名(方法名需要使用`create(Class<Interface>, InterfaceImpl())`里的方法名)
6. `needWait` 异步线程执行的 Task 是否需要在被调用 await 时进行等待，默认不需要
7. `runOnMainThread` 是否在主线程执行
8. `onlyInMainProcess` 是否只在主进程进行
9. `tailRunnable` 主任务执行完成之后需要执行的任务
10. `needCall` ~~Task是否需要回调~~ 现在更推荐使用 `tailRunnable`，因为目前callback是在任务完成后调用
11. `callback` ~~执行的回调~~ 现在更推荐使用 `tailRunnable`，因为目前callback是在任务完成后调用

## Annotation 只推荐在`create(Class<Interface>, InterfaceImpl())`的接口中使用
- `@Task` 用来标记方法，这个方法会被转换成 `DessertTask` 
- `@TaskConfig` Task的各种属性设置
>- `priority` 只能在 `Priorities.THREAD_PRIORITY_FOREGROUND`、`Priorities.THREAD_PRIORITY_BACKGROUND`、`Priorities.THREAD_PRIORITY_LOWEST` 中选择
>- `runOnExecute` 同意义于 `runOn` , 不过只能使用 `Executors.IO` 和 `Executors.CPU`
>- `dependOn` 同意义于 `dependonByName`
>- `tailRunnable` 对应的 `@TaskTailRunnable` 内使用的 name
>- `targetCallback` 对应的 `@TaskCallback`内使用的 name
- `@TaskCallback` 用来标记方法，这个方法会被转换成 `callback` 
- `@TaskTailRunnable` 用来标记方法，这个方法会被转换成 `tailRunnable`