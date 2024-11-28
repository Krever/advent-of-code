package org.example.day1.day21

import org.example.day1.utils.Coords
import org.example.day1.utils.Utils

// put the points into some quadratic solver (or find the equation yourself)
// it prints out the f(x) for x=0,1,2,3
// answer if f(202300)
fun main() {
    val input = Utils.loadResource("/day21.txt")
    val parsed: Input = input.lines()
    val start = findStart(parsed)

    val steps = (0..3).map { 65 + it * 131 }
//    val steps = listOf(6, 10, 50, 100, 500, 1000, 5000)
    val sizes = steps.map {
        println( possiblePlacements2(parsed, start, it).size) }
    println(sizes)
}


fun possiblePlacements2(input: Input, start: Coords, numOfSteps: Int): Set<Coords> = run {
    fun run(stepsRemaining: Int, positions: Set<Coords>): Set<Coords> = run {
        if (stepsRemaining == 0) positions
        else {
            val newPositions = positions.flatMap { reachableNeighbours2(input, it) }.toSet()
            run(stepsRemaining - 1, newPositions)
        }
    }
    run(numOfSteps, setOf(start))
}

fun reachableNeighbours2(input: Input, point: Coords): Set<Coords> = run {
    listOf(
        point.plus(0, 1),
        point.plus(1, 0),
        point.plus(0, -1),
        point.plus(-1, 0)
    )
        .filter { p ->
            val row = Math.floorMod(p.row.toInt(), input.size)
            val col = Math.floorMod(p.col.toInt(), input[0].length)
            input[row][col] != '#'
        }
        .toSet()
}