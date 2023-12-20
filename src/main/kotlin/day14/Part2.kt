package day14

import utils.Utils


fun main() {
    val input = Utils.loadResource("/day14.txt")
    val parsed = parsePlatform(input)
    val (cyclePrefix, cycle) = rotateUntilLoop(parsed)
    println("Cycle: $cyclePrefix ${cycle.size}")
    val resultIndex = (1000000000 - cyclePrefix) % cycle.size
    val resulPlatform = cycle[resultIndex]
    val load = calculateRawLoad(resulPlatform)
    println(load)
}

// (row, col)
typealias Coords = Pair<Int, Int>

data class Platform(val rows: Int, val cols: Int, val rocks: Set<Coords>, val barriers: Set<Coords>)

enum class Dir {
    N, E, W, S
}

fun parsePlatform(input: String): Platform {
    val lines = input.lines()
    val rocks = mutableListOf<Coords>()
    val barriers = mutableListOf<Coords>()
    for ((row, line) in lines.withIndex()) {
        rocks.addAll(line.withIndex().filter { it.value == 'O' }.map { row to it.index })
        barriers.addAll(line.withIndex().filter { it.value == '#' }.map { row to it.index })
    }
    return Platform(lines.size, lines[0].length, rocks.toSet(), barriers.toSet())
}

fun tilt(platform: Platform, dir: Dir): Platform {
    val newRocks = mutableListOf<Coords>()
    if (dir == Dir.N || dir == Dir.S) {
        for (col in 0..<platform.cols) {
            val barriers = platform.barriers.filter { it.second == col }.map { it.first }
            val rocks = platform.rocks.filter { it.second == col }.map { it.first }
            (listOf(-1) + barriers + listOf(platform.rows)).zipWithNext().forEach { (from, to) ->
                val numOfRocks = rocks.count { pos -> pos in from..<to }
                val start = if (dir == Dir.N) from + 1 else to - numOfRocks
                (0..<numOfRocks).forEach { newRocks.add((start + it) to col) }
            }
        }
    } else {
        for (row in 0..<platform.rows) {
            val barriers = platform.barriers.filter { it.first == row }.map { it.second }
            val rocks = platform.rocks.filter { it.first == row }.map { it.second }
            (listOf(-1) + barriers + listOf(platform.cols)).zipWithNext().forEach { (from, to) ->
                val numOfRocks = rocks.count { pos -> pos in from..<to }
                val start = if (dir == Dir.W) from + 1 else to - numOfRocks
                (0..<numOfRocks).forEach { newRocks.add(row to (start + it)) }
            }
        }
    }
    return Platform(platform.rows, platform.cols, newRocks.toSet(), platform.barriers)
}

fun render(p: Platform) {
    val matrix = MutableList(p.rows) { MutableList(p.cols) { '.' } }
    for (r in p.rocks) {
        matrix[r.first].set(r.second, 'O')
    }
    for (b in p.barriers) {
        matrix[b.first].set(b.second, '#')
    }
    matrix.forEach { println(it.joinToString("")) }
    println()
}

fun cycle(p: Platform): Platform {
    val a = tilt(p, Dir.N)
    val b = tilt(a, Dir.W)
    val c = tilt(b, Dir.S)
    return tilt(c, Dir.E)
}
typealias CyclePrefix = Int

fun rotateUntilLoop(p: Platform): Pair<CyclePrefix, List<Platform>> {
    val seenPlatforms = mutableMapOf<Platform, Int>()
    var current = p
    while (!seenPlatforms.contains(current)) {
        seenPlatforms[current] = seenPlatforms.size
        current = cycle(current)
    }
    val cycleStart = seenPlatforms[current]!!
    val cycle = (cycleStart..<seenPlatforms.size).toList()
        .map { idx -> seenPlatforms.toList().find { it.second == idx }!!.first }
    val cyclePrefix = seenPlatforms.size - cycle.size
    return cyclePrefix to cycle
}

fun calculateRawLoad(p: Platform): Int = p.rocks.sumOf { p.rows - it.first }
