package org.example.day1.day9

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day9.txt")
    val parsed = parseInput(input)
    val result = parsed.sumOf { it.getValue(0, it.size) }
    println(result)
}

fun parseInput(input: String): List<List<Int>> {
    val lines = input.lines()
    return lines.map { it.split(" ").map { it.toInt() } }
}

fun List<Int>.getValue(row: Int, column: Int): Int {
//    println(">>> (${row} ${column})")
    val rowSize = this.size - row
    val value = if (row == 0 && column < rowSize) this[column]
    else if (row == this.size - 2) 0
    else if (column == rowSize) getValue(row, column - 1) + getValue(row + 1, column - 1)
    else getValue(row - 1, column + 1) - getValue(row - 1, column)
//    println("(${row} ${column}) = $value")
    return value
}