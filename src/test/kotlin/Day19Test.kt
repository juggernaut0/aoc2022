import kotlin.test.Test
import kotlin.test.assertEquals

class Day19Test {
    @Test
    fun ex() {
        val blueprint = Day19.parseBlueprint("Blueprint 1: " +
                "Each ore robot costs 4 ore. " +
                "Each clay robot costs 2 ore. " +
                "Each obsidian robot costs 3 ore and 14 clay. " +
                "Each geode robot costs 2 ore and 7 obsidian.")

        assertEquals(9, blueprint.findOptimal(24))
    }

    @Test
    fun ex2() {
        val blueprint = Day19.parseBlueprint("Blueprint 2: " +
                "Each ore robot costs 2 ore. " +
                "Each clay robot costs 3 ore. " +
                "Each obsidian robot costs 3 ore and 8 clay. " +
                "Each geode robot costs 3 ore and 12 obsidian.")

        assertEquals(12, blueprint.findOptimal(24))
    }
}