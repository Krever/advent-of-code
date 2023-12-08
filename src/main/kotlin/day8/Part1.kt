package day8

import utils.Utils


fun main() {
    val input = Utils.loadResource("/day8.txt")
    val parsed = parseInput(input)
    val path = findPath(parsed, "AAA", "ZZZ")
    println(path)
    println(path.size - 1) // strip the start node
}

typealias Node = String
typealias Dirs = String
typealias Direction = Char // L or R
typealias NodesMap = Map<Node, Pair<Node, Node>>

data class Input(val directions: Dirs, val nodes: NodesMap)

fun parseInput(input: String): Input {
    val lines = input.lines()
    val directions = lines[0]
    val nodes = lines.drop(2).map { line ->
        val split = line.split(" = ")
        val id = split[0]
        val dirs = split[1]
        val dirsSplit = dirs.removePrefix("(").removeSuffix(")").split(", ")
        val left = dirsSplit[0]
        val right = dirsSplit[1]
        id to (left to right)
    }
    return Input(directions, nodes.toMap())
}

fun Dirs.getDirection(step: Long): Direction {
    return this[step.mod(this.length)]
}

fun NodesMap.getNextNode(from: Node, direction: Direction): Node {
    return if (direction == 'L') this[from]!!.first
    else this[from]!!.second
}

fun findPath(input: Input, start: Node, destination: Node): List<Node> {
    tailrec fun step(currentNode: Node, step: Long, path: List<Node>): List<Node> {
        val nextDir = input.directions.getDirection(step)
        val nextNode = input.nodes.getNextNode(currentNode, nextDir)
        val newPath = path.plus(nextNode)
        return if (nextNode == destination) newPath
        else step(nextNode, step + 1, newPath)
    }
    return step(start, 2147000000, listOf(start))
}
