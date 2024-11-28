package org.example.day1.day8

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day8.txt")
    val parsed = parseInput(input)
    val numOfSteps = findNumOfStepsTillFinish2(parsed)
    println(numOfSteps) // strip the start node
}

typealias StepMod = Int
typealias Step = Long
typealias Endings = Map<Pair<StepMod, Node>, Step>

fun findNumOfStepsTillFinish2(input: Input): Long {
    val starts = input.nodes.keys.filter { it.endsWith('A') }
    tailrec fun findAllEndings(from: Node, step: Step, endings: Endings): Endings {
        val nextDir = input.directions.getDirection(step)
        val stepMod = step.mod(input.directions.length)
        val nextNode = input.nodes.getNextNode(from, nextDir)
        val key = stepMod to nextNode
        return if (endings.contains(key)) endings
        else {
            val newEndings =
                if (nextNode.endsWith('Z')) endings.plus(key to step) else endings
            findAllEndings(nextNode, step + 1, newEndings)
        }
    }

    val allEndings = starts.map { node ->
        val endings = findAllEndings(node, 0, mapOf())
        // all start points seems to have only one possible ending, solution relies on that
        assert(endings.size == 1)
        endings.toList()[0]
    }
    // all start points seems to converge on the same modulo, rest of solution relies on that
    assert(allEndings.map { it.first.first }.distinct().size == 1)
    val allEndingsSteps = allEndings.map { it.second + 1 } // plus one to adjust for 0-indexing io steps

    return findLCMOfListOfNumbers(allEndingsSteps)
}

// Least common multiple, https://www.baeldung.com/kotlin/lcm
fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm.mod(a) == 0L && lcm.mod(b) == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = findLCM(result, numbers[i])
    }
    return result
}
