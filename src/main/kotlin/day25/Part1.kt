package day25

import org.jgrapht.Graph
import org.jgrapht.alg.flow.DinicMFImpl
import org.jgrapht.alg.flow.EdmondsKarpMFImpl
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import utils.Utils
import kotlin.random.Random


fun main() {
    val input = Utils.loadResource("/day25.txt")
    val parsed: Input = parseInput(input)
    val graph = buildGraph(parsed)
    val answer = solvePart1(graph)
    println(answer)
}

typealias NodeId = String

data class Connections(val from: NodeId, val to: List<NodeId>)
typealias Input = List<Connections>

fun parseInput(input: String): Input = input.lines().map { line ->
    val split = line.split(": ")
    Connections(split[0], split[1].split(" "))
}

typealias E = DefaultEdge

fun buildGraph(input: Input): Graph<NodeId, E> = run {
    val nodes = input.map { it.from } + input.flatMap { it.to }
    val edges = input.flatMap { c -> c.to.map { c.from to it } }
    val graph = DefaultUndirectedGraph<NodeId, DefaultEdge>(DefaultEdge::class.java)

    nodes.forEach { graph.addVertex(it) }
    edges.forEach { graph.addEdge(it.first, it.second) }
    graph
}

fun solvePart1(graph: Graph<NodeId, E>): Long = run {
    var edges: Set<DefaultEdge> = setOf()
    val algorithm = EdmondsKarpMFImpl(graph)
    while(edges.size != 3) {
        val (start, end) = graph.vertexSet().toList().shuffled()
        algorithm.calculateMaximumFlow(start, end)
        edges = algorithm.cutEdges
        println(edges)
    }
    algorithm.sourcePartition.size.toLong() * algorithm.sinkPartition.size
}