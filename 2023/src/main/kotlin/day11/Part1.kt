package org.example.day1.day11

import org.example.day1.utils.Utils
import kotlin.math.abs


fun main() {
    val input = Utils.loadResource("/day11.txt")
    val parsed = parseInput(input)
    val expanded = expand(parsed)
    val galaxies = findGalaxies(expanded)
    val pairs = generateUnorderedPairs(galaxies)
    val result = pairs.sumOf { calculateDistance(it.first, it.second) }
    print(result)
}

typealias Input = List<String>
typealias Coords = Pair<Int, Int>

fun parseInput(input: String): Input {
    val lines = input.lines()
    return lines
}

fun findGalaxies(input: Input): List<Coords> {
    return input.flatMapIndexed { lineIdx, line ->
        line.withIndex().filter { x -> x.value == '#' }
            .map { it -> lineIdx to it.index }
    }
}

fun expand(input: Input): Input {
    val linesExpanded = input.flatMap { line -> if (line.contains("#")) listOf(line) else listOf(line, line) }
    val buffer = linesExpanded.map { StringBuilder(it) }
    var numAdded = 0
    for (i in input[0].indices) {
        val columnEmpty = input.count { it[i] == '#' } == 0
        if (columnEmpty) {
            buffer.forEach { it.insert(i + numAdded, '.') }
            numAdded += 1
        }
    }
    return buffer.map { it.toString() }
}

fun <T> generateUnorderedPairs(list: List<T>): List<Pair<T, T>> {
    return list.flatMapIndexed { idx, elem ->
        list.drop(idx + 1)
            .map { elem to it }
            .filter { (a, b) -> a != b }
    }
}

fun calculateDistance(a: Coords, b: Coords): Int = abs(a.first - b.first) + abs(a.second - b.second)
