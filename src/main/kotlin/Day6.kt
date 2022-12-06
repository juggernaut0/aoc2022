fun main() {
    val input = getInput()

    println(input.windowed(4).indexOfFirst { it.toSet().size == 4 } + 4)
    println(input.windowed(14).indexOfFirst { it.toSet().size == 14 } + 14)
}
