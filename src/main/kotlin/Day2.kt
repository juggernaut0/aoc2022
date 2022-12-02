fun main() {
    val input = getInput().lines().filterNot { it.isBlank() }

    println(input.sumOf { line ->
        val (oppChoice, myChoice) = line.split(' ')
        score(oppChoice, myChoice)
    })

    println(input.sumOf { line ->
        val (oppChoice, outcome) = line.split(' ')
        val myChoice = getMyChoice(oppChoice, outcome)
        score(oppChoice, myChoice)
    })
}

fun score(oppChoice: String, myChoice: String): Long {
    val oppNum = when(oppChoice) {
        "A" -> 1
        "B" -> 2
        "C" -> 3
        else -> error(oppChoice)
    }

    val basePoints = when(myChoice) {
        "X" -> 1
        "Y" -> 2
        "Z" -> 3
        else -> error(myChoice)
    }

    val winPoints = when(val winDiff = (oppNum - basePoints + 3) % 3) {
        0 -> 3
        1 -> 0
        2 -> 6
        else -> error(winDiff)
    }

    return (basePoints + winPoints).toLong()
}

fun getMyChoice(oppChoice: String, outcome: String): String {
    val oppNum = when(oppChoice) {
        "A" -> 0
        "B" -> 1
        "C" -> 2
        else -> error(oppChoice)
    }

    val outNum = when (outcome) {
        "X" -> oppNum - 1
        "Y" -> oppNum
        "Z" -> oppNum + 1
        else -> error("outcome")
    }

    return when((outNum + 3) % 3) {
        0 -> "X"
        1 -> "Y"
        2 -> "Z"
        else -> error(outNum)
    }
}
