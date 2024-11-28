package org.example.day1

import java.io.File

val digitsAsWords: Map<String, Int> = listOf(
    "one",
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
).mapIndexed { id, word -> word to id + 1 }.toMap()
val digitsAsChars = (1..9).map { it.digitToChar().toString() to it }
val digitsToInt = digitsAsWords + digitsAsChars

val rawRegex = "(${digitsToInt.keys.joinToString("|")})"
//words may overlap
val regex = Regex("(?=($rawRegex)).")

fun main(args: Array<String>) {
    println(regex)
    val result = File(args[0]).useLines {
        it.filter { it.isNotEmpty() }.sumOf { line ->
            val allMatches = regex.findAll(line).toList().map { it.groupValues.get(1) }
            val first = digitsToInt.get(allMatches.first())
            val last = digitsToInt.get(allMatches.last())
            println("$line $first$last")
            "$first$last".toInt()
        }
    }
    print(result)
}