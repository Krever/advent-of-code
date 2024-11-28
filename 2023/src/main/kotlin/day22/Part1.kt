package org.example.day1.day22

import org.example.day1.utils.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = Utils.loadResource("/day22.txt")
    val parsed: Input = parseInput(input)
    val stable = fallDown(parsed)
    val supportData = calculateSupport(parsed, stable)
    println(numOfRemovable(supportData))

}

data class Point(val x: Int, val y: Int, val z: Int)
data class Brick(val from: Point, val to: Point, val label: String) {
    fun z() = min(from.z, to.z)
    fun zSize(): Int = abs(to.z - from.z)

    val points2d: List<Point2d> = run {
        (min(from.x, to.x)..max(from.x, to.x))
            .flatMap { x ->
                (min(from.y, to.y)..max(from.y, to.y))
                    .map { y ->
                        Point2d(x, y)
                    }
            }

    }

}
typealias Input = List<Brick>

fun parseInput(input: String): Input = run {
    input.lines().mapIndexed { index, line ->
        val split = line.split("~")
        Brick(parsePoint(split[0]), parsePoint(split[1]), ('A'.code + index).toChar().toString())
    }
}

fun parsePoint(str: String): Point = run {
    val split = str.split(",")
    Point(split[0].toInt(), split[1].toInt(), split[2].toInt())
}

val ground: Label = ""
typealias Support = Set<Pair<Label, Label>>
fun fallDown(bricks: List<Brick>): Support = run {
    tailrec fun run(
        queue: List<Brick>,
        maxes: Map<Point2d, Pair<Label, Int>>,
        supports: Set<Pair<Label, Label>>
    ): Set<Pair<Label, Label>> {
        return if (queue.isEmpty()) supports
        else {
            val current = queue.first()
            val below = current.points2d.map { maxes.getOrDefault(it, ground to 0) }
            val maxZ = below.maxOf { it.second }
            val supporting = below.filter { it.second == maxZ && it.first != ground }.map { it.first to current.label }
            val newZ = maxZ + 1 + current.zSize()
            val newMaxes = maxes + current.points2d.associateWith { current.label to newZ }
            run(queue.drop(1), maxes + newMaxes, supports + supporting)
        }
    }

    val initQueue = bricks.sortedBy { it.z() }
    run(initQueue, mapOf(), setOf())
}

data class Point2d(val x: Int, val y: Int)
typealias Label = String

data class SupportData(val supports: Map<Label, Set<Label>>, val supportedBy: Map<Label, Set<Label>>)

fun calculateSupport(bricks: List<Brick>, aSupportB: Support): SupportData = run {
    val allBricks = bricks.map { it.label }
    SupportData(
        allBricks.associateWith { b -> aSupportB.filter { it.first == b }.map { it.second }.toSet() },
        allBricks.associateWith { b -> aSupportB.filter { it.second == b }.map { it.first }.toSet() }
    )
}

fun numOfRemovable(data: SupportData): Int = run {
    data.supports.size - data.supportedBy.filter { it.value.size == 1 }.map { it.value.first() }.toSet().size
}