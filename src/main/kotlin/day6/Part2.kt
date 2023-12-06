package day6

import java.io.File


fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        val lines = it.toList()
        val time = lines[0].split(":")[1].replace(" ", "").toLong()
        val distance = lines[1].split(":")[1].replace(" ", "").toLong()
        val solutions = solveAsEquation(time, distance)
        val cutOffs = convertToCutoffValues(solutions)
        println("time: ${time}, dist: ${distance}, solutions: ${cutOffs}")
        val numOfSolution = cutOffs.second - cutOffs.first + 1
        numOfSolution
    }

    println(result)
}