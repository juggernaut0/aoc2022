fun main() {
    Day17.run(getInput())
}

object Day17 {
    fun run(input: String) {
        simulate(input, 2022)
        simulate(input, 1_000_000_000_000L)
    }

    fun simulate(input: String, n: Long) {
        val dashRock = RockShape("dash", "####")
        val plusRock = RockShape("plus",
            """
                .#.
                ###
                .#.
            """.trimIndent()
        )
        val lRock = RockShape("L",
            """
                ..#
                ..#
                ###
            """.trimIndent()
        )
        val lineRock = RockShape("line",
            """
                #
                #
                #
                #
            """.trimIndent()
        )
        val squareRock = RockShape("square",
            """
                ##
                ##
            """.trimIndent()
        )
        val rockList = listOf(dashRock, plusRock, lRock, lineRock, squareRock)

        val world = World(input.trim().asSequence().withIndex().cycle())
        val rocks = rockList.asSequence().cycle().withIndex().constrainOnce().iterator()

        lateinit var i1: Pair<Int, Int> // start of first cycle
        lateinit var i2: Pair<Int, Int> // start of second cycle
        // state to (# of rocks dropped, height)
        val seenStates = mutableMapOf<State, Pair<Int, Int>>()
        for ((i, rock) in rocks) {
            val new = i to world.top
            val old = seenStates.put(State((i % 5), world.jetI, world.topRocks()), new)
            if (old != null) {
                i1 = old
                i2 = new
                world.addRock(rock)
                break
            }

            if (i == n.toInt()) {
                println(world.top)
                return
            }

            world.addRock(rock)
        }

        val (start, startTop) = i1
        val cycleLength = i2.first - i1.first
        val cycleTop = i2.second
        val cycleHeight = i2.second - i1.second
        val times = (n - start) / cycleLength
        val rem = (n - start) % cycleLength

        for (rock in rocks.take(rem.toInt() - 1)) {
            world.addRock(rock.value)
        }
        val remHeight = world.top - cycleTop

        println(startTop + times * cycleHeight + remHeight)
    }

    data class State(val rockI: Int, val jetI: Int, val topRocks: Set<Point>)

    class RockShape(val name: String, val points: Set<Point>) {
        constructor(name: String, art: String) : this(name, kotlin.run {
            val points = mutableSetOf<Point>()
            for ((y, line) in art.trim().lines().reversed().withIndex()) {
                for ((x, c) in line.withIndex()) {
                    if (c == '#') {
                        points.add(Point(x, y))
                    }
                }
            }
            points
        })
    }

    class World(jets: Sequence<IndexedValue<Char>>) {
        val jets = jets.iterator()
        val rocks: MutableSet<Point> = mutableSetOf()
        var top = 0
        var jetI = 0

        fun addRock(shape: RockShape, verbose: Boolean = false) {
            var pos = Point(2, top + 3)
            if (verbose) println("drop $pos")

            while (true) {
                val (i, jet) = jets.next()
                jetI = i

                val newPos = if (jet == '<') {
                    pos.diff(x = -1)
                } else {
                    pos.diff(x = 1)
                }
                if (shape.points.all { isValid(newPos + it) }) {
                    pos = newPos
                }
                if (verbose) println("jet $jet $pos")

                val newPoints = shape.points.map { pos + it }
                if (newPoints.any { it.y == 0 || it.diff(y = -1) in rocks }) {
                    rocks.addAll(newPoints)
                    top = rocks.maxOf { it.y } + 1
                    break
                } else {
                    pos = pos.diff(y = -1)
                }
            }

            // cleanup rocks that don't matter
            rocks.removeIf { it.y < top - 200 }
        }

        fun isValid(p: Point): Boolean {
            return p.x in 0..6 && p.y >= 0 && p !in rocks
        }

        fun visualizeTop() {
            for (y in (top - 1) downTo (top - 20)) {
                print("|")
                for (x in 0..6) {
                    if (Point(x, y) in rocks) {
                        print('#')
                    } else {
                        print(' ')
                    }
                }
                print("|")
                println()
            }
        }

        fun topRocks(): Set<Point> {
            return rocks.filter { it.y >= top - 50 }.map { it.diff(y = -(top - 50)) }.toSet()
        }
    }

    private fun <T> Sequence<T>.cycle(): Sequence<T> {
        return sequence {
            while (true) {
                yieldAll(this@cycle)
            }
        }
    }

    private fun <T> Iterator<T>.take(n: Int): Sequence<T> {
        return sequence {
            var left = n
            while (hasNext() && left > 0) {
                yield(next())
                left--
            }
        }
    }
}
