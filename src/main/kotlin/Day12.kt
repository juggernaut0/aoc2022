import java.util.Comparator
import java.util.PriorityQueue

fun main() {
    val input = getInput()
    Day12.run(input)
}

object Day12 {
    enum class Dir {
        N, E, S, W;

        fun go(p: Point): Point {
            return when (this) {
                N -> p.copy(y = p.y - 1)
                E -> p.copy(x = p.x + 1)
                S -> p.copy(y = p.y + 1)
                W -> p.copy(x = p.x - 1)
            }
        }
    }

    fun run(input: String) {
        lateinit var start: Point
        lateinit var goal: Point
        val grid = input.toGrid { c, p ->
            when (c) {
                'S' -> {
                    start = p
                    0
                }
                'E' -> {
                    goal = p
                    25
                }
                else -> c - 'a'
            }
        }

        fun invCanGo(curr: Int, d: Point): Boolean {
            val dh = grid.getOrNull(d) ?: return false
            return (curr - dh) <= 1
        }

        println(pathfind(grid, goal, ::invCanGo) { it == start })
        println(pathfind(grid, goal, ::invCanGo) { grid[it] == 0})
    }

    private fun pathfind(grid: Grid<Int>, start: Point, canGo: (Int, Point) -> Boolean, goalCond: (Point) -> Boolean): Int {
        val queue = PriorityQueue<List<Point>>(Comparator.comparing { t -> t.size })
        val nodes = mutableMapOf<Point, Int>()
        queue.add(listOf(start))
        nodes[start] = 1

        while (queue.isNotEmpty()) {
            val path = queue.remove()
            val curr = grid[path.last()]
            for (dir in Dir.values()) {
                val d = path.last().let { dir.go(it) }.takeIf { canGo(curr, it) } ?: continue

                if (goalCond(d)) {
                    return path.size
                }

                val newPath = path + d
                if (newPath.size < (nodes[d] ?: Int.MAX_VALUE)) {
                    queue.add(newPath)
                    nodes[d] = newPath.size
                }
            }
        }

        error("pathfinding failure")
    }
}