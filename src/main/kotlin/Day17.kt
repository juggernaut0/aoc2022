fun main() {
    Day17.run(getInput())
}

object Day17 {
    fun run(input: String) {
        //simulate(input, 2022)
        simulate(input, 1_000_000_000_000L)
    }

    fun simulate(input: String, n: Long) {
        val jets = input.trim().asSequence().cycle()

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
        val (start, cycleLength) = findCycleLength(rockList, input)
        println("cyclelength $cycleLength")
        println("start $start")
        val div = (n - start) / cycleLength
        val mod = (n - start) % cycleLength
        println("divmod $div $mod")

        val world = World(input.trim().asSequence().withIndex().cycle())
        val rocks = rockList.asSequence().cycle().constrainOnce().iterator()
        for (rock in rocks.take(start)) {
            world.addRock(rock)
        }
        val startTop = world.top
        for (rock in rocks.take(cycleLength)) {
            world.addRock(rock)
        }
        val cycleTop = world.top
        val cycleHeight = world.top - startTop
        for (rock in rocks.take(mod.toInt())) {
            world.addRock(rock)
        }
        val remHeight = world.top - cycleTop
        println("$startTop $cycleTop $cycleHeight")

        println(startTop + div * cycleHeight + remHeight)

        /*val world = World(input.trim().asSequence().withIndex().cycle())
        val rocks = rockList.asSequence().cycle().constrainOnce().iterator()
        for (rock in rocks.take(start)) {
            //println("adding ${rock.name}")
            world.addRock(rock)
        }
        println(world.top)
        //println(world.topRocks())
        world.visualizeTop()
        for (n in 0..2) {
            for (rock in rocks.take(cycleLength)) {
                //println("adding ${rock.name}")
                world.addRock(rock)
            }
            println(world.top)
            world.visualizeTop()
        }*/

        /*val tops = rocks.mapIndexed { i, it ->
            world.addRock(it, verbose = i <= 3)
            world.top
        }.toList()
        val topTop = world.top
        val rocks2 = sequenceOf(dashRock, plusRock, lRock, lineRock, squareRock).cycle().take(cycleLength)
        val tops2 = rocks2.mapIndexed { i, it ->
            world.addRock(it, verbose = i <= 3)
            val tr = world.topRocks()
            world.top - topTop
        }.toList()
        val diffI = tops.zip(tops2).indexOfFirst { (a, b) -> a != b }

        println()*/

        /*val world = World(jets)
        for (rock in rocks) {
            world.addRock(rock)
        }
        println(divtop + world.top)*/
    }

    data class Cycle(val start: Int, val length: Int)
    data class State(val rockI: Int, val jetI: Int, val topRocks: Set<LongPoint>)
    fun findCycleLength(rockList: List<RockShape>, input: String): Cycle {
        val rocks = rockList.asSequence().cycle().withIndex().take(3_000_000)
        val world = World(input.trim().asSequence().withIndex().cycle())
        var i1 = -1
        var i2 = -1
        val map = mutableMapOf<State, Int>()
        for ((i, rock) in rocks) {
            val old = map.put(State((i % 5), world.jetI, world.topRocks()), i)
            if (old != null) {
                //println(world.topRocks())
                world.visualizeTop()
                println(world.top)
                println("found a repeat with rock $old and $i")
                i1 = old
                i2 = i
                break
            }
            world.addRock(rock)
            //cyclelength 1730
            //start 2597
            /*if (world.jetI == 0) {
                if (i1 < 0) {
                    i1 = i
                } else if (i2 < 0) {
                    i2 = i
                } else {
                    break
                }
            }*/
        }
        check(i1 > 0) { "Failed to find first reset point" }
        check(i2 > 0) { "Failed to find second reset point" }
        val length = i2 - i1
        return Cycle(i1, length)
    }

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
        val rocks: MutableSet<LongPoint> = mutableSetOf()
        var top = 0L
        var jetI = 0

        fun addRock(shape: RockShape, verbose: Boolean = false) {
            var pos = LongPoint(2, top + 3)
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
                if (newPoints.any { it.y == 0L || it.diff(y = -1) in rocks }) {
                    rocks.addAll(newPoints)
                    //top += shape.height
                    top = rocks.maxOf { it.y } + 1
                    break
                } else {
                    pos = pos.diff(y = -1)
                }
            }

            // cleanup rocks that don't matter

            rocks.removeIf { it.y < top - 200 }
        }

        fun isValid(p: LongPoint): Boolean {
            return p.x in 0..6 && p.y >= 0 && p !in rocks
        }

        fun visualizeTop() {
            for (y in (top - 1) downTo (top - 20)) {
                print("|")
                for (x in 0L..6) {
                    if (LongPoint(x, y) in rocks) {
                        print('#')
                    } else {
                        print(' ')
                    }
                }
                print("|")
                println()
            }
        }

        fun topRocks(): Set<LongPoint> {
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

    private fun <T> Sequence<T>.takeLong(n: Long): Sequence<T> {
        return sequence {
            val underlying = iterator()

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