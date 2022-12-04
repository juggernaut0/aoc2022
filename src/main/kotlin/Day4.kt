fun main() {
    val input = getInput().lines().map { parseRanges(it) }

    println(input.count { (a, b) -> a.fullyContains(b) || b.fullyContains(a) })
    println(input.count { (a, b) -> a.overlaps(b) })
}

private val linePattern = Regex("^(\\d+)-(\\d+),(\\d+)-(\\d+)$")
private fun parseRanges(line: String): Pair<IntRange, IntRange> {
    val match = linePattern.matchEntire(line) ?: error(line)
    val (a, b, c, d) = match.groupValues.drop(1).map { it.toInt() }
    return a..b to c..d
}

private fun IntRange.fullyContains(other: IntRange): Boolean {
    return this.first <= other.first && this.last >= other.last
}

private fun IntRange.overlaps(other: IntRange): Boolean {
    val d1 = this.last - other.first
    val d2 = this.first - other.last
    return d1 * d2 <= 0
}
