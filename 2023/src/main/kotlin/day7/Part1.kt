package org.example.day1.day7

import arrow.core.extensions.map.foldable.exists
import org.example.day1.utils.Utils


fun main() {
    val input = Utils.loadResource("/day7.txt")
    val parsed = parseInput(input)
    val result = calculateTotalWinnings(parsed, ComparisonStrategy())
    println(result)
}

typealias Hand = String
typealias Card = Char
typealias Bid = Int
typealias Input = List<Pair<Hand, Bid>>

fun parseInput(input: String): Input {
    return input.lines().map {
        val split = it.split(" ")
        split[0] to split[1].toInt()
    }
}

fun calculateTotalWinnings(data: List<Pair<Hand, Bid>>, strategy: ComparisonStrategy): Int {
    val sorted = data.sortedWith(compareBy(strategy.handComparator) { it.first })
    sorted.forEach { println("${it} ${strategy.detectHandType(it.first)}") }
    return sorted.mapIndexed { idx, (_, bid) ->
        val rank = idx + 1
        bid * rank
    }.sum()
}

open class ComparisonStrategy {
    protected open val cardsOrder = "AKQJT98765432"

    private val cardComparator: Comparator<Card> = compareBy { cardsOrder.indexOf(it) }

    open fun detectHandType(hand: Hand): HandType {
        val lettersMap = hand.lettersMap()
        return if (lettersMap.size == 1) return HandType.FiveOfAKind
        else if (lettersMap.exists { it == 4 }) HandType.FourOfAKind
        else if (lettersMap.exists { it == 3 } && lettersMap.exists { it == 2 }) HandType.FullHouse
        else if (lettersMap.exists { it == 3 }) HandType.ThreeOfAKind
        else if (lettersMap.count { it.value == 2 } == 2) HandType.TwoPair
        else if (lettersMap.exists { it == 2 }) HandType.OnePair
        else HandType.HighCard
    }

    val handComparator: Comparator<Hand> =
        compareBy<Hand> { hand -> handTypesOrder.indexOf(detectHandType(hand)) }
            .thenBy(cardComparator) { it[0] }
            .thenBy(cardComparator) { it[1] }
            .thenBy(cardComparator) { it[2] }
            .thenBy(cardComparator) { it[3] }
            .thenBy(cardComparator) { it[4] }
            .reversed()

    protected fun Hand.lettersMap(): Map<Char, Int> = this.groupingBy { it }.eachCount()
}

val handTypesOrder = listOf(
    HandType.FiveOfAKind,
    HandType.FourOfAKind,
    HandType.FullHouse,
    HandType.ThreeOfAKind,
    HandType.TwoPair,
    HandType.OnePair,
    HandType.HighCard,
)

enum class HandType {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard;
}
