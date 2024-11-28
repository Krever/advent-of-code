package org.example.day1.day20

import org.example.day1.utils.Utils

fun main() {
    val input = Utils.loadResource("/day20.txt")
    val parsed = parseInput(input)
    parsed.find { it.name == "broadcaster" }!!.destinations.forEach {
        runForSingleBroadcast(it, parsed)
    }
}

fun runForSingleBroadcast(m: ModuleId, input: Input) {
    val modifiedInput = input.map { if (it.name == "broadcaster") it.copy(destinations = listOf(m)) else it }
    var state = buildInitialState(modifiedInput)
    var stats = SignalsSent(0, 0, false)
    var numOfPresses = 0
    while (true) {
        if (stats.rxHandled) break
        else {
            val (newState, newStats) = pressButton(state)
            state = newState
            stats = stats + newStats
            numOfPresses += 1
        }
    }
    println(numOfPresses)

}