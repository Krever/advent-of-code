package day16

import utils.Utils


fun main() {
    val input = Utils.loadResource("/day16.txt")
    val parsed = parseInput(input)
    val energised = runBeams(parsed, Cell(0, 0, Dir.Right)).map { it.row to it.col }.toSet()
    println(energised.size)
}

typealias Input = List<String>

fun parseInput(input: String): Input = input.lines()

enum class Dir {
    Left, Right, Up, Down
}

data class Cell(val row: Int, val col: Int, val dir: Dir)

fun runBeams(input: Input, start: Cell): Set<Cell> {
    tailrec fun run(energised: Set<Cell>, remaining: Set<Cell>): Set<Cell> {
        return if (remaining.isEmpty()) energised
        else {
            val current = remaining.first()
            val newRemaining = remaining.minus(current)
            if (energised.contains(current)) run(energised, remaining.minus(current))
            else {
                val newDirs = nextDirs(current.dir, input[current.row][current.col])
                val newCells = newDirs.map { dir ->
                    val posD = posChange(dir)
                    Cell(current.row + posD.first, current.col + posD.second, dir)
                }.filter { it.row >= 0 && it.row < input.size && it.col >= 0 && it.col < input.first().length }
                run(energised.plus(current), newRemaining + newCells)
            }

        }
    }
    return run(setOf(), setOf(start))
}

fun posChange(dir: Dir) = when (dir) {
    Dir.Left -> 0 to -1
    Dir.Right -> 0 to 1
    Dir.Up -> -1 to 0
    Dir.Down -> 1 to 0
}

fun nextDirs(dir: Dir, node: Char): List<Dir> {
    val sameDir = listOf(dir)
    return when (node) {
        '.' -> sameDir
        '/' -> when (dir) {
            Dir.Left -> listOf(Dir.Down)
            Dir.Right -> listOf(Dir.Up)
            Dir.Up -> listOf(Dir.Right)
            Dir.Down -> listOf(Dir.Left)
        }

        '\\' -> when (dir) {
            Dir.Left -> listOf(Dir.Up)
            Dir.Right -> listOf(Dir.Down)
            Dir.Up -> listOf(Dir.Left)
            Dir.Down -> listOf(Dir.Right)
        }

        '-' -> when (dir) {
            Dir.Left, Dir.Right -> sameDir
            Dir.Up, Dir.Down -> listOf(Dir.Left, Dir.Right)
        }

        '|' -> when (dir) {
            Dir.Left, Dir.Right -> listOf(Dir.Up, Dir.Down)
            Dir.Up, Dir.Down -> sameDir
        }

        else -> TODO()
    }
}