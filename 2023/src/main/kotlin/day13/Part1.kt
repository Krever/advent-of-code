package org.example.day1.day13

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day13.txt")
    val parsed = parseInput(input)
    val result = parsed.sumOf { pattern ->
        val horizontal = pattern.findReflection()?.plus(1)?.times(100)
        val vertical = pattern.transposed().findReflection()?.plus(1)
        val result = vertical ?: horizontal
        println(result)
        result!!
    }
    println(result)
}

typealias Input = List<Pattern>

data class Pattern(val raw: List<String>) {
    fun transposed() = Pattern(transpose(raw))

    fun findReflection(): Int? = (0..(raw.size - 2)).find { checkIfReflection(this, it) }
}

fun parseInput(input: String): Input {
    val patterns = input.split("\n\n")
    return patterns.map { Pattern(it.lines()) }
}


fun transpose(input: List<String>): List<String> {
    if (input.isEmpty()) return emptyList()
    val rowLength = input.first().length
    return (0 until rowLength).map { column ->
        input.joinToString(separator = "") { it[column].toString() }
    }
}

fun checkIfReflection(pattern: Pattern, index: Int): Boolean {
    fun step(distance: Int): Boolean {
        val checkedLine = index - distance
        val opposite = index + 1 + distance
        return if (checkedLine < 0 || opposite >= pattern.raw.size) true
        else if (pattern.raw[checkedLine] == pattern.raw[opposite]) step(distance + 1)
        else false
    }
    return step(0)
}

