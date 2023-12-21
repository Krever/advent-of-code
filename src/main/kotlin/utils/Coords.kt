package utils

data class Coords(val row: Long, val col: Long) {
    operator fun times(x: Long): Coords =
        Coords(row * x, col * x)

    operator fun plus(other: Coords): Coords =
        Coords(row + other.row, col + other.col)

    fun isAbove(other: Coords): Boolean = col == other.col && row < other.row
}