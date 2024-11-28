package org.example.day1.day15

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day15.txt")
    val parsed = parseInput(input)
    val result = parsed.map { hash(it) }
    println(result.sum())
}

typealias Input = List<String>

fun parseInput(input: String): Input = input.split(",")

fun hash(str: String) = str.fold(0) { hash, char -> (hash + char.code) * 17 % 256 }