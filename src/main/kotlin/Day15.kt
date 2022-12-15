import kotlin.math.absoluteValue

fun main() {
    Day15.run(getInput())
}

object Day15 {
    fun run(input: String) {
        val sensors = input.lines()
            .map { line ->
                val match = pattern.matchEntire(line) ?: error(line)
                val (sx, sy, bx, by) = match.destructured
                val pos = Point(sx.toInt(), sy.toInt())
                val beacon = Point(bx.toInt(), by.toInt())
                val radius = pos.l1distance(beacon)
                Sensor(pos, radius)
            }

        val minX = sensors.minOf { it.pos.x - it.radius } - 1
        val maxX = sensors.maxOf { it.pos.x + it.radius } + 1

        // minus 1 because there is a beacon exactly on 2000000
        println((minX..maxX).count { x -> sensors.any { Point(x, 2_000_000) in it } } - 1)

        for (y in 0..4_000_000) {
            var x = 0

            while (x <= 4_000_000) {
                val p = Point(x, y)
                // find a sensor that I am in
                val sensor = sensors.find { p in it }
                // If I am not in any sensors, that's the solution
                if (sensor == null) {
                    println(p.x.toLong() * 4_000_000L + p.y.toLong())
                    return
                }
                // jump just outside that sensor's range
                x = sensor.pos.x + sensor.radius - (sensor.pos.y - y).absoluteValue + 1
            }
        }
        error("failed")
    }

    private val pattern = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")

    data class Sensor(val pos: Point, val radius: Int) {
        operator fun contains(p: Point): Boolean {
            return pos.l1distance(p) <= radius
        }
    }
}
