package org.example.day1.day10

import org.example.day1.day10.Dir.*
import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day10.txt")
    val parsed = parseInput(input)
    val start = parsed.findStartingPoint()
    val rawLoop = findPossibleOutputs(start, parsed).firstNotNullOf { parsed.findPathToStart(start, it) }
    val loop = Loop(rawLoop)
    val result = parsed.indices.sumOf { line ->
        parsed[0].indices.count { col -> loop.encloses(line to col) }
    }
    println()
    printMap(parsed)
    println()
    printHorizontalDirs(parsed, loop)
    println()
    printNextDirs(parsed, loop)
    println(result)
}

fun Dir.asArrow(): Char = when (this) {
    N -> '↑'
    E -> '→'
    W -> '←'
    S -> '↓'
}

typealias RawLoop = List<Pair<Coords, Dir>>

data class Loop(val raw: RawLoop) {
    val horDirs: List<Dir> = buildHorizontalDirsForLoop(raw)
    val byCoords: Map<Coords, Int> = raw.mapIndexed { idx, elem -> elem.first to idx }.toMap()

    fun contains(c: Coords) = byCoords.contains(c)
    fun size() = raw.size

    fun getHorizontalDir(c: Coords): Dir = horDirs[byCoords[c]!!]
    fun getNextDir(c: Coords): Dir = raw[byCoords[c]!!].second
}

fun Loop.encloses(c: Coords): Boolean {
    val isPartOfLoop = this.contains(c)
    return if (isPartOfLoop) false
    else {
        val numOfIntersections = this.findNumOfIntersections(c)
        println("$c $numOfIntersections ")
        numOfIntersections % 2 == 1
    }
}

fun buildHorizontalDirsForLoop(loop: RawLoop): List<Dir> {
    val buffer: MutableList<Dir?> = MutableList(loop.size) { null }
    fun search(index: Int): Dir {
        val elem = loop[index]
        return if (elem.second.isHorizontal()) {
            buffer.set(index, elem.second)
            elem.second
        } else if (buffer[index] != null) buffer[index]!!
        else if (index == 0) search(loop.size - 1)
        else search(index - 1)
    }
    return loop.indices.reversed().map { search(it) }.reversed()
}

fun Loop.findNumOfIntersections(c: Coords): Int {
    val onLine = this.raw
        .filter { (lineElem, _) -> lineElem.first < c.first && lineElem.second == c.second }
        .sortedBy { it.first.first }
        .map { this.getHorizontalDir(it.first) }

    val init: Pair<Int, Dir?> = 0 to null
    return onLine.fold(init) { (count, lastDir), dir -> if (lastDir != dir) (count + 1) to dir else count to dir }.first
}

fun Dir.isHorizontal(): Boolean {
    return when (this) {
        N -> false
        E -> true
        W -> true
        S -> false
    }
}

fun printMap(parsed: Input) = parsed.mapIndexed { index, it ->
    "$index" + it
        .replace('L', '└')
        .replace('J', '┘')
        .replace('7', '┐')
        .replace('F', '┌')
        .replace('|', '│')
        .replace('-', '─')
}.forEach { println(it) }


fun printHorizontalDirs(parsed: Input, loop: Loop) = parsed.indices.forEach { line ->
    parsed[0].indices.forEach { col ->
        val c = line to col
        val char = if (loop.contains(c)) {
            val dir = loop.getHorizontalDir(c)
            dir.asArrow()
        } else '.'
        print(" $char")
    }
    println()
}


fun printNextDirs(parsed: Input, loop: Loop) = parsed.indices.forEach { line ->
    parsed[0].indices.forEach { col ->
        val c = line to col
        val char = if (loop.contains(c)) {
            val dir = loop.getNextDir(c)
            dir.asArrow()
        } else '.'
        print(" $char")
    }
    println()
}
