package day16

import utils.Utils


fun main() {
    val input = Utils.loadResource("/day16.txt")
    val parsed = parseInput(input)
    val starts = generatePossibleStarts(parsed)
    val result = starts.withIndex().maxOf { x ->
        println("${x.index}/${starts.size}")
        score(runBeams(parsed, x.value))
    }
    println(result)
}

fun score(energised: Set<Cell>) = energised.map { it.row to it.col }.toSet().size

fun generatePossibleStarts(input: Input): List<Cell> {
    val maxCol = input.first().length - 1
    val maxRow = input.size - 1
    return input.indices.map { Cell(it, 0, Dir.Right) } +
            input.indices.map { Cell(it, maxCol, Dir.Left) } +
            (0..maxCol).map { Cell(0, it, Dir.Down) } +
            (0..maxCol).map { Cell(maxRow, it, Dir.Up) }
}