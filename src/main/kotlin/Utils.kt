fun getInput(): String {
    val day = System.getProperty("aoc.day")!!
    val resName = "/day$day.txt"
    return object {}::class.java.getResourceAsStream(resName)
        .let { it ?: error("Unable to find input: $resName") }
        .reader()
        .readText()
}
