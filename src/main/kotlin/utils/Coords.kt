package utils

data class Coords(val row: Long, val col: Long) {
    operator fun times(x: Long): Coords =
        Coords(row * x, col * x)

    operator fun plus(other: Coords): Coords = plus(other.row, other.col)
    fun plus(row: Long, col: Long): Coords = Coords(this.row + row, this.col + col)

    fun isAbove(other: Coords): Boolean = col == other.col && row < other.row
}