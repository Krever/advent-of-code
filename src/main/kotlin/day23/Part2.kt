package day23

import utils.Coords
import utils.Utils
import kotlin.math.max

fun main() {
    val input = Utils.loadResource("/day23.txt")
    val parsed: Input = parseInput(input)
    val graph = buildGraph(parsed)
    val hike = hike2(graph)
    println(hike)
}

data class Edge(val weight: Int, val destination: Coords)
typealias Graph = Map<Coords, Set<Edge>>

fun buildGraph(input: Input): Graph {
    val start = Coords(0, input.first().indexOfFirst { it == '.' }.toLong())
    val end = Coords(input.size.toLong() - 1, input.last().indexOfFirst { it == '.' }.toLong())

    data class QElem(val start: Coords, val prev: Coords, val at: Coords, val path: Int)

    tailrec fun step(queue: List<QElem>, acc: Graph): Graph {
        return if (queue.isEmpty()) acc
        else {
            val cur = queue.first()
            val neighbours: List<Coords> = validNeighbours(cur.at, input).filter { it != cur.prev }
            when (neighbours.size) {
                0 -> {
                    val newGraph = if (cur.at == end) {
                        val newEdge = Edge(cur.path, cur.at)
                        acc + (cur.start to acc.getOrDefault(cur.start, setOf()).plus(newEdge))
                    } else acc
                    step(queue.drop(1), newGraph)
                }

                1 -> {
                    val newElem = QElem(cur.start, cur.at, neighbours[0], cur.path + 1)
                    step(listOf(newElem) + queue.drop(1), acc)
                }

                else -> {
                    val newEdge = Edge(cur.path, cur.at)
                    if (acc[cur.start]?.contains(newEdge) == true) step(queue.drop(1), acc)
                    else {
                        val newGraph = acc + (cur.start to acc.getOrDefault(cur.start, setOf()).plus(newEdge))
                        val newQueue = neighbours.map { n -> QElem(cur.at, cur.at, n, 1) } + queue.drop(1)
                        step(newQueue, newGraph)
                    }
                }
            }
        }
    }

    val unidirected = step(listOf(QElem(start, start, start, 0)), mapOf())
    val reversed: Graph = unidirected.flatMap { (node, edges) ->
        edges.map { e -> e.destination to Edge(e.weight, node) }
    }.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }
    val bidirect = (unidirected.keys + reversed.keys).associateWith {
        unidirected.getOrDefault(it, setOf()) + reversed.getOrDefault(it, setOf())
    }
    return bidirect
}

fun hike2(graph: Graph): Int {
    val start = graph.keys.find { it.row == 0L }!!
    val end = graph.keys.maxBy { it.row }

    data class QueueElem(val at: Coords, val path: Set<Coords>, val pathLen: Int)

    tailrec fun step(queue: List<QueueElem>, maxLen: Int): Int {
        return if (queue.isEmpty()) maxLen
        else {
            val cur = queue.first()
            val hikeLength = cur.pathLen
            println("${queue.size} $hikeLength ${maxLen}")
            if (cur.at == end) step(queue.drop(1), max(maxLen, hikeLength))
            else {
                val nextSteps = graph[cur.at]!!
                    .filter { !cur.path.contains(it.destination) }
                val newQueue =
                    nextSteps.map {
                        QueueElem(
                            it.destination,
                            cur.path + cur.at,
                            cur.pathLen + it.weight
                        )
                    } + queue.drop(1)
                step(newQueue, maxLen)
            }
        }
    }
    return step(listOf(QueueElem(start, setOf(), 0)), 0)
}

fun validNeighbours(from: Coords, input: Input): List<Coords> = listOf(
    from.plus(-1, 0),
    from.plus(1, 0),
    from.plus(0, -1),
    from.plus(0, 1),
).filter { point -> point.row >= 0 && point.row < input.size && point.col >= 0 && point.col < input.first().length }
    .filter { point -> input[point.row.toInt()][point.col.toInt()] != '#' }.map { it }