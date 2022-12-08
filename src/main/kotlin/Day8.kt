fun main() {
    val input = getInput()

    val grid = input.toGrid { it.toString().toInt() }

    println(grid.points().count { isVisible(grid, it) })
    println(grid.points().maxOfOrNull { scenicScore(grid, it) })
}

fun isVisible(grid: Grid<Int>, p: Point): Boolean {
    val me = grid[p]

    if(p.x == 0 || (0 until p.x).all { x -> grid[Point(x, p.y)] < me }) return true
    if(p.x == grid.width() - 1 || (p.x + 1 until grid.width()).all { x -> grid[Point(x, p.y)] < me }) return true
    if(p.y == 0 || (0 until p.y).all { y -> grid[Point(p.x, y)] < me }) return true
    if(p.y == grid.height() - 1 || (p.y + 1 until grid.height()).all { y -> grid[Point(p.x, y)] < me }) return true
    return false
}

fun scenicScore(grid: Grid<Int>, p: Point): Int {
    val me = grid[p]

    val n = (p.y - 1 downTo 0).countWhileInclusive { y -> grid[p.copy(y = y)] < me }
    val s = (p.y + 1 until grid.height()).countWhileInclusive { y -> grid[p.copy(y = y)] < me }
    val e = (p.x + 1 until grid.width()).countWhileInclusive { x -> grid[p.copy(x = x)] < me }
    val w = (p.x - 1 downTo 0).countWhileInclusive { x -> grid[p.copy(x = x)] < me }

    return n * e * s * w
}

fun <T> Iterable<T>.countWhileInclusive(pred: (T) -> Boolean): Int {
    var res = 0
    for (t in this) {
        res += 1
        if (!pred(t)) break
    }
    return res
}
