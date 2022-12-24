fun main() {
    Day24.run(getInput())
}

object Day24 {
    fun run(input: String) {
        val map = input.toGrid { c, _ ->
            when (c) {
                '.' -> Tile.Open(emptySet())
                '#' -> Tile.Wall
                '^' -> Tile.Open(setOf(Dir.U))
                'v' -> Tile.Open(setOf(Dir.D))
                '<' -> Tile.Open(setOf(Dir.L))
                '>' -> Tile.Open(setOf(Dir.R))
                else -> error(c)
            }
        }
        // note: 5 is a hardcoded GCF of input dimensions
        val maps = generateSequence(map) { nextMap(it) }.take((map.width() - 2) * (map.height() - 2) / 5).toList()

        part1(maps)
        part2(maps)
    }

    private fun part1(maps: List<Grid<Tile>>) {
        val start = Point(1, 0)
        val end = Point(maps[0].width() - 2, maps[0].height() - 1)

        println(pathTime(maps, start, end, 0))
    }

    private fun part2(maps: List<Grid<Tile>>) {
        val start = Point(1, 0)
        val end = Point(maps[0].width() - 2, maps[0].height() - 1)

        val a = pathTime(maps, start, end, 0)
        val b = pathTime(maps, end, start, a)
        val c = pathTime(maps, start, end, b)
        println(c)
    }

    private fun pathTime(maps: List<Grid<Tile>>, start: Point, end: Point, startTime: Int): Int {
        val goDown = start.y < end.y

        val queue = ArrayDeque<State>()
        val seen = mutableMapOf<State.Key, Int>()
        queue.add(State(start, startTime, maps))
        var best = Int.MAX_VALUE
        while (queue.isNotEmpty()) {
            val state = queue.removeLast()
            if (state.time + state.pos.l1distance(end) >= best) continue
            val key = state.key()
            if (state.time >= (seen[key] ?: Int.MAX_VALUE)) {
                continue
            } else {
                seen[key] = state.time
            }
            if (state.pos == end) {
                if (state.time < best) {
                    best = state.time
                    continue
                }
            }
            val next = state.next(goDown)
            queue.addAll(next.reversed())
        }
        return best
    }

    enum class Dir { U, D, L, R }
    sealed interface Tile {
        object Wall : Tile
        data class Open(val dirs: Set<Dir>) : Tile {
            fun clear(): Boolean {
                return dirs.isEmpty()
            }
        }

        fun isOpen(): Boolean {
            return this is Open && clear()
        }
    }

    class State(val pos: Point, val time: Int, private val maps: List<Grid<Tile>>) {
        fun next(goDown: Boolean): List<State> {
            val newMap = maps[(time + 1) % maps.size]

            val u = pos.diff(y = -1)
            val d = pos.diff(y =  1)
            val l = pos.diff(x = -1)
            val r = pos.diff(x =  1)

            val dirs = if (goDown) {
                listOf(d, r, pos, u, l)
            } else {
                listOf(u, l, pos, d, r)
            }

            return dirs
                .filter { p -> p in newMap && newMap[p].isOpen() }
                .map { State(it, time + 1, maps) }
        }

        data class Key(val pos: Point, val mapI: Int)
        fun key() = Key(pos, time % maps.size)
    }

    private fun nextMap(map: Grid<Tile>): Grid<Tile> {
        return Grid(map.width(), map.height()) { p ->
            when (map[p]) {
                Tile.Wall -> Tile.Wall
                is Tile.Open -> {
                    if (p.y == 0 || p.y == map.height() - 1) return@Grid Tile.Open(emptySet())
                    val u = if (p.y == 1) p.copy(y = map.height() - 2) else p.diff(y = -1)
                    val d = if (p.y == map.height() - 2) p.copy(y = 1) else p.diff(y =  1)
                    val l = if (p.x == 1) p.copy(x = map.width() - 2) else p.diff(x = -1)
                    val r = if (p.x == map.width() - 2) p.copy(x = 1) else p.diff(x =  1)

                    val blizzards = mutableSetOf<Dir>()
                    if (Dir.U in (map[d] as Tile.Open).dirs) {
                        blizzards.add(Dir.U)
                    }
                    if (Dir.D in (map[u] as Tile.Open).dirs) {
                        blizzards.add(Dir.D)
                    }
                    if (Dir.L in (map[r] as Tile.Open).dirs) {
                        blizzards.add(Dir.L)
                    }
                    if (Dir.R in (map[l] as Tile.Open).dirs) {
                        blizzards.add(Dir.R)
                    }

                    Tile.Open(blizzards)
                }
            }
        }
    }
}
