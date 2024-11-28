package org.example.day1.day9

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day9.txt")
    val parsed = parseInput(input)
    val result = parsed.sumOf { it.getValue2(0, -1) }
    println(result)
}

fun List<Int>.getValue2(row: Int, column: Int): Int {
//    println(">>> (${row} ${column})")
    val value = if (row == 0 && column >= 0) this[column]
    else if (row == this.size - 2) return 0
    else if (column == -1) getValue2(row, column + 1) - getValue2(row + 1, column)
    else getValue2(row - 1, column + 1) - getValue2(row - 1, column)
//    println("(${row} ${column}) = $value")
    return value
}