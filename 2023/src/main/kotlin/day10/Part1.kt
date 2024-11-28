package org.example.day1.day10

import org.example.day1.day10.Dir.*
import org.example.day1.utils.Utils
import kotlin.math.ceil


fun main() {
    val input = Utils.loadResource("/day10.txt")
    val parsed = parseInput(input)
    val start = parsed.findStartingPoint()
    val result = findPossibleOutputs(start, parsed).firstNotNullOf { parsed.findPathToStart(start, it) }
    println(ceil(result.size.toDouble() / 2))

}

typealias Input = List<String>
typealias Coords = Pair<Int, Int>
typealias Node = Char

fun parseInput(input: String): Input {
    val lines = input.lines()
    return lines
}

fun Input.findStartingPoint(): Coords {
    for ((lineIdx, line) in this.withIndex()) {
        for ((cellIdx, cell) in line.withIndex()) {
            if (cell == 'S') return lineIdx to cellIdx
        }
    }
    throw RuntimeException("Start not found")
}

fun Coords.add(x: Int, y: Int) = (this.first + x) to (this.second + y)

fun Coords.go(dir: Dir) = when (dir) {
    N -> this.add(-1, 0)
    E -> this.add(0, 1)
    W -> this.add(0, -1)
    S -> this.add(1, 0)
}

fun findPossibleOutputs(start: Coords, input: Input): List<Dir> {
    return Dir.entries.filter { dir -> input.getValue(start.go(dir)).isConnectedTo(dir.opposite()) }
}

enum class Dir {
    N, E, W, S;

    fun opposite(): Dir {
        return when (this) {
            N -> S
            E -> W
            W -> E
            S -> N
        }
    }
}


val chartToDirs: Map<Char, List<Dir>> = mapOf(
    '|' to listOf(N, S),
    '-' to listOf(E, W),
    'L' to listOf(N, E),
    'J' to listOf(N, W),
    '7' to listOf(S, W),
    'F' to listOf(S, E),
)

fun Node.nextDir(from: Dir): Dir? {
    val dirs = chartToDirs.get(this)
    return dirs?.filter { it != from }?.get(0)
}

typealias PathElem = Pair<Coords, Dir>

fun Node.isConnectedTo(from: Dir): Boolean {
    return chartToDirs.get(this)?.contains(from) ?: false
}

fun Input.findPathToStart(start: Coords, into: Dir): List<PathElem>? {
    println("Looking for path from $start going into dir $into")
    tailrec fun step(path: List<PathElem>): List<PathElem>? {
        val (lastNode, dir) = path.last()
        println(path.last())
        val nextNode = lastNode.go(dir)
        val nextValue = this.getValue(nextNode)
        if (nextValue == 'S') return path
        else {
            val nextDir = nextValue.nextDir(dir.opposite())
            if (nextDir == null) return null
            else return step(path.plus(nextNode to nextDir))
        }
    }
    return step(listOf(start to into))
}

fun Input.getValue(c: Coords): Char = this[c.first][c.second]
