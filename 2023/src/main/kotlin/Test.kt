package org.example.day1

import java.io.File

fun main(args: Array<String>) {
    tailrec fun test(int: Int): Int {
        return test(1) + 1
    }
    test(1)

    listOf(1).redOr

    val x: IntRange = 1..2
}