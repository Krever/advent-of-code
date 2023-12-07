package day7

import arrow.core.extensions.map.foldable.exists
import utils.Utils


fun main() {
    val input = Utils.loadResource("/day7.txt")
    val parsed = parseInput(input)
    val result = calculateTotalWinnings(parsed, ComparisonStrategy2())
    println(result)
}


class ComparisonStrategy2 : ComparisonStrategy() {
    override val cardsOrder = "AKQT98765432J"

    override fun detectHandType(hand: Hand): HandType {
        val lettersMap = hand.lettersMap()
        return if (lettersMap.contains('J')) {
            val withoutJokersMap = lettersMap.filterKeys { it != 'J' }
            val numOfJokers = lettersMap.getOrElse('J') { 0 }
            if (withoutJokersMap.size <= 1) HandType.FiveOfAKind
            else if (withoutJokersMap.exists { it == 4 - numOfJokers }) HandType.FourOfAKind
            else if (withoutJokersMap.size == 2) HandType.FullHouse
            else if (withoutJokersMap.exists { it == 3 - numOfJokers }) HandType.ThreeOfAKind
            else if (numOfJokers >= 2 || withoutJokersMap.exists { it == 2 }) HandType.TwoPair
            else HandType.OnePair
        } else super.detectHandType(hand)
    }

}


