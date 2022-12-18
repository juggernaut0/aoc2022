fun main() {
    Day18.run(getInput())
}

object Day18 {
    fun run(input: String) {
        val points = input.lines().map { line ->
            val parts = line.split(',').map { it.toInt() }
            Point3(parts[0], parts[1], parts[2])
        }.toSet()

        println(points.sumOf { p -> p.neighbors().count { it !in points } })

        val maxX = points.maxOf { it.x }
        val minX = points.minOf { it.x }
        val maxY = points.maxOf { it.y }
        val minY = points.minOf { it.y }
        val maxZ = points.maxOf { it.z }
        val minZ = points.minOf { it.z }

        val outside = mutableSetOf<Point3>()
        val inside = mutableSetOf<Point3>()

        // returns true if point is exterior
        fun expand(start: Point3): Boolean {
            if (start in inside) return false

            val working = mutableSetOf<Point3>()
            val queue = mutableListOf(start)
            while (queue.isNotEmpty()) {
                val p = queue.removeLast()
                if (p in points) continue
                if (p in working) continue
                if (p.x !in minX..maxX || p.y !in minY..maxY || p.z !in minZ..maxZ || p in outside) {
                    outside.addAll(working)
                    return true
                }
                working.add(p)
                queue.addAll(p.neighbors())
            }
            inside.addAll(working)
            return false
        }

        println(points.flatMap { it.neighbors() }.count { it !in points && expand(it) })
    }
}