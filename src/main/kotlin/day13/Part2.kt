package day13

import utils.Utils


fun main() {
    val input = Utils.loadResource("/day13.txt")
    val parsed = parseInput(input)
    val result = parsed.sumOf { pattern ->
        val horizontal = pattern.findReflection2()?.plus(1)?.times(100)
        val vertical = pattern.transposed().findReflection2()?.plus(1)
        val result = horizontal ?: vertical
        println(result)
        result!!
    }
    println(result)
}

fun Pattern.findReflection2(): Int? = (0..(raw.size - 2)).find { checkIfReflection2(this, it) }

fun checkIfReflection2(pattern: Pattern, index: Int): Boolean {
    fun step(distance: Int, allowedError: Int): Boolean {
        val checkedLine = index - distance
        val opposite = index + 1 + distance
        return if (checkedLine < 0 || opposite >= pattern.raw.size) allowedError == 0 // otherwise smudge wasn corrected
        else {
            val errors = countDifferences(pattern.raw[checkedLine], pattern.raw[opposite])
            if (errors <= allowedError) step(distance + 1, allowedError - errors)
            else false
        }
    }
    return step(0, 1)
}

fun countDifferences(str1: String, str2: String): Int {
    require(str1.length == str2.length) { "Strings must be of the same length" }
    return str1.indices.count { str1[it] != str2[it] }
}

