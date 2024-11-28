package org.example.day1.day24

import org.example.day1.utils.Utils

fun main() {
    val input = Utils.loadResource("/day24.txt")
    val parsed: Input = parseInput(input)
    val numOfColliding = checkCollisions(parsed)
    println(numOfColliding)
}

data class Vec2(val x: Double, val y: Double)
data class Vec3(val x: Double, val y: Double, val z: Double)
data class Particle(val position: Vec3, val velocity: Vec3) {
    val xFun: LinFun = LinFun(velocity.x, position.x)
    val yFun: LinFun = LinFun(velocity.y, position.y)

    fun isInTheFuture(v: Vec2): Boolean = xFun.unapply(v.x) > 0 && yFun.unapply(v.y) > 0
}
typealias Input = List<Particle>

fun parseInput(input: String): Input = input.lines().map { line ->
    val split = line.split(" @ ")
    Particle(parseVec(split[0]), parseVec(split[1]))
}

fun parseVec(str: String): Vec3 = run {
    val split = str.split(", ")
    Vec3(split[0].trim().toDouble(), split[1].trim().toDouble(), split[2].trim().toDouble())
}

// f(x) = ax + b
data class LinFun(val a: Double, val b: Double) {
    fun apply(v: Double) = a + v * b
    fun unapply(v: Double) = (v - b) / a
}

sealed interface Intersection {
    data object All : Intersection // the same line
    data object None : Intersection // parallel
    data class Point(val value: Vec2) : Intersection // crossing
}

fun checkCollision(p1: Particle, p2: Particle): Intersection = run {
    val p1f = compound(p1.xFun, p1.yFun)
    val p2f = compound(p2.xFun, p2.yFun)
    val x = calcIntersection(p1f, p2f)
    x
}

fun calcIntersection(f1: LinFun, f2: LinFun): Intersection = run {
    if (f1.a == f2.a) {
        if (f1.b == f2.b) Intersection.All
        else Intersection.None
    } else {
        val x = (f2.b - f1.b) / (f1.a - f2.a)
        val y = (f1.a * f2.b - f2.a * f1.b) / (f1.a - f2.a)
        Intersection.Point(Vec2(x, y))
    }
}


// expreses f2 in terms of f1
fun compound(f1: LinFun, f2: LinFun): LinFun = run {
    val a = (f2.a / f1.a)
    val b = -(f2.a / f1.a) * f1.b + f2.b
    LinFun(a, b)
}

fun checkCollisions(input: Input) = run {
    val bounds = 200000000000000.0..400000000000000.0
//    val bounds = 7.0..27.0
    input.indices.flatMap { i -> (i + 1 until input.size).map { i to it } }
        .count {
            val p1 = input[it.first]
            val p2 = input[it.second]
            val col = checkCollision(p1, p2)
            println(p1)
            println(p2)
            println(checkCollision(p1, p2))
            println()
            when (col) {
                Intersection.All -> true
                Intersection.None -> false
                is Intersection.Point ->
                    p1.isInTheFuture(col.value) &&
                            p2.isInTheFuture(col.value) &&
                            bounds.contains(col.value.x) &&
                            bounds.contains(col.value.y)
            }
        }
}