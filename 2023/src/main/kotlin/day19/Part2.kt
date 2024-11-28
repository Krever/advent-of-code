package org.example.day1.day19

import org.example.day1.utils.Utils
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = Utils.loadResource("/day19.txt")
    val parsed = parseInput(input)
    val ranges = calculateRanges(parsed.workflows)
    val possibilities = ranges.sumOf { c ->
        c.m.count().toLong() * c.x.count() * c.a.count() * c.s.count()
    }
    println(possibilities)
}

data class Criteria(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange)

fun calculateRanges(workflows: List<Workflow>): Set<Criteria> {
    fun step(workflow: Workflow, range: Criteria): Set<Criteria> = run {
        val resultsInit: Set<Criteria> = setOf()
        workflow.rules.fold(range to resultsInit) { (curRange, results), rule ->
            val constrainedRange = constrain(curRange, rule, opposite = false)
            val newResults = when (rule.result) {
                is RuleResult.DecisionResult -> when (rule.result.decision) {
                    Decision.A -> setOf(constrainedRange)
                    Decision.R -> setOf()
                }

                is RuleResult.WorkflowResult -> step(workflows.find { it.name == rule.result.name }!!, constrainedRange)
            }
            val nextRange = constrain(curRange, rule, opposite = true)
            nextRange to results + newResults
        }.second
    }

    val defaultRange = 1..4000
    return step(workflows.find { it.name == "in" }!!, Criteria(defaultRange, defaultRange, defaultRange, defaultRange))
}

fun constrain(crit: Criteria, rule: Rule, opposite: Boolean): Criteria = run {
    when (rule.condition) {
        null -> crit
        else -> {
            val newValue = rule.condition.threshold
            val oldRange = when (rule.condition.field) {
                PartCategory.X -> crit.x
                PartCategory.M -> crit.m
                PartCategory.A -> crit.a
                PartCategory.S -> crit.s
            }
            val newRange = when (rule.condition.sign) {
                Comparison.LT ->
                    if (!opposite) oldRange.first..min(oldRange.endInclusive, newValue - 1)
                    else max(oldRange.first, newValue)..oldRange.endInclusive

                Comparison.GT ->
                    if (!opposite) max(oldRange.first, newValue + 1)..oldRange.endInclusive
                    else oldRange.first..min(oldRange.endInclusive, newValue)
            }
            when (rule.condition.field) {
                PartCategory.X -> crit.copy(x = newRange)
                PartCategory.M -> crit.copy(m = newRange)
                PartCategory.A -> crit.copy(a = newRange)
                PartCategory.S -> crit.copy(s = newRange)
            }
        }
    }
}