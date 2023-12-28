package day23

import utils.Coords
import utils.Utils
import kotlin.math.max

fun main() {
    val input = Utils.loadResource("/day23.txt")
    val parsed: Input = parseInput(input)
    val result = hike(parsed)
    println(result)
}

typealias Input = List<String>

fun parseInput(input: String): Input = input.lines()

fun hike(input: Input): Int {
    data class QueueElem(val path: List<Coords>, val toward: Coords)
    val start = Coords(0, input.first().indexOfFirst { it == '.' }.toLong())
    val end = Coords(input.size.toLong() - 1, input.last().indexOfFirst { it == '.' }.toLong())
    tailrec fun step(queue: List<QueueElem>, maxLen: Int): Int {
        return if (queue.isEmpty()) maxLen
        else {
            val nextElem = queue.first()
            val hikeLength = nextElem.path.size + 1
            if (nextElem.toward == end) step(queue.drop(1), max(maxLen, hikeLength))
            else {
                val nextSteps = possibleSteps(nextElem.toward, input, nextElem.path)
                val newQueue = queue.drop(1) + nextSteps.map { QueueElem(nextElem.path.plus(nextElem.toward), it) }
                step(newQueue, maxLen)
            }
        }
    }
    return step(listOf(QueueElem(listOf(), start)), 0) - 1 // -1 because start doesn't count
}

fun possibleSteps(from: Coords, input: Input, path: List<Coords>): List<Coords> =
    listOf(
        from.plus(-1, 0) to setOf('.', '^'),
        from.plus(1, 0) to setOf('.', 'v'),
        from.plus(0, -1) to setOf('.', '<'),
        from.plus(0, 1) to setOf('.', '>')
    )
        .filter { (point, _) -> point.row >= 0 && point.row < input.size && point.col >= 0 && point.col < input.first().length }
        .filter { (point, allowedChars) -> allowedChars.contains(input[point.row.toInt()][point.col.toInt()]) }
        .filter { (point, _) -> !path.contains(point) }
        .map { it.first }