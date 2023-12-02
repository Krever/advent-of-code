package org.example.day2

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import java.io.File
import kotlin.math.max

data class Cubes(val green: Int, val blue: Int, val red: Int) {
    fun lessThanEq(o: Cubes) = this.green <= o.green &&
            this.blue <= o.blue &&
            this.red <= o.red
}

data class Game(val id: Int, val samples: List<Cubes>) {
    val max: Cubes =
        samples.fold(Cubes(0, 0, 0)) { acc, elem ->
            Cubes(
                max(acc.green, elem.green),
                max(acc.blue, elem.blue),
                max(acc.red, elem.red)
            )
        }
}

fun main(args: Array<String>) {

    val limits = Cubes(13, 14, 12)

    val result = File(args[0]).useLines {
        it.filter { it.isNotEmpty() }
            .map { GameParser.parseToEnd(it) }
            .filter { it.max.lessThanEq(limits) }
            .sumOf { it.id }
    }
    println(result)
}

object GameParser : Grammar<Game>() {
    val gamePrefix by literalToken("Game ")
    val int by regexToken("[0-9]+")
    val colon by literalToken(":")
    val semicolon by literalToken(";")
    val comma by literalToken(",")
    val color by regexToken("(blue|red|green)")
    val space by literalToken(" ")

    val prefixParser by (gamePrefix * int * colon).use { t2.text.toInt() }
    val colorValue by (space * int * space * color).map { t -> t.t4.text to t.t2.text.toInt() }
    val cubesParser by separatedTerms(colorValue, comma).map { t ->
        val map = t.toMap()
        Cubes(
            map.getOrElse("green") { 0 },
            map.getOrElse("blue") { 0 },
            map.getOrElse("red") { 0 }
        )
    }
    val lineParser by (prefixParser * separatedTerms(cubesParser, semicolon)).use { Game(t1, t2.toList()) }

    override val rootParser: Parser<Game> = lineParser
}
