package day14

import utils.Utils


fun main() {
    val input = Utils.loadResource("/day14.txt")
    val parsed = parseInput(input)
    val result = calculateLoad(parsed)
    println(result)
}

typealias Input = List<String>

fun parseInput(input: String) = input.lines()

fun calculateLoad(input: Input): Int {
    fun weight(row: Int) = input.size - row
    fun calcColumn(col: Int): Int {
        fun step(row: Int, barrier: Int, accumulator: Int): Int {
            return if (row == input.size) accumulator
            else {
                val elem = input[row][col]
                if (elem == 'O') step(row + 1, barrier + 1, accumulator + weight(barrier))
                else if (elem == '.') step(row + 1, barrier, accumulator)
                else if (elem == '#') step(row + 1, row + 1, accumulator)
                else TODO()
            }
        }

        val colResult = step(0, 0, 0)
        println("Column $col: $colResult")
        return colResult
    }
    return input.indices.sumOf { calcColumn(it) }
}
