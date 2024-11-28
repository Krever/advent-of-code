package org.example.day1.day6

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sqrt


fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        val lines = it.toList()
        val times = lines[0].split("\\s+".toRegex()).drop(1).map { it.toLong() }
        val distances = lines[1].split("\\s+".toRegex()).drop(1).map { it.toLong() }
        times.zip(distances).map { (time, distance) ->
            val solutions = solveAsEquation(time, distance)
            val cutOffs = convertToCutoffValues(solutions)
            println("time: ${time}, dist: ${distance}, solutions: ${solutions}")
            val numOfSolution = cutOffs.second - cutOffs.first + 1
            numOfSolution
        }.reduce { a, b -> a * b }
    }
    println(result)


}

/*
x - num of ms pressing (not known)
y - num of ms available (known)
z - distance to cross (known)

x * (y-x) > z
-x^2 + yx - z > 0
a = -1, b = y, c = -z

d = b^2 - 4ac
d = y^2 - 4z
x1 = (-b - sqrt(d))/2a
x1 = (-y - sqrt(d))/(-2)
x2 = (-y + sqrt(d))/(-2)

 */
fun solveAsEquation(time: Long, distance: Long): Pair<Double, Double> {
    val y = time.toDouble()
    val z = distance.toDouble()
    val d = y * y - 4 * z
    val x1 = (-y - sqrt(d)) / -2
    val x2 = (-y + sqrt(d)) / -2
    return x2 to x1
}

fun convertToCutoffValues(solution: Pair<Double, Double>): Pair<Int, Int> {
    return Math.ceil(solution.first.absoluteValue).toInt() to Math.floor(solution.second.absoluteValue).toInt()
}