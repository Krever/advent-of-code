package day18

import arrow.core.extensions.set.foldable.exists
import utils.Utils
import utils.Coords


fun main() {
    val input = Utils.loadResource("/day18.txt")
    val parsed = parseInput(input)
    val numOfBorders = parsed.sumOf { it.len.toLong() }
    val lines = buildLines(parsed)
    println(result(lines, numOfBorders))
}

data class DigLine(val dir: Dir, val len: Long, val color: String)
typealias Input = List<DigLine>

fun parseInput(input: String): Input {
    return input.lines()
        .map { line ->
            val split = line.split(" ")
            DigLine(
                dir = Dir.valueOf(split[0]),
                len = split[1].toLong(),
                color = split[2].removePrefix("(").removeSuffix(")")
            )
        }
}
// from is exclusive
data class Line(val start: Coords, val dir: Dir, val len: Long) {
    val end: Coords = start + (dir.asCoordChange() * len)
}

fun buildLines(input: Input): List<Line> {
    val init: List<Line> = listOf()
    return input.fold(init) { trench, line ->
        val start = trench.lastOrNull()?.end ?: Coords(0, 0)
        trench.plus(Line(start, line.dir, line.len))
    }
}


enum class Dir {
    L, R, U, D;

    fun asCoordChange(): Coords = when (this) {
        L -> Coords(0, -1)
        R -> Coords(0, 1)
        U -> Coords(-1, 0)
        D -> Coords(1, 0)
    }
}


fun shoelace(lines: List<Line>): Long =
    lines.sumOf { l ->
        (l.start.col - l.end.col).toLong() * (l.end.row + l.start.row)
    } / 2

// https://old.reddit.com/r/adventofcode/comments/18l2tap/2023_day_18_the_elves_and_the_shoemaker/kdw7rqk/
fun result(lines: List<Line>, numOfBorders: Long): Long = shoelace(lines) + numOfBorders / 2 + 1