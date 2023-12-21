package day17

import utils.Utils


fun main() {
    val input = Utils.loadResource("/day17.txt")
    val parsed = parseInput(input)
    val result = dijkstra(parsed, parsed.target(), ::nextSteps2)
    println(result)
}

fun nextSteps2(input: Input, step: Step): List<Step> {
    val pos = step.pos
    val next = Dir.entries
        .filter { it != opposite(step.dir) }
        .map {
            val nextPos = when (it) {
                Dir.Left -> (pos.first) to (pos.second - 1)
                Dir.Right -> (pos.first) to (pos.second + 1)
                Dir.Up -> (pos.first - 1) to pos.second
                Dir.Down -> (pos.first + 1) to pos.second
            }
            val stepsInDir = if (step.dir == it) step.stepsInCurrentDir + 1 else 1
            Step(nextPos, it, stepsInDir)
        }
        .filter { x ->
            x.pos.first >= 0 && x.pos.first < input.size &&
                    x.pos.second >= 0 && x.pos.second < input.first().length &&
                    ((x.dir == step.dir && x.stepsInCurrentDir <= 10) ||
                            (x.dir != step.dir && step.stepsInCurrentDir >= 4))
        }
    return next
}