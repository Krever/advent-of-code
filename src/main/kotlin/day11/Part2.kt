package day11

import day10.Dir.*
import utils.Utils
import kotlin.math.max
import kotlin.math.min


fun main() {
    val input = Utils.loadResource("/day11.txt")
    val parsed = parseInput(input)
    val galaxies = findGalaxies(parsed)
    val pairs = generateUnorderedPairs(galaxies)
    val sizes = createSizeLists(parsed)
    val result = pairs.sumOf { calculateDistance2(it.first, it.second, sizes) }
    print(result)
}


// index to column/row size
typealias SizeMap = Map<Int, Int>

// lines, columns
typealias Sizes = Pair<SizeMap, SizeMap>

fun createSizeLists(input: Input): Sizes {
    val sizeMultiplier = 1000000
    val linesSizes =
        input.mapIndexed { idx, line -> if (line.contains("#")) idx to 1 else idx to sizeMultiplier }.toMap()
    val colsSizes = input[0].indices.map { i ->
        val columnEmpty = input.count { it[i] == '#' } == 0
        if (columnEmpty) i to sizeMultiplier else i to 1
    }.toMap()
    return linesSizes to colsSizes
}

fun calculateDistance2(a: Coords, b: Coords, sizes: Sizes): Long {
    val yDist = ((min(a.first, b.first) + 1)..max(a.first, b.first))
        .sumOf { sizes.first[it]!!.toLong() }

    val xDist = ((min(a.second, b.second) + 1)..max(a.second, b.second))
        .sumOf { sizes.second[it]!!.toLong() }

    return xDist + yDist
}
