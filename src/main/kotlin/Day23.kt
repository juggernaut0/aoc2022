fun main() {
    Day23.run(getInput())
}

object Day23 {
    fun run(input: String) {
        val elves = input.lines().withIndex()
            .flatMap { (y, line) ->
                line.withIndex().mapNotNull { (x, c) -> if (c == '#') { Point(x, y) } else { null } }
            }
            .toSet()

        val after10 = (0 until 10).fold(elves, ::step)

        val minX = after10.minOf { it.x }
        val maxX = after10.maxOf { it.x }
        val minY = after10.minOf { it.y }
        val maxY = after10.maxOf { it.y }

        println(((maxX - minX + 1) * (maxY - minY + 1)) - after10.size)

        try {
            generateSequence(0) { it + 1 }.fold(elves, ::step)
        } catch (e: NoMovesException) {
            println(e.stepNum + 1)
            return
        }
        error("Part 2 failed")
    }

    enum class Dir { N, S, W, E }

    private fun step(elves: Set<Point>, stepNum: Int): Set<Point> {
        fun propose(p: Point): Point {
            val n = p.diff(y = -1)
            val ne = p.diff(x = 1, y = -1)
            val e = p.diff(x = 1)
            val se = p.diff(x = 1, y = 1)
            val s = p.diff(y = 1)
            val sw = p.diff(x = -1, y = 1)
            val w = p.diff(x = -1)
            val nw = p.diff(x = -1, y = -1)

            if (listOf(n, ne, e, se, s, sw, w, nw).all { it !in elves }) {
                return p
            }

            for (di in 0 until 4) {
                when (Dir.values()[(di + stepNum) % 4]) {
                    Dir.N -> {
                        if (n !in elves && ne !in elves && nw !in elves) {
                            return n
                        }
                    }
                    Dir.S -> {
                        if (s !in elves && se !in elves && sw !in elves) {
                            return s
                        }
                    }
                    Dir.W -> {
                        if (w !in elves && nw !in elves && sw !in elves) {
                            return w
                        }
                    }
                    Dir.E -> {
                        if (e !in elves && ne !in elves && se !in elves) {
                            return e
                        }
                    }
                }
            }

            return p
        }

        val proposed = elves.groupBy { propose(it) }
        val newSet = proposed.flatMap { (k, v) -> if (v.size == 1) listOf(k) else v }.toSet()
        check(newSet.size == elves.size) { "Lost an elf!" }

        if (newSet == elves) throw NoMovesException(stepNum)

        return newSet
    }

    class NoMovesException(val stepNum: Int): Throwable()
}