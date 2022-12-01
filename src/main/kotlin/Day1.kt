fun main() {
    val input = getInput()

    val sums = input.trim()
        .split("\n\n")
        .map { elf -> elf.lines().sumOf { it.toLong() } }
    
    println(sums.max())
    println(sums.sortedDescending().subList(0, 3).sum())
}
