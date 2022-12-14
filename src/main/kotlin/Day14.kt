fun main() {
    Day14.run(getInput())
}

object Day14 {
    @Suppress("ControlFlowWithEmptyBody")
    fun run(input: String) {
        val paths = input.lines().map { line ->
            val points = line.split(" -> ").map {
                val parts = it.split(',')
                Point(parts[0].toInt(), parts[1].toInt())
            }
            Path(points)
        }

        val maxY = paths.maxOf { it.points.maxOf { p -> p.y } }

        val walls = mutableSetOf<Point>()
        for (path in paths) {
            val pointPairs = path.points.windowed(2)
            for ((p1, p2) in pointPairs) {
                if (p1.x == p2.x) {
                    val (min, max) = if (p1.y < p2.y) p1.y to p2.y else p2.y to p1.y
                    for (y in min..max) {
                        walls.add(Point(p1.x, y))
                    }
                } else {
                    val (min, max) = if (p1.x < p2.x) p1.x to p2.x else p2.x to p1.x
                    for (x in min..max) {
                        walls.add(Point(x, p1.y))
                    }
                }
            }
        }

        run {
            val world = World(walls.toSet(), mutableSetOf(), maxY + 1)

            while (world.drop()) { }
            println(world.sand.size)
        }

        for (x in -5..1005) {
            walls.add(Point(x, maxY + 2))
        }

        run {
            val world = World(walls.toSet(), mutableSetOf(), maxY + 3)

            while (world.drop()) { }
            println(world.sand.size)
        }
    }

    class Path(val points: List<Point>)

    class World(private val walls: Set<Point>, val sand: MutableSet<Point>, private val abyss: Int) {
        // returns true if the simulation continues
        fun drop(): Boolean {
            var sand = Point(500, 0)
            if (!isEmpty(sand)) return false
            while (sand.y < abyss) {
                val down = sand.diff(y = 1)
                val downLeft = sand.diff(x = -1, y = 1)
                val downRight = sand.diff(x = 1, y = 1)
                sand = if (isEmpty(down)) {
                    down
                } else if (isEmpty(downLeft)) {
                    downLeft
                } else if (isEmpty(downRight)) {
                    downRight
                } else {
                    this.sand.add(sand)
                    return true
                }
            }
            return false
        }

        private fun isEmpty(p: Point): Boolean {
            return p !in walls && p !in sand
        }
    }
}
