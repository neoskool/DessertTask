package com.shen.dessert_task.sort

import java.util.*

/**
 * created by shen on 2019/10/27
 * at 17:06
 **/
class Graph(private var verticalCount: Int) {
    private val adj: MutableList<MutableList<Int>> by lazy {
        MutableList(verticalCount) { mutableListOf<Int>() }
    }

    /**
     * 添加边
     *
     * @param u from
     * @param v to
     */
    fun addEdge(u: Int, v: Int) = adj[u].add(v)

    fun topoLogicSort(): Vector<Int> {
        val indegree = IntArray(verticalCount)
        for (i in 0 until verticalCount) {//初始化所有点的入度数量
            val temp = adj[i]
            for (node in temp) {
                indegree[node]++
            }
        }
        val queue = LinkedList<Int>()
        for (i in 0 until verticalCount) {//找出所有入度为0的点
            if (indegree[i] == 0) {
                queue.add(i)
            }
        }
        var cnt = 0
        val topOrder = Vector<Int>()
        while (!queue.isEmpty()) {
            val u = queue.poll()
            topOrder.add(u)
            for (node in adj[u]) {//找到该点（入度为0）的所有邻接点
                if (--indegree[node] == 0) {//把这个点的入度减一，如果入度变成了0，那么添加到入度0的队列里
                    queue.add(node)
                }
            }
            cnt++
        }
        check(cnt == verticalCount) {
            //检查是否有环，理论上拿出来的点的次数和点的数量应该一致，如果不一致，说明有环
            "Exists a cycle in the graph"
        }
        return topOrder
    }
}