fun main() {
    val instructions = getInput().lines().map {
        val parts = it.split(' ')
        if (parts[0] == "noop") {
            Noop
        } else {
            Addx(parts[1].toInt())
        }
    }

    val res = mutableListOf<Int>()
    var x = 1

    for (instr in instructions) {
        repeat(instr.time) { res.add(x) }
        x = instr.execute(x)
    }

    fun ss(i: Int) = res[i-1] * i

    println(ss(20) + ss(60) + ss(100) + ss(140) + ss(180) + ss(220))

    for ((cycle, x) in res.withIndex()) {
        val pos = cycle % 40
        if (pos in setOf(x - 1, x, x + 1)) {
            print('#')
        } else {
            print(' ')
        }
        if (pos == 39) {
            println()
        }
    }
}

private sealed interface Instruction2 {
    val time: Int
    fun execute(x: Int): Int
}
private object Noop : Instruction2 {
    override val time: Int = 1
    override fun execute(x: Int) = x
}
private data class Addx(val n: Int) : Instruction2 {
    override val time: Int = 2
    override fun execute(x: Int) = x + n
}
