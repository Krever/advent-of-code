package day4

import org.example.day3.findNumbers
import org.example.day3.isAdjacentToSymbol
import java.io.File
import kotlin.math.pow

data class Scratchcard(val id: Int, val matches: Int)

fun main(args: Array<String>) {
    val result = File(args[0]).useLines {
        val scratchcards = it.filter { it.isNotEmpty() }
            .mapIndexed { idx, line ->
                val tokens = line.split("\\s".toRegex()).filter { it.isNotEmpty() }
                    .drop(2) // game prefix
                val winning = tokens.takeWhile { it != "|" }.toSet()
                val owned = tokens.drop(winning.size + 1).toSet() // 1 for `|`
                val matches = winning.intersect(owned)
                Scratchcard(idx + 1, matches.size)
            }
        calculate(scratchcards)
    }
    println(result)
}

typealias CardId = Int
typealias NumOfInstances = Int

fun calculate(input: Sequence<Scratchcard>): Int {
    val buffer: MutableMap<CardId, Pair<Scratchcard, NumOfInstances>> =
        input.map { it.id to (it to 1) }.toMap().toMutableMap()

    buffer.forEach { id, (card, numOfInstances) ->
        (1..card.matches)
            .forEach {
                val wonCardId = id + it
                buffer.computeIfPresent(wonCardId) { _, (wonCard, wonCardInstances) -> wonCard to (wonCardInstances + numOfInstances) }
            }
    }

    return buffer.toList().sumOf { it.second.second }
}