import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayDeque

fun main() {
    Day16.run(getInput())
}

object Day16 {
    fun run(input: String) {
        part1(input)
        part2(input)
    }

    fun part1(input: String) {
        val valves = compressValves(input)

        //val states = PriorityQueue<SearchState>(Comparator.comparing{ -it.pressure })
        val states = ArrayDeque<SearchState>()
        states.add(SearchState(at = "AA", opened = valves.filter { it.value.flowRate == 0 }.map { it.key }.toSet(), time = 0, pressure = 0))
        val seenStates = mutableMapOf<SearchState.Key, Int>()
        var max = 0
        while (states.isNotEmpty()) {
            val state = states.removeLast()
            val stateKey = state.key()
            if (state.time == 30) {
                if (state.pressure > max) {
                    max = state.pressure
                    //println(max)
                }
            } else if (state.pressure > (seenStates[stateKey] ?: -1)) {
                seenStates[stateKey] = state.pressure
                states.addAll(state.next(valves).reversed())
            }
        }
        println(max)
    }

    fun part2(input: String) {
        val valves = parseValves(input)
        val maxFlow = valves.values.sumOf { it.flowRate }
        /*println("calulating distances")
        val distances = calcDistances(valves)
        for (from in distances) {
            println(from)
        }*/

        val states = ArrayDeque<State2>()
        states.add(State2(mePath = listOf("AA"), elPath = listOf("AA"), opened = valves.filter { it.value.flowRate == 0 }.map { it.key }.toSet(), time = 0, pressure = 0))
        val seenStates = mutableMapOf<State2.Key, Int>()
        var max = 0
        while (states.isNotEmpty()) {
            val state = states.removeLast()

            val maxPossible = (26 - state.time) * maxFlow + state.pressure
            if (maxPossible <= max) continue

            /*println(state)
            println("---")
            state.next(valves).forEach { println(it) }
            println()*/
            val stateKey = state.key(valves)
            if (state.time == 26) {
                if (state.pressure > max) {
                    max = state.pressure
                    //println(max)
                }
            } else if (state.pressure > (seenStates[stateKey] ?: -1)) {
                seenStates[stateKey] = state.pressure
                states.addAll(state.next(valves).reversed())
            }
        }
        println(max)
    }

    fun calcDistances(valves: Map<String, Valve>): Map<String, Map<String, List<String>>> {
        val res = mutableMapOf<String, Map<String, List<String>>>()
        for ((from, fromValve) in valves.filter { it.value.flowRate > 0 || it.value.neighbors.size != 2 }) {
            val ds = mutableMapOf(from to emptyList<String>())
            val queue = ArrayDeque<Valve>()
            queue.add(fromValve)
            while (queue.isNotEmpty()) {
                val valve = queue.removeFirst()
                val path = ds.getValue(valve.name)
                for (n in valve.neighbors.keys) {
                    val newPath = path + n
                    if (n !in ds || ds.getValue(n).size > newPath.size) {
                        ds[n] = newPath
                        queue.add(valves.getValue(n))
                    }
                }
            }
            res[from] = ds
        }
        return res
    }

    fun parseValves(input: String): Map<String, Valve> {
        return input.lines().map { parseValve(it) }.associateBy { it.name }
    }

    private fun compressValves(input: String): Map<String, Valve> {
        val valves = input.lines().map { parseValve(it) }.associateByTo(mutableMapOf()) { it.name }
        val names = valves.keys.toSet()
        for (name in names) {
            val valve = valves.getValue(name)
            if (valve.flowRate == 0 && valve.neighbors.size == 2) {
                val (a, b) = valve.neighbors.entries.toList()

                // Update a to skip me
                val av = valves.getValue(a.key)
                val avn = av.neighbors.toMutableMap()
                avn[b.key] = a.value + b.value
                avn.remove(name)!!
                valves[a.key] = av.copy(neighbors = avn)

                // Update b to skip me
                val bv = valves.getValue(b.key)
                val bvn = bv.neighbors.toMutableMap()
                bvn[a.key] = a.value + b.value
                bvn.remove(name)!!
                valves[b.key] = bv.copy(neighbors = bvn)

                // kill myself
                valves.remove(name)!!
            }
        }
        valves.entries.forEach { println(it) }
        return valves
    }

    // neighbors = name -> how long it takes to get there
    data class Valve(val name: String, val flowRate: Int, val neighbors: Map<String, Int>) {
        fun reachable(valves: Map<String, Valve>, at: String): Set<String> {
            val s = mutableSetOf<String>()
            s.add(name)
            s.add(at)
            reach(s, valves)
            s.remove(at)
            return s
        }

