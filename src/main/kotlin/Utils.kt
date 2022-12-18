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
    operator fun get(p: Point): T {
        return getOrNull(p) ?: throw IndexOutOfBoundsException(p.toString())
    }

    fun getOrNull(p: Point): T? {
        return if (p.y in data.indices && data.isNotEmpty() && p.x in data[0].indices) {
            data[p.y][p.x]
        } else {
            null
        }
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
    val lines = trim().lines()
    val data: MutableList<MutableList<T?>> = MutableList(lines.size) { MutableList(lines[0].length) { null } }
    for ((y, line) in lines.withIndex()) {
        for ((x, c) in line.withIndex()) {
            data[y][x] = mapper(c, Point(x, y))
        }
    }
    @Suppress("UNCHECKED_CAST")
    return Grid(data) as Grid<T>
}