fun main() {
    Day20.run(getInput())
}

object Day20 {
    fun run(input: String) {
        val file = input.lines().map { it.toLong() }

        mixItUp(file, times = 1)
        mixItUp(file.map { it * 811589153 }, times = 10)
    }

    fun mixItUp(file: List<Long>, times: Int) {
        val result = RingList(file).apply { mix(times) }.toList().map { it.value }
        val base = result.indexOf(0)
        val a = result[(base + 1000) % result.size]
        val b = result[(base + 2000) % result.size]
        val c = result[(base + 3000) % result.size]
        println(a + b + c)
    }

    class RingList(list: List<Long>) {
        private var result = list.withIndex().toList()
        private val size = list.size
        private val zeroIndex = list.indexOf(0)

        init {
            require(zeroIndex >= 0)
        }

        fun mix(times: Int) {
            repeat(times) {
                val lists = result.map { mutableListOf(it) }

                for (x in 0 until size) {
                    if (x == zeroIndex) continue
                    val currentListIndex = lists.indexOfFirst { it.isNotEmpty() && it[0].index == x }.also { check(it >= 0) }
                    val item = lists[currentListIndex].removeFirst()
                    var rem = Math.floorMod(item.value, size - 1)
                    var ii = currentListIndex
                    while (rem > lists[ii].size) {
                        rem -= lists[ii].size
                        ii = (ii + 1) % size
                    }
                    lists[ii].add(index = rem, item)
                }

                result = lists.flatten()
            }
        }

        fun toList(): List<IndexedValue<Long>> = result
    }
}
