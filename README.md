# DessertTask [中文点这里](https://github.com/flalaorg/DessertTask/blob/master/Documents/README-CN.md)
easy start task in application in different threads

## Get It

>+ Androidx [![](https://jitpack.io/v/flalaorg/DessertTask.svg)](https://jitpack.io/#flalaorg/DessertTask) <br/>
>+ Android-support [![](https://jitpack.io/v/flalaorg/DessertTask-Android.svg)](https://jitpack.io/#flalaorg/DessertTask-Android)

## Implementation
> AndroidX Please use `implementation 'com.github.flalaorg:DessertTask:$version'` <br/>
> Android-support Please use `implementation 'com.github.flalaorg:DessertTask-Android:$version'`

## How to use
+ How to initialize, we recommend the following <br/>
 ```kotlin
 DessertDispatcher.init(this)
 ```
 <br/>
 
+ If you want to get an instance of it <br/>
```kotlin
// Chained call
DessertDispatcher.init(this)
            .build()
```
or
```kotlin
// Get singleton
DessertDispatcher.getInstance()
```
<br/>

+ Now you can add tasks as you like <br/>
```kotlin
create(Class<Interface>, InterfaceImpl())
// such as
create(ITask::class.java, TaskImpl())
```
or
```kotlin
//DessertTask is abstract you should extend it, Then put its implementation class in
addTask(DessertTask())
// such as
addTask(TaskOne())
```
or
```kotlin
addTask(easyTask { 
    Log.d("EasyTask wow", "Start: ${Thread.currentThread().name}")       
})
```
+ End you should use `start()` to Start Tasks
+ Sample
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

## DessertTask Attributes
1. `priority` The priority range can be specified according to the importance and workload of the task
2. `needRunAsSoon` Meaning as its name
3. `runOn` Task execution thread pool, can be specified, generally default
4. `dependOn` Dependent Task
5. `dependOnByName` The name of the dependent task, which can be the method name or the class name
6. `needWait` Whether the Task executed by the asynchronous thread needs to wait when await is called
7. `runOnMainThread` Meaning as its name
8. `onlyInMainProcess` Meaning as its name
9. `tailRunnable` Tasks that need to be performed after the main task is completed
10. `needCall` ~~Does the Task require a callback~~ More recommended now use `tailRunnable`
11. `callback` ~~Meaning as its name~~ More recommended now use `tailRunnable`

## Annotation
- `@Task` Marking methods, the method will be converted to `DessertTask` 
- `@TaskConfig` Task Attributes setting, but there are some differences
>- `priority` can only be selected in `Priorities.THREAD_PRIORITY_FOREGROUND`、`Priorities.THREAD_PRIORITY_BACKGROUND`、`Priorities.THREAD_PRIORITY_LOWEST`
>- `runOnExecute` is the same as `runOn`, but can only be selected in `Executors.IO` and `Executors.CPU`
>- `dependOn` is the same as `dependonByName`
>- `tailRunnable` method name who annotation used `@TaskTailRunnable`
>- `targetCallback` method name who annotation used `@TaskCallback`
- `@TaskCallback` Marking methods, the method will be converted to `callback`
- `@TaskTailRunnable` Marking methods, the method will be converted to `tailRunnable`