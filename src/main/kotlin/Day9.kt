import kotlin.math.absoluteValue

fun main() {
    val moves = getInput().lines().map {
        val (dir, num) = it.split(' ', limit = 2)
        Move(Direction.valueOf(dir), num.toInt())
    }

    println(simulate(Rope(2), moves))
    println(simulate(Rope(10), moves))
}

private fun simulate(rope: Rope, moves: List<Move>): Int {
    val points = mutableSetOf(rope.tails.last())
    for (move in moves) {
        repeat(move.num) {
            rope.move(move.direction)
            points.add(rope.tails.last())
        }
    }
    return points.size
}

private enum class Direction { U, D, L, R }
private data class Move(val direction: Direction, val num: Int)
private class Rope(var head: Point, var tails: List<Point>) {
    constructor(length: Int) : this(Point(0, 0), List(length - 1) { Point(0, 0) })

    fun move(direction: Direction) {
        val newHead = when (direction) {
            Direction.U -> head.copy(y = head.y + 1)
            Direction.D -> head.copy(y = head.y - 1)
            Direction.L -> head.copy(x = head.x - 1)
            Direction.R -> head.copy(x = head.x + 1)
        }

        val newTails = tails.scan(newHead) { h, tail ->
            val dx = (h.x - tail.x)
            val dy = (h.y - tail.y)

            val newTail = if (dx == 0 && dy.absoluteValue > 1) {
                tail.copy(y = tail.y + dy.coerceIn(-1..1))
            } else if (dy == 0 && dx.absoluteValue > 1) {
                tail.copy(x = tail.x + dx.coerceIn(-1..1))
            } else if (dx.absoluteValue > 1 || dy.absoluteValue > 1) {
                Point(x = tail.x + dx.coerceIn(-1..1), y = tail.y + dy.coerceIn(-1..1))
            } else {
                tail
            }

            newTail
        }

        head = newHead
        tails = newTails.drop(1)
    }
}
