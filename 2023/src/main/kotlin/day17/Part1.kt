package org.example.day1.day17

import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day17.txt")
    val parsed = parseInput(input)
    val result = dijkstra(parsed, parsed.target(), ::nextSteps)
    println(result)
}

typealias Input = List<String>

fun parseInput(input: String): Input = input.lines()

typealias Coords = Pair<Int, Int>

enum class Dir {
    Left, Right, Up, Down
}

fun Input.target() = (this.size - 1) to (this[0].length - 1)
fun Input.value(pos: Coords) = this[pos.first][pos.second].digitToInt()

data class Step(val pos: Coords, val dir: Dir, val stepsInCurrentDir: Int)

fun dijkstra(input: Input, target: Coords, nextSteps: (input: Input, step: Step) -> List<Step>): Int {
    val dist = mutableMapOf<Step, Int>()
    val prev = mutableMapOf<Step, Step>()
    val Q = mutableSetOf<Step>()
    Q.add(Step(0 to 1, Dir.Right, 1))
    Q.add(Step(1 to 0, Dir.Down, 1))
    dist[Step(0 to 1, Dir.Right, 1)] = input.value(0 to 1)
    dist[Step(1 to 0, Dir.Down, 1)] = input.value(1 to 0)
    val visited = mutableSetOf<Step>()
    while (Q.isNotEmpty()) {
        println("${Q.size} ${visited.size}")
        val u = Q.minBy { dist[it]!! }
        if (u.pos == target && u.stepsInCurrentDir >= 4) break
        Q.remove(u)
        visited.add(u)
        val connected = nextSteps(input, u).filter { e -> !visited.contains(e) }
        Q.addAll(connected)
        for (v in connected) {
            val alt = dist[u]!! + input.value(v.pos)
            if (alt < (dist[v] ?: Int.MAX_VALUE)) {
                dist[v] = alt
                prev[v] = u
            }
        }
    }
    render(input, getPath(input, prev))
    return dist.toList().find { it.first.pos == input.target() && it.first.stepsInCurrentDir >= 4  }!!.second
}

fun getPath(input: Input, prevs: MutableMap<Step, Step>): List<Step> {
    val S = mutableListOf<Step>()
    var u: Step? = prevs.toList().find { it.first.pos == input.target() && it.first.stepsInCurrentDir >= 4 }!!.first
    while (u != null) {
        S.add(u)
        u = prevs[u]
    }
    return S
}

fun render(input: Input, path: List<Step>): Unit {
    val matrix = input.map { it.toMutableList() }.toMutableList()
    for (s in path) {
        matrix[s.pos.first][s.pos.second] = when (s.dir) {
            Dir.Left -> '<'
            Dir.Right -> '>'
            Dir.Up -> '^'
            Dir.Down -> 'V'
        }
    }
    println(matrix.map { it.joinToString("") }.joinToString("\n"))
}

fun nextSteps(input: Input, step: Step): List<Step> {
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
                    x.stepsInCurrentDir <= 3
        }
    return next
}

fun opposite(dir: Dir) = when (dir) {
    Dir.Left -> Dir.Right
    Dir.Right -> Dir.Left
    Dir.Up -> Dir.Down
    Dir.Down -> Dir.Up
}