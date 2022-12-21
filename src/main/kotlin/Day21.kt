fun main() {
    Day21.run(getInput())
}

object Day21 {
    private lateinit var monkeys: Map<String, Monkey>

    fun run(input: String) {
        monkeys = input.lines().map { parseMonkey(it) }.associateBy { it.name }

        part1()
        part2()
    }

    private val pattern = Regex("(.+): (?:(\\d+)|(.+) ([+\\-*/]) (.+))")
    private fun parseMonkey(line: String): Monkey {
        val match = pattern.matchEntire(line) ?: error(line)
        val name = match.groupValues[1]
        val num = match.groupValues[2].takeIf { it.isNotEmpty() }?.toLong()

        return if (num != null) {
            NumberMonkey(name, num)
        } else {
            val left = match.groupValues[3]
            val right = match.groupValues[5]
            val op = when (val c = match.groupValues[4]) {
                "+" -> Op.PLUS
                "-" -> Op.MINUS
                "*" -> Op.TIMES
                "/" -> Op.DIV
                else -> error("unreachable: $c")
            }
            OpMonkey(name, left, right, op)
        }
    }

    private fun part1() {
        println(eval(monkeys.getValue("root")))
    }

    private fun part2() {
        val root = monkeys.getValue("root") as OpMonkey

        val leftKeys = mutableSetOf<String>()
        val rightKeys = mutableSetOf<String>()
        explore(root.left, leftKeys)
        explore(root.right, rightKeys)

        check(("humn" in leftKeys) xor ("humn" in rightKeys))

        val value: Long
        val expr: Monkey
        if ("humn" in leftKeys) {
            value = eval(monkeys.getValue(root.right))
            expr = monkeys.getValue(root.left)
        } else {
            value = eval(monkeys.getValue(root.left))
            expr = monkeys.getValue(root.right)
        }

        var (n, monkey) = eq(value, expr)
        while (monkey != null) {
            val (newN, newMonkey) = eq(n, monkey)
            n = newN
            monkey = newMonkey
        }

        println(n)
    }

    private fun explore(name: String, set: MutableSet<String>) {
        set.add(name)
        val monkey = monkeys.getValue(name)
        if (monkey is OpMonkey) {
            explore(monkey.left, set)
            explore(monkey.right, set)
        }
    }

    private fun eq(value: Long, tree: Monkey): Pair<Long, Monkey?> {
        if (tree !is OpMonkey) {
            check(tree.name == "humn")
            return value to null
        }

        val leftKeys = mutableSetOf<String>()
        val rightKeys = mutableSetOf<String>()
        explore(tree.left, leftKeys)
        explore(tree.right, rightKeys)

        return if ("humn" in leftKeys) {
            val e = eval(monkeys.getValue(tree.right))
            when (tree.op) {
                Op.PLUS -> {
                    // v = x + e
                    value - e
                }
                Op.MINUS -> {
                    // v = x - e
                    value + e
                }
                Op.TIMES -> {
                    // v = x * e
                    value / e
                }
                Op.DIV -> {
                    // v = x / e
                    value * e
                }
            } to monkeys.getValue(tree.left)
        } else {
            val e = eval(monkeys.getValue(tree.left))
            when (tree.op) {
                Op.PLUS -> {
                    // v = e + x
                    value - e
                }
                Op.MINUS -> {
                    // v = e - x -> x = e - v
                    e - value
                }
                Op.TIMES -> {
                    // v = e * x
                    value / e
                }
                Op.DIV -> {
                    // v = e / x -> x = e / v
                    e / value
                }
            } to monkeys.getValue(tree.right)
        }
    }

    private fun eval(monkey: Monkey): Long {
        val queue = mutableListOf(monkey)
        val values = mutableMapOf<String, Long>()

        while (queue.isNotEmpty()) {
            val top = queue.last()
            when (top) {
                is NumberMonkey -> {
                    values[top.name] = top.number
                    queue.removeLast()
                }
                is OpMonkey -> {
                    if (top.left !in values) {
                        queue.add(monkeys[top.left]!!)
                    } else if (top.right !in values) {
                        queue.add(monkeys[top.right]!!)
                    } else {
                        val left = values.getValue(top.left)
                        val right = values.getValue(top.right)
                        values[top.name] = when (top.op) {
                            Op.PLUS -> left + right
                            Op.MINUS -> left - right
                            Op.TIMES -> left * right
                            Op.DIV -> left / right
                        }
                        queue.removeLast()
                    }
                }
            }
        }

        return values.getValue(monkey.name)
    }

    sealed interface Monkey {
        val name: String
    }
    class NumberMonkey(override val name: String, val number: Long) : Monkey
    class OpMonkey(override val name: String, val left: String, val right: String, val op: Op) : Monkey
    enum class Op { PLUS, MINUS, TIMES, DIV }
}
