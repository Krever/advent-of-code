package day5

import java.io.File

fun Almanac.resolveSeedsAsRanges(): Sequence<SeedRecipe> {
    return this.seeds.chunked(2).asSequence().mapIndexed() { idx, it ->
        val start = it[0]
        val end = start + it[1]
        val rangeResult = start.rangeUntil(end).asSequence().map {
            if (it % 1000000 == 0L) {
                println("Range ${idx}: done ${1 - (end.toDouble() - it) / (end - start)}")
            }
            resolveSeed(it)
        }.minBy { it.location }
        println("Range ${idx}: ${rangeResult}")
        rangeResult
    }
}

fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        val lines = it.toMutableList()
        val almanac = parseAlmanac(lines)
        almanac.resolveSeedsAsRanges().minOf { it.location }
    }

    println(result)
}
