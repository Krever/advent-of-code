package org.example.day3

import java.io.File

typealias Schematic = List<String>

data class Number(val row: Int, val col: Int, val value: String) {
    fun intValue() = value.toInt()
}


fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        val engineSchematic: Schematic = it.toList()
        findNumbers(engineSchematic)
            .filter { isAdjacentToSymbol(engineSchematic, it) }
            .sumOf { it.intValue() }
    }

    println(result)
}

fun findNumbers(s: Schematic): List<Number> {
    val regex = Regex("[0-9]+")
    return s.flatMapIndexed { rowIdx, line ->
        regex.findAll(line).map { result -> Number(rowIdx, result.range.first, result.value) }
    }
}

fun isAdjacentToSymbol(s: Schematic, number: Number): Boolean {
    val adjacentCells = listOf(
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
    }

    return adjacentCells.find { coord ->
        val cell = s.getOrNull(coord.second)?.getOrNull(coord.first)
        cell != null && cell != '.'
    } != null
}
