import kotlin.math.absoluteValue

fun getInput(): String {
    val day = System.getProperty("aoc.day")!!
    val resName = "/day$day.txt"
    return object {}::class.java.getResourceAsStream(resName)
        .let { it ?: error("Unable to find input: $resName") }
        .reader()
        .readText()
}

data class Point(val x: Int, val y: Int) {
    fun diff(x: Int = 0, y: Int = 0): Point {
        return Point(this.x + x, this.y + y)
    }

    fun l1distance(other: Point): Int {
        return (x - other.x).absoluteValue + (y - other.y).absoluteValue
    }

    operator fun plus(other: Point): Point {
        return diff(other.x, other.y)
    }
}

data class LongPoint(val x: Long, val y: Long) {
    fun diff(x: Long = 0L, y: Long = 0L): LongPoint {
        return LongPoint(this.x + x, this.y + y)
    }

    operator fun plus(other: LongPoint): LongPoint {
        return diff(other.x, other.y)
    }

    operator fun plus(other: Point): LongPoint {
        return diff(other.x.toLong(), other.y.toLong())
    }
}

data class Point3(val x: Int, val y: Int, val z: Int) {
    fun diff(x: Int = 0, y: Int = 0, z: Int = 0): Point3 {
        return Point3(this.x + x, this.y + y, this.z + z)
    }

    fun neighbors(): List<Point3> {
        return listOf(
            diff(x = 1), diff(x = -1),
            diff(y = 1), diff(y = -1),
            diff(z = 1), diff(z = -1),
        )
    }
}

data class Grid<T>(val data: List<List<T>>) {
    operator fun contains(p: Point): Boolean {
        return p.y in data.indices && data.isNotEmpty() && p.x in data[p.y].indices
    }

    operator fun get(p: Point): T {
        return getOrNull(p) ?: throw IndexOutOfBoundsException(p.toString())
    }

    fun getOrNull(p: Point): T? {
        return if (p in this) data[p.y][p.x] else null
    }

    fun width() = data.getOrNull(0)?.size ?: 0
    fun height() = data.size

    fun points(): Sequence<Point> {
        return (0 until width())
            .asSequence()
            .flatMap { x -> (0 until height()).asSequence().map { y -> Point(x, y) } }
    }
}
fun <T> String.toGrid(mapper: (Char, Point) -> T): Grid<T> {
    val lines = trimEnd().lines()
    val data: MutableList<MutableList<T?>> = MutableList(lines.size) { mutableListOf() }
    for ((y, line) in lines.withIndex()) {
        data[y] = MutableList(line.length) { null }
        for ((x, c) in line.withIndex()) {
            data[y][x] = mapper(c, Point(x, y))
        }
    }
    @Suppress("UNCHECKED_CAST")
    return Grid(data) as Grid<T>
}

fun <T> Grid(width: Int, height: Int, fn: (Point) -> T): Grid<T> {
    val data = MutableList(height) { y -> MutableList(width) { x -> fn(Point(x, y)) } }
    return Grid(data)
}

tailrec fun gcd(a: Int, b: Int): Int {
    return if (b == 0) {
        a
    } else {
        gcd(b, a % b)
    }
}

fun lcm(a: Int, b: Int): Int {
    return (a * b) / gcd(a, b)
}

// optimizes for LOWEST metric value
interface Search<TState, TKey, TMetric : Comparable<TMetric>> {
    fun start(): TState

    fun TState.isGoal(): Boolean

    fun TState.key(): TKey

    fun TState.metric(): TMetric

    fun TState.nextStates(): List<TState>

    /**
     * @return an overestimation of the metric of the goal state following this state
     */
    fun TState.estimated(): TMetric? = null
}

fun <TState, TKey, TMetric : Comparable<TMetric>> Search<TState, TKey, TMetric>.breadthFirstSearch(): TMetric? {
    val queue = ArrayDeque<TState>()
    val seen = mutableMapOf<TKey, TMetric>()
    queue.add(start())
    while (queue.isNotEmpty()) {
        val state = queue.removeFirst()
        val metric = state.metric()

        if (state.isGoal()) {
            return metric
        }

        val key = state.key()
        if (seen[key]?.let { metric >= it } == true) {
            continue
        } else {
            seen[key] = metric
        }

        val next = state.nextStates()
        queue.addAll(next)
    }
    return null
}

fun <TState, TKey, TMetric : Comparable<TMetric>> Search<TState, TKey, TMetric>.depthFirstSearch(): TMetric? {
    val queue = mutableListOf(start())
    var best: TMetric? = null
    while (queue.isNotEmpty()) {
        val state = queue.removeLast()

        val estimated = state.estimated()
        if (best != null && estimated != null && estimated <= best) continue

        if (state.isGoal()) {
            val metric = state.metric()
            if (best == null || metric > best) {
                best = metric
            }
        } else {
            queue.addAll(state.nextStates())
        }
    }
    return best
}