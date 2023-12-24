package day21

import arrow.core.extensions.list.foldable.exists
import arrow.core.extensions.map.foldable.forAll
import arrow.mtl.Coreader
import utils.Coords
import utils.Utils

fun main() {
    val input = Utils.loadResource("/day21.txt")
    val parsed: Input = input.lines()
    val start = findStart(parsed)
    val positions = possiblePlacements(parsed, start, 64)
    println(positions.size)
}

typealias Input = List<String>

fun findStart(input: Input): Coords = run {
    val row = input.indexOfFirst { it.contains("S") }
    val col = input[row].indexOfFirst { it == 'S' }
    Coords(row.toLong(), col.toLong())
}

fun possiblePlacements(input: Input, start: Coords, numOfSteps: Int): Set<Coords> = run {
    fun run(stepsRemaining: Int, positions: Set<Coords>): Set<Coords> = run {
        if (stepsRemaining == 0) positions
        else {
            val newPositions = positions.flatMap { reachableNeighbours(input, it) }.toSet()
            run(stepsRemaining - 1, newPositions)
        }
    }
    run(numOfSteps, setOf(start))
}

fun reachableNeighbours(input: Input, point: Coords): Set<Coords> = run {
    listOf(
        point.plus(0, 1),
        point.plus(1, 0),
        point.plus(0, -1),
        point.plus(-1, 0)
    ).filter { it.row >= 0 && it.col >= 0 && it.row < input.size && it.col < input[0].length }
        .filter { input[it.row.toInt()][it.col.toInt()] != '#' }
        .toSet()
}
