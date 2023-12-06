package day5

import java.io.File
import java.util.stream.StreamSupport
import kotlin.math.min

typealias Location = Long

fun Almanac.resolveSeedsAsRanges(): Sequence<Location> {
    return this.seeds.chunked(2).asSequence().mapIndexed() { idx, it ->
        val start = it[0]
        val end = start + it[1]
        val rangeResult = StreamSupport.stream(start.rangeUntil(end).spliterator(), true).map {
            if (it % 1000000 == 0L) {
                println("Range ${idx}: done ${1 - (end.toDouble() - it) / (end - start)}")
            }
            resolveSeed(it).location
        }.reduce() { a, b -> min(a, b) }.get()
        println("Range ${idx}: ${rangeResult}")
        rangeResult
    }
}

fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        val lines = it.toList()
        val almanac = parseAlmanac(lines)
        almanac.resolveSeedsAsRanges().min()
    }

    println(result)
}
