package day15

import arrow.core.Either
import arrow.core.extensions.list.foldable.nonEmpty
import utils.Utils


fun main() {
    val input = Utils.loadResource("/day15.txt")
    val parsed = parseInitSequence(input)
    val lenses = installLenses(parsed)
    println(sumUp(lenses))
}

typealias Remove = Unit
typealias FocalLength = Int
typealias Label = String

data class Step(val label: Label, val operation: Either<Remove, FocalLength>)
typealias InitSequence = List<Step>

fun parseInitSequence(str: String): InitSequence = str.split(",").map {
    val last = it.last()
    if (last == '-') Step(it.dropLast(1), Either.left(Unit))
    else Step(it.dropLast(2), Either.right(last.digitToInt()))
}

data class Lens(val label: String, val length: FocalLength)

fun installLenses(seq: InitSequence): Map<Int, List<Lens>> {
    val init: Map<Int, List<Lens>> = mapOf()
    return seq.fold(init) { boxes, step ->
        val labelHash = hash(step.label)
        val lenses = boxes.getOrDefault(labelHash, listOf())
        val newLenses = step.operation.fold(
            { remove ->
                lenses.mapNotNull { if (it.label == step.label) null else it }
            },
            { insert ->
                var updated = false
                val newLens = Lens(step.label, insert)
                val updatedList = lenses.map {
                    if (it.label == step.label) {
                        updated = true
                        newLens
                    } else it
                }
                if (!updated) lenses.plus(newLens) else updatedList
            }
        )
        val newBoxes = boxes.plus(labelHash to newLenses)
        render(step, newBoxes)
        newBoxes
    }
}

fun sumUp(state: Map<Int, List<Lens>>): Int {
    return state.toList().sumOf { (boxNum, lenses) ->
        lenses.withIndex().sumOf {
            (1 + boxNum) * (1 + it.index) * it.value.length
        }
    }
}

fun render(step: Step, state: Map<Int, List<Lens>>) {
    val op = step.operation.fold({ "-" }, { "=${it}" })
    println("After \"${step.label}${op}\":")
    state.toList().sortedBy { it.first }
        .forEach { (id, lenses) ->
            if (lenses.nonEmpty()) {
                val lensesRendered = lenses.joinToString(separator = " ") { "[${it.label} ${it.length}]" }
                println("Box ${id}: ${lensesRendered}")
            }
        }
    println()
}