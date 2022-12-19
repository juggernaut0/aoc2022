fun main() {
    Day19.run(getInput())
}

object Day19 {
    fun run(input: String) {
        val blueprints = input.lines().map { parseBlueprint(it) }

        val optimals = blueprints.associateWith { bp -> bp.findOptimal(24).also { println("${bp.id}: $it") } }
        println(optimals.entries.sumOf { it.key.id * it.value })

        val top3 = blueprints.take(3).associateWith { bp -> bp.findOptimal(32).also { println("${bp.id}: $it") } }
        println(top3.values.reduce(Int::times))
    }

    val pattern = Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
    fun parseBlueprint(line: String): Blueprint {
        val match = pattern.matchEntire(line) ?: error(line)
        val (id, oreOre, clayOre, obsOre, obsClay, geoOre, geoObs) = match.destructured
        return Blueprint(
            id = id.toInt(),
            oreOre = oreOre.toInt(),
            clayOre = clayOre.toInt(),
            obsOre = obsOre.toInt(),
            obsClay = obsClay.toInt(),
            geoOre = geoOre.toInt(),
            geoObs = geoObs.toInt(),
        )
    }

    class Blueprint(val id: Int, val oreOre: Int, val clayOre: Int, val obsOre: Int, val obsClay: Int, val geoOre: Int, val geoObs: Int) {
        val maxOreCost = maxOf(clayOre, obsOre, geoOre)

        fun findOptimal(time: Int): Int {
            val queue = mutableListOf(State())
            var best = -1
            while (queue.isNotEmpty()) {
                val state = queue.removeLast()

                if (state.estimated(time) <= best) continue

                if (state.time == time) {
                    if (state.geo > best) {
                        best = state.geo
                    }
                } else {
                    queue.addAll(search(state))
                }
            }
            return best
        }

        // returns new possible state succeeding this one
        fun search(state: State): List<State> {
            val res = mutableListOf<State>()
            val (ore, clay, obs) = state

            res.add(state.income())

            if (ore >= oreOre && state.oreRobots < maxOreCost) {
                res.add(state.income(newOre = -oreOre, newOreRobots = 1))
            }
            if (ore >= clayOre && state.clayRobots < obsClay) {
                res.add(state.income(newOre = -clayOre, newClayRobots = 1))
            }
            if (ore >= obsOre && clay >= obsClay && state.obsRobots < geoObs) {
                res.add(state.income(newOre = -obsOre, newClay = -obsClay, newObsRobots = 1))
            }
            if (ore >= geoOre && obs >= geoObs) {
                res.add(state.income(newOre = -geoOre, newObs = -geoObs, newGeoRobots = 1))
            }

            return res
        }

        fun State.estimated(maxTime: Int): Int {
            var estClay = clay
            var estClayRobots = clayRobots
            var estObs = obs
            var estObsRobots = obsRobots
            var estGeo = geo
            var estGeoRobots = geoRobots

            for (t in time until maxTime) {
                estClay += estClayRobots
                estClayRobots++

                estObs += estObsRobots
                if (estClay >= obsClay) {
                    estClay -= obsClay
                    estObsRobots++
                }

                estGeo += estGeoRobots
                if (estObs >= geoObs) {
                    estObs -= geoObs
                    estGeoRobots++
                }
            }

            return estGeo
        }
    }

    data class State(
        val ore: Int,
        val clay: Int,
        val obs: Int,
        val geo: Int,
        val oreRobots: Int,
        val clayRobots: Int,
        val obsRobots: Int,
        val geoRobots: Int,
        val time: Int,
    ) {
        constructor() : this(0, 0, 0, 0, 1, 0, 0, 0, 0)

        fun income(
            newOre: Int = 0,
            newClay: Int = 0,
            newObs: Int = 0,
            newOreRobots: Int = 0,
            newClayRobots: Int = 0,
            newObsRobots: Int = 0,
            newGeoRobots: Int = 0,
        ): State {
            return copy(
                ore = ore + oreRobots + newOre,
                clay = clay + clayRobots + newClay,
                obs = obs + obsRobots + newObs,
                geo = geo + geoRobots,
                oreRobots = oreRobots + newOreRobots,
                clayRobots = clayRobots + newClayRobots,
                obsRobots = obsRobots + newObsRobots,
                geoRobots = geoRobots + newGeoRobots,
                time = time + 1,
            )
        }
    }
}
