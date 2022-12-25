import kotlin.math.pow

fun main() {
    Day25.run(getInput())
}

object Day25 {
    fun run(input: String) {
        println(input.lines().fold(Snafu(0)) { a, b -> a + b.toSnafu() })
    }

    class Snafu(private val value: Long) {
        override fun toString(): String {
            tailrec fun stringify(n: Long, s: String): String {
                if (n == 0L) {
                    return s
                }
                val m = ((n + 2) % 5) - 2
                val c = when (m) {
                    -2L -> '='
                    -1L -> '-'
                    0L -> '0'
                    1L -> '1'
                    2L -> '2'
                    else -> error("unreachable")
                }
                return stringify((n - m) / 5, c + s)
            }

            val result = stringify(value, "")

            return result.takeIf { it.isNotEmpty() } ?: "0"
        }

        operator fun plus(other: Snafu): Snafu {
            return Snafu(value + other.value)
        }
    }

    fun String.toSnafu(): Snafu {
        val value = reversed().foldIndexed(0L) { i, acc, c ->
            val charValue = when(c) {
                '=' -> -2
                '-' -> -1
                '0' -> 0
                '1' -> 1
                '2' -> 2
                else -> error(c)
            }
            acc + (5.0).pow(i).toLong() * charValue
        }
        return Snafu(value)
    }
}
