package org.example.day3

import java.io.File


data class Coord(val col: Int, val row: Int)
data class GearCandidate(val number: Number, val adjacentAsterisks: Set<Coord>)

fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        val engineSchematic: Schematic = it.toList()
        val candidates = findNumbers(engineSchematic)
            .mapNotNull { isGearCandidate(engineSchematic, it) }

        val gears: List<Pair<GearCandidate, GearCandidate>> = candidates.flatMapIndexed { cIdx, candidate ->
            candidates.filterIndexed() { oIdx, other ->
                candidate != other &&
                        candidate.adjacentAsterisks.intersect(other.adjacentAsterisks).isNotEmpty() &&
                        cIdx < oIdx // to remove duplicates
            }
                .map { candidate to it }
        }
        gears
            .sumOf { p -> p.first.number.intValue() * p.second.number.intValue() }
    }

    println(result)
}


fun isGearCandidate(s: Schematic, number: Number): GearCandidate? {
    val adjacentCells = (listOf(
        number.col - 1 to number.row - 1,
        number.col - 1 to number.row,
        number.col - 1 to number.row + 1,
        number.col + number.value.length to number.row - 1,
        number.col + number.value.length to number.row,
        number.col + number.value.length to number.row + 1,
    ) + (0.rangeUntil(number.value.length)).flatMap { idx ->
        listOf(
            number.col + idx to number.row - 1,
            number.col + idx to number.row + 1
        )
    }).map { Coord(it.first, it.second) }
    val adjacentAsterisks = adjacentCells.mapNotNull { coord ->
        val cell = s.getOrNull(coord.row)?.getOrNull(coord.col)
        if (cell == '*') coord
        else null
    }

    return if (adjacentAsterisks.isEmpty()) null
    else GearCandidate(number, adjacentAsterisks.toSet())
}
