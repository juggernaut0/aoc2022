fun main() {
    val input = getInput().lines()

    println(input.map { findError(it) }.sumOf { valueOf(it) })
    println(input.chunked(3).map { findBadge(it) }.sumOf { valueOf(it) })
}

private fun findError(line: String): Char {
    val a = line.subSequence(0, line.length / 2).toSet()
    val b = line.subSequence(line.length / 2, line.length).toSet()
    return a.intersect(b).single()
}

private fun findBadge(lines: List<String>): Char {
    return lines.map { it.toSet() }.reduce { a, b -> a.intersect(b) }.single()
}

private fun valueOf(c: Char): Int {
    return when (c) {
        in 'a'..'z' -> (c - 'a') + 1
        in 'A'..'Z' -> (c - 'A') + 27
        else -> error(c)
    }
}
