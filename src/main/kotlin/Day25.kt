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
            val result = StringBuilder()

            tailrec fun go(n: Long) {
                if (n == 0L) {
                    return
                }
                val m = n % 5
                val c = when (m) {
                    0L -> '0'
                    1L -> '1'
                    2L -> '2'
                    3L -> '='
                    4L -> '-'
                    else -> error("unreachable")
                }
                result.append(c)
                val o = ((m + 2) % 5) - 2
                go((n - o) / 5)
            }

            go(value)

            return result.toString().reversed().takeIf { it.isNotEmpty() } ?: "0"
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
