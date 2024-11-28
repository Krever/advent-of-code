package org.example.day1.day12

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day12.txt")
    val parsed = parseInput(input)
    val result = parsed.mapIndexed { idx, (line, counts) ->
        val line = List(5) { line }.joinToString("?")
        val counts = List(5) { counts }.flatten()
        placementsCache.clear()
        numOfMatchesCache.clear()
        val numOfPossibilities = numOfMatches(line, counts)
        println(">>> ${idx}/${parsed.size} $numOfPossibilities ")
        numOfPossibilities
    }.sum()
    print(result)
}

val numOfMatchesCache: MutableMap<Pair<String, Int>, Long> = mutableMapOf()

fun numOfMatches(line: String, counts: List<Int>): Long {
    fun step(line: String, remainingCounts: List<Int>): Long {
        fun calculate(): Long {
            return if (remainingCounts.isEmpty()) {
                if (line.contains('#')) 0
                else 1
            } else {
                val nextCount = remainingCounts[0]
                val numOfPossibilities = findPossiblePlacements(nextCount, line)
                    .sumOf { placement ->
                        // +1 because we need to strip the margin/separator
                        val newLine = line.drop(placement + nextCount + 1)
                        step(newLine, remainingCounts.drop(1))
                    }
                numOfPossibilities
            }
        }
        return numOfMatchesCache.getOrPut(line to remainingCounts.size) { calculate() }
    }
    return step(line, counts)
}

val placementsCache: MutableMap<Pair<Int, String>, List<Int>> = mutableMapOf()
fun findPossiblePlacements(length: Int, line: String): List<Int> {
    fun calculate(): List<Int> {
        val regex = """(?<!#)[?#]{$length}(?=[.?]|$)"""
        val overlappingRegex = "(?=$regex).".toRegex()
        val matches = overlappingRegex.findAll(line).toList().map { it.range.start }
            .filter { it == 0 || !line.take(it - 1).contains('#') }
        return matches
    }
    return placementsCache.getOrPut(length to line) { calculate() }
}