        private fun reach(s: MutableSet<String>, valves: Map<String, Valve>) {
            for (n in neighbors.keys) {
                if (s.add(n)) {
                    valves.getValue(n).reach(s, valves)
                }
            }
        }
    }

    val valvePattern = Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)")
    fun parseValve(line: String): Valve {
        val match = valvePattern.matchEntire(line) ?: error(line)
        val (name, flowRate, neighbors) = match.destructured
        return Valve(name, flowRate.toInt(), neighbors.split(", ").associateWith { 1 })
    }

    data class SearchState(val at: String, val opened: Set<String>, val time: Int, val pressure: Int) {
        fun next(valves: Map<String, Valve>): List<SearchState> {
            val res = mutableListOf<SearchState>()
            val atValve = valves.getValue(at)
            val pressDiff = opened.sumOf { valves.getValue(it).flowRate }
            val newPressure = pressure + pressDiff

            if (at !in opened) {
                res.add(this.copy(opened = opened + at, time = time + 1, pressure = newPressure))
            }

            for ((neigh, nd) in atValve.neighbors) {
                val branchFinished = valves.getValue(neigh).reachable(valves, at).all { it in opened }

                val actualD = nd.coerceAtMost(30 - time)

                if (!branchFinished) {
                    res.add(this.copy(at = neigh, time = time + actualD, pressure = pressure + pressDiff * actualD))
                }
            }

            if (res.isEmpty()) {
                val d = 30 - time
                res.add(this.copy(time = 30, pressure = pressure + pressDiff * d))
            }

            return res
        }

        fun key(): Key = Key(at, opened, time)

        data class Key(val at: String, val opened: Set<String>, val time: Int)
    }

    data class State2(val mePath: List<String>, val elPath: List<String>, val opened: Set<String>, val time: Int, val pressure: Int) {
        fun next(valves: Map<String, Valve>/*, paths: Map<String, Map<String, List<String>>>*/): List<State2> {
            fun generateOptions(xPath: List<String>): List<Option> {
                val options = mutableListOf<Option>()
                if (valves.size == opened.size) {
                    options.add(Wait)
                } else if (xPath.size == 1) {
                    val at = xPath.first()
                    if (at !in opened) {
                        options.add(Open)
                    }
                    for ((neigh, nd) in valves.getValue(at).neighbors) {
                        val branchFinished = valves.getValue(neigh).reachable(valves, at).all { it in opened }
                        //val branchFinished = false

                        if (!branchFinished) {
                            var last = at
                            val path = mutableListOf(neigh)
                            while (true) {
                                val term = valves.getValue(path.last())
                                if (term.name !in opened || term.neighbors.size != 2) break
                                val next = term.neighbors.keys.single { it != last }
                                last = term.name
                                path.add(next)
                            }
                            options.add(Goto(path))
                        }
                    }
                    /*for (unopened in valves.keys - opened) {
                        if (unopened == at) continue
                        val path = paths.getValue(at).getValue(unopened)
                        options.add(Goto(path))
                    }*/
                } else {
                    options.add(Goto(xPath.drop(1)))
                }
                return options
            }

            val myOptions = generateOptions(mePath)

            val elOptions = generateOptions(elPath)

            val res = mutableListOf<State2>()
            val pressDiff = opened.sumOf { valves.getValue(it).flowRate }

            for (myOpt in myOptions) {
                for (elOpt in elOptions) {
                    //if (myOpt == Open && elOpt == Open) continue

                    val mePath = when (myOpt) {
                        is Goto -> myOpt.path
                        else -> mePath
                    }

                    val elPath = when (elOpt) {
                        is Goto -> elOpt.path
                        else -> elPath
                    }

                    val opened = opened.toMutableSet()
                    if (myOpt == Open) {
                        opened.add(this.mePath.single())
                    }
                    if (elOpt == Open) {
                        opened.add(this.elPath.single())
                    }

                    res.add(State2(mePath, elPath, opened, time + 1, pressure + pressDiff))
                }
            }

            return res
        }

        fun key(valves: Map<String, Valve>): Key {
            //val pressDiff = opened.sumOf { valves.getValue(it).flowRate }
            val encoded = valves.keys.sorted().foldIndexed(0L) { i, n, v -> if (v in opened) n + (1L shl (i+1)) else n }
            return Key(mePath.first(), elPath.first(), encoded, time)
        }

        data class Key(val meAt: String, val elAt: String, val opened: Long, val time: Int)

        sealed interface Option
        object Open : Option
        object Wait : Option
        data class Goto(val path: List<String>) : Option
    }
}