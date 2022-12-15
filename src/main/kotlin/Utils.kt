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