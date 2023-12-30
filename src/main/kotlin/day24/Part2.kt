package day24

import com.microsoft.z3.ArithExpr
import com.microsoft.z3.BoolExpr
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.RatNum
import utils.Utils

/**
 * Download Z3 binaries and add put the dir in -Djava.library.path
 */
fun main() {
    val z3Path = "/Users/krever/Downloads/z3-4.12.4-arm64-osx-11.0/bin"
    System.load("${z3Path}/libz3.dylib")
    System.load("${z3Path}/libz3java.dylib")
    val input = Utils.loadResource("/day24.txt")
    val parsed: Input = parseInput(input)
    print(solve(parsed))
}

fun solve(input: Input): Long = run {
    val ctx = Context()
    val solver = ctx.mkSolver()
    val (x, y, z) = listOf("x", "y", "z").map { ctx.mkRealConst(it) }
    val (vx, vy, vz) = listOf("vx", "vy", "vz").map { ctx.mkRealConst(it) }
    val solution = with(ctx) {
        repeat(3) {
            val p = input[it]
            val t = ctx.mkRealConst("t$it")
            solver.add((x + vx * t).eq(p.position.x.asZ3() + p.velocity.x.asZ3() * t))
            solver.add((y + vy * t).eq(p.position.y.asZ3() + p.velocity.y.asZ3() * t))
            solver.add((z + vz * t).eq(p.position.z.asZ3() + p.velocity.z.asZ3() * t))
        }
        solver.check()
        val model = solver.model
        model.eval(x + y + z, false)
    }
    return solution.toString().toLong()
}

typealias Z3Context = com.microsoft.z3.Context

context (Z3Context) operator fun <R : com.microsoft.z3.ArithSort> Expr<R>.plus(other: Expr<R>): ArithExpr<R> =
    this@Z3Context.mkAdd(this@Expr, other)

context (Z3Context) operator fun <R : com.microsoft.z3.ArithSort> Expr<R>.times(other: Expr<R>): ArithExpr<R> =
    this@Z3Context.mkMul(this@Expr, other)

context (Z3Context) fun <R : com.microsoft.z3.ArithSort> Expr<R>.eq(other: Expr<out R>): BoolExpr =
    this@Z3Context.mkEq(this@Expr, other)

context (Z3Context) fun Double.asZ3(): RatNum =
    this@Z3Context.mkReal(this@Double.toString())

