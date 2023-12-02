package org.example.day2

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import java.io.File
import kotlin.math.max

fun Cubes.power(): Int {
    return this.red * this.blue * this.green
}

fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        it.filter { it.isNotEmpty() }
            .map { GameParser.parseToEnd(it) }
            .sumOf { it.max.power() }
    }
    println(result)
}
