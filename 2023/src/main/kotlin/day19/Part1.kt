package org.example.day1.day19

import org.example.day1.utils.Utils

fun main() {
    val input = Utils.loadResource("/day19.txt")
    val parsed = parseInput(input)
    println(evaluateParts(parsed))
}

data class Input(val workflows: List<Workflow>, val parts: List<Part>)
data class Workflow(val name: String, val rules: List<Rule>)
data class Rule(val condition: Condition?, val result: RuleResult)
data class Part(val x: Int, val m: Int, val a: Int, val s: Int)
data class Condition(val field: PartCategory, val sign: Comparison, val threshold: Int)
enum class PartCategory { X, M, A, S }
enum class Comparison { LT, GT }
sealed interface RuleResult {
    data class WorkflowResult(val name: String) : RuleResult
    data class DecisionResult(val decision: Decision) : RuleResult
}
enum class Decision { A, R }


fun parseInput(input: String): Input = run {
    val split = input.split("\n\n")
    val workflows = split[0].lines().map(::parseWorkflow)
    val parts = split[1].lines().map(::parsePart)
    Input(workflows, parts)
}

fun parsePart(s: String): Part = run {
    val props = s.removePrefix("{")
        .removeSuffix("}")
        .split(",")
        .associate { prop ->
            val split = prop.split("=")
            split[0] to split[1].toInt()
        }
    Part(props["x"]!!, props["m"]!!, props["a"]!!, props["s"]!!)
}

fun parseWorkflow(s: String): Workflow = run {
    val r = """([a-z]+)\{(.*)\}""".toRegex()
    val matchResult = r.matchEntire(s)!!.groupValues
    val name = matchResult[1]
    val rules = matchResult[2]
    val rulesParsed = rules.split(",").map { ruleStr ->
        val split = ruleStr.split(":")
        if (split.size == 1) {
            Rule(null, parseResult(split[0]))
        } else {
            Rule(parseCondition(split[0]), parseResult(split[1]))
        }
    }
    Workflow(name, rulesParsed)
}

fun parseResult(str: String): RuleResult = when (str) {
    "A" -> RuleResult.DecisionResult(Decision.A)
    "R" -> RuleResult.DecisionResult(Decision.R)
    else -> RuleResult.WorkflowResult(str)
}

fun parseCondition(str: String): Condition = run {
    val r = "([a-z]+)([><])([0-9]+)".toRegex()
    val matchResult = r.matchEntire(str)!!.groupValues
    val partCat = PartCategory.valueOf(matchResult[1].uppercase())
    val sign = when (matchResult[2]) {
        ">" -> Comparison.GT
        "<" -> Comparison.LT
        else -> TODO()
    }
    val threshold = matchResult[3].toInt()
    Condition(partCat, sign, threshold)
}

fun evaluateParts(input: Input): Int = run {
    input.parts.filter { evaluatePart(it, input.workflows) == Decision.A }
        .sumOf { p -> p.m + p.x + p.a + p.s }
}

fun evaluatePart(part: Part, workflows: List<Workflow>): Decision = run {
    tailrec fun step(workflow: Workflow): Decision {
        val result = evaluateWorkflow(workflow, part)
        return when (result) {
            is RuleResult.DecisionResult -> result.decision
            is RuleResult.WorkflowResult -> {
                val nextWf = workflows.find { it.name == result.name }!!
                step(nextWf)
            }
        }
    }
    step(workflows.find { it.name == "in" }!!)
}

fun evaluateWorkflow(workflow: Workflow, part: Part): RuleResult = run {
    workflow.rules.firstNotNullOf { evaluateRule(it, part) }
}

fun evaluateRule(rule: Rule, part: Part): RuleResult? =
    if (rule.condition == null) rule.result
    else {
        val c: Condition = rule.condition
        val value = when (c.field) {
            PartCategory.X -> part.x
            PartCategory.M -> part.m
            PartCategory.A -> part.a
            PartCategory.S -> part.s
        }
        val satisfied = when (c.sign) {
            Comparison.LT -> value < c.threshold
            Comparison.GT -> value > c.threshold
        }
        if (satisfied) rule.result else null
    }