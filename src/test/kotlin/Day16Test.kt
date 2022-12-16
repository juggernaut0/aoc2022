import kotlin.test.Test

class Day16Test {
    val input = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent()

    @Test
    fun ex() {
        Day16.run(input)
    }

    /*@Test
    fun st1() {
        // State2(mePath=[CC], elPath=[CC], opened=[AA, FF, GG, II, DD, CC], time=3, pressure=20)
        val valves = Day16.parseValves(input)
        val state = Day16.State2(mePath = listOf("CC"), elPath = listOf("CC"), opened = setOf("AA", "FF", "GG", "II", "DD", "CC"), time = 3, pressure = 20)
        state.next(valves).forEach { println(it) }
    }*/
}