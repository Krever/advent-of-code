package org.example.day1.day4

import java.io.File
import kotlin.math.pow


fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        it.filter { it.isNotEmpty() }
            .map { line ->
                val tokens = line.split("\\s".toRegex()).filter { it.isNotEmpty() }
                    .drop(2) // game prefix
                val winning = tokens.takeWhile { it != "|" }.toSet()
                val owned = tokens.drop(winning.size + 1).toSet() // 1 for `|`
                val matches = winning.intersect(owned)
                if (matches.isNotEmpty()) 2.0.pow(matches.size - 1).toInt()
                else 0
            }
            .sum()
    }

    println(result)
}
