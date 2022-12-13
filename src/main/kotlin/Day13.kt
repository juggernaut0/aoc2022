import java.lang.StringBuilder

fun main() {
    Day13.run(getInput())
}

object Day13 {
    fun run(input: String) {
        run1(input)
        run2(input)
    }

    private fun run1(input: String) {
        val pairs = input.split("\n\n").map { pair -> pair.lines().let { (a, b) -> parse(a) to parse(b) } }

        println(pairs.withIndex().mapNotNull { (i, p) -> (i + 1).takeIf { p.first < p.second } }.sum())
    }

    private fun run2(input: String) {
        val packets = input.lines().filter { it.isNotBlank() }.mapTo(mutableListOf()) { parse(it) }
        val two = parse("[[2]]").also { packets.add(it) }
        val six = parse("[[6]]").also { packets.add(it) }
        packets.sort()
        val x = packets.indexOfFirst { it == two } + 1
        val y = packets.indexOfFirst { it == six } + 1
        println(x * y)
    }

    sealed interface Value : Comparable<Value> {
        override operator fun compareTo(other: Value): Int = compare(this, other)
    }
    data class Number(val n: Int) : Value {
        override fun toString(): String = n.toString()
    }
    data class ListValue(val l: List<Value>) : Value {
        override fun toString(): String = l.toString()
    }

    class Parser(private val str: String) {
        private var i = 0

        private fun current(): Char = str[i]
        private fun advance() {
            i++
        }

        private fun require(c: Char) {
            check(current() == c)
            advance()
        }

        private fun value(): Value {
            return if (current() == '[') {
                list()
            } else if (current().isDigit()) {
                number()
            } else {
                error(current())
            }
        }

        fun list(): ListValue {
            require('[')
            val l = mutableListOf<Value>()
            while (current() != ']') {
                l.add(value())
                if (current() == ',') {
                    advance()
                }
            }
            require(']')
            return ListValue(l)
        }

        private fun number(): Number {
            val s = StringBuilder()
            while (current().isDigit()) {
                s.append(current())
                advance()
            }
            return Number(s.toString().toInt())
        }
    }

    fun parse(str: String): Value {
        return Parser(str).list()
    }

    fun compare(a: Value, b: Value): Int {
        return when {
            a is Number && b is Number -> a.n.compareTo(b.n)
            a is ListValue && b is ListValue -> {
                for (i in 0 until kotlin.math.max(a.l.size, b.l.size)) {
                    val ai = a.l.getOrNull(i) ?: return -1
                    val bi = b.l.getOrNull(i) ?: return 1

                    val comp = compare(ai, bi)
                    if (comp != 0) return comp
                }
                0
            }
            a is Number -> compare(ListValue(listOf(Number(a.n))), b)
            b is Number -> compare(a, ListValue(listOf(Number(b.n))))
            else -> error("unreachable")
        }
    }
}
