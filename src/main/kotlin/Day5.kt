fun main() {
    val input = getInput()

    val parts = input.split("\n\n")
    // drop off last line of drawing
    val rows = parts[0].lines().let { it.subList(0, it.size - 1) }
    val instructions = parts[1].lines().map { Instruction(it) }

    val stacks1 = makeStacks(rows)
    for (instr in instructions) {
        instr.execute(stacks1)
    }
    println(String(stacks1.map { it.last() }.toCharArray()))

    val stacks2 = makeStacks(rows)
    for (instr in instructions) {
        instr.executeProperly(stacks2)
    }
    println(String(stacks2.map { it.last() }.toCharArray()))
}

private fun makeStacks(rows: List<String>): List<MutableList<Char>> {
    return List(9) { getStack(rows, it) }
}

private fun getStack(rows: List<String>, col: Int): MutableList<Char> {
    val drop = 4 * col
    return rows
        .mapNotNull { row -> row.drop(drop + 1).getOrNull(0)?.takeUnless { it == ' ' } }
        .reversed()
        .toMutableList()
}

private class Instruction(instr: String) {
    private companion object {
        private val instrPattern = Regex("move (\\d+) from (\\d+) to (\\d+)")
    }

    val num: Int
    val from: Int
    val to: Int

    init {
        val match = instrPattern.matchEntire(instr) ?: error(instr)
        num = match.groupValues[1].toInt()
        from = match.groupValues[2].toInt()
        to = match.groupValues[3].toInt()
    }

    fun execute(stacks: List<MutableList<Char>>) {
        repeat(num) {
            stacks[to-1].add(stacks[from-1].removeLast())
        }
    }

    fun executeProperly(stacks: List<MutableList<Char>>) {
        val sub = stacks[from-1].let { it.subList(it.size - num, it.size) }
        stacks[to-1].addAll(sub)
        sub.clear() // because "sub" is a SubList, this clears the section of the original list
    }
}
