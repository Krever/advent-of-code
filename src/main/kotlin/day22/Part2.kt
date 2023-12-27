package day22

import utils.Utils

fun main() {
    val input = Utils.loadResource("/day22.txt")
    val parsed: Input = parseInput(input)
    val stable = fallDown(parsed)
    val supportData = calculateSupport(parsed, stable)
    println(maxChainReaction(supportData))

}

fun maxChainReaction(data: SupportData): Int = run {

    tailrec fun step(toBeDestroyed: Set<Label>, alreadyDestroyed: Set<Label>): Set<Label> {
        return if (toBeDestroyed.isEmpty()) alreadyDestroyed
        else {
            val current = toBeDestroyed.first()
            val newDestroyed = alreadyDestroyed + current
            val reaction = data.supportedBy.filter { newDestroyed.containsAll(it.value) && it.value.isNotEmpty() }.keys - alreadyDestroyed
            val newQueue = toBeDestroyed + reaction - current
            step(newQueue, newDestroyed)
        }
    }

    val results = data.supports.keys.associateWith { step(setOf(it), setOf()) }
    results.toList().sumOf { it.second.minus(it.first).size }
}
