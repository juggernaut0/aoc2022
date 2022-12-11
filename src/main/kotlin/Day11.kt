fun main() {
    val input = getInput()
    with(Day11) {
        run1(input)
        run2(input)
    }
}

object Day11 {
    data class Monkey(
        val items: MutableList<Int>,
        val operation: Operation,
        val test: Int,
        val trueDest: Int,
        val falseDest: Int,
    ) {
        var inspectCount = 0L

        fun monkeyDo(monkeys: List<Monkey>, worryReduction: Boolean) {
            val magic = monkeys.map { it.test }.reduce { a, b -> a * b }
            for (item in items) {
                val newItem = operation.operate(item).let { if (worryReduction) { (it / 3).toInt() } else { (it % magic).toInt() } }
                inspectCount += 1
                val testResult = newItem % test == 0
                if (testResult) {
                    monkeys[trueDest].items.add(newItem)
                } else {
                    monkeys[falseDest].items.add(newItem)
                }
            }
            items.clear()
        }
    }
    data class Operation(
        val op: Op,
        val left: Operand,
        val right: Operand,
    ) {
        fun operate(old: Int): Long {
            val leftVal = if (left is Const) left.n else old
            val rightVal = if (right is Const) right.n else old
            return op.operate(leftVal.toLong(), rightVal.toLong())
        }

        override fun toString() = "Operation($left $op $right)"
    }
    enum class Op(val operate: (Long, Long) -> Long) { PLUS(Long::plus), MUL(Long::times) }
    sealed interface Operand
    object Old : Operand {
        override fun toString() = "Old"
    }
    data class Const(val n: Int) : Operand

    private val operationPattern = Regex("old ([+*]) (old|\\d+)")
    private fun parseMonkey(s: String): Monkey {
        val lines = s.lines().drop(1).map { it.trim() }
        val items = lines[0].drop(16).split(", ").mapTo(mutableListOf()) { it.toInt() }
        val operationMatch = operationPattern.matchEntire(lines[1].drop(17))!!
        val op = if (operationMatch.groupValues[1] == "+") Op.PLUS else Op.MUL
        val right = operationMatch.groupValues[2].let { group2 -> if (group2 == "old") Old else Const(group2.toInt()) }
        val operation = Operation(op, Old, right)
        val test = lines[2].drop(19).toInt()
        val trueDest = lines[3].drop(25).toInt()
        val falseDest = lines[4].drop(26).toInt()

        return Monkey(items, operation, test, trueDest, falseDest)
    }

    fun run1(input: String) {
        val monkeys = input.split("\n\n").map { parseMonkey(it) }
        repeat(20) {
            monkeys.forEach { it.monkeyDo(monkeys, true) }
        }
        val sorted = monkeys.map { it.inspectCount }.sortedDescending()
        println(sorted[0] * sorted[1])
    }

    fun run2(input: String, times: Int = 10_000) {
        val monkeys = input.split("\n\n").map { parseMonkey(it) }
        repeat(times) {
            monkeys.forEach { it.monkeyDo(monkeys, false) }
        }
        val sorted = monkeys.map { it.inspectCount }.sortedDescending()
        println(sorted[0] * sorted[1])
    }
}
