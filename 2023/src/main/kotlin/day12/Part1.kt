package org.example.day1.day12

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day12.txt")
    val parsed = parseInput(input)
    val result = parsed.mapIndexed { idx, (line, counts) ->
        val numOfPossibilities = countPossibilities(line, counts)
        println("${idx}/${parsed.size} $numOfPossibilities")
        numOfPossibilities
    }.sum()
    print(result)
}

typealias Input = List<Pair<String, List<Int>>>

fun parseInput(input: String): Input {
    val lines = input.lines()
    return lines.map { l ->
        val split = l.split(' ')
        val pattern = split[0]
        val counts = split[1].split(',').map { it.toInt() }
        pattern to counts
    }
}


fun countPossibilities(line: String, counts: List<Int>): Int {
    return if (line.contains('?')) {
        countPossibilities(line.replaceFirst('?', '#'), counts) +
                countPossibilities(line.replaceFirst('?', '.'), counts)
    } else {
        if (checkIfMatches(line, counts)) 1 else 0
    }
}


val regex = "#+".toRegex()
fun checkIfMatches(line: String, counts: List<Int>): Boolean {
    return regex.findAll(line).toList().map { it.value.length } == counts
}


