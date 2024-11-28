package org.example.day1.day8

import arrow.core.extensions.list.foldable.forAll
import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day8.txt")
    val parsed = parseInput(input)
    val numOfSteps = findNumOfStepsTillFinish(parsed)
    println(numOfSteps) // strip the start node
}

fun findNumOfStepsTillFinish(input: Input): Long {
    val starts = input.nodes.keys.filter { it.endsWith('A') }
    fun canFinish(nodes: List<Node>) = nodes.forAll { it.endsWith('Z') }
    tailrec fun step(currentNodes: List<Node>, step: Long): Long {
        val nextDir = input.directions.getDirection(step)
        val nextNodes = currentNodes.map { input.nodes.getNextNode(it, nextDir) }
//        println(nextNodes)
        if(step.mod(1000000) == 0) { println(step); println(currentNodes) }
        return if (canFinish(nextNodes)) step
        else step(nextNodes, step + 1)
    }
    return step(starts, 0)
}
