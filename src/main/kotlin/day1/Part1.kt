package org.example.day1

import java.io.File

fun main(args: Array<String>) {
    val result = File(args[0]).useLines {
        it.filter { it.isNotEmpty() }
            .sumOf { line ->
                val first = line.find { it.isDigit() }
                val last = line.findLast { it.isDigit() }
                "$first$last".toInt()
            }
    }
    print(result)
}