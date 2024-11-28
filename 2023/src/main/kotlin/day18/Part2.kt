package org.example.day1.day18

import org.example.day1.utils.Utils

fun main() {
    val input = Utils.loadResource("/day18.txt")
    val parsed = parseInput(input)
    val fixed = parsed.map(::fixInput)
    val numOfBorders = fixed.sumOf { it.len }
    val lines = buildLines(fixed)
    println(result(lines, numOfBorders))
}


fun fixInput(l: DigLine): DigLine = run {
    val num = l.color.removePrefix("#")
    val newLength = num.take(5).toLong(16)
    val newDir = when (num.takeLast(1)) {
        "0" -> Dir.R
        "1" -> Dir.D
        "2" -> Dir.L
        "3" -> Dir.U
        else -> TODO()
    }
    DigLine(newDir, newLength, l.color)
}