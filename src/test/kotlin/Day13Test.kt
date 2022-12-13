import kotlin.test.Test
import kotlin.test.assertTrue

class Day13Test {
    @Test
    fun a() {
        val a = Day13.parse("[1,1,3,1,1]")
        val b = Day13.parse("[1,1,5,1,1]")
        assertTrue(a < b)
    }

    @Test
    fun b() {
        val a = Day13.parse("[[1],[2,3,4]]")
        val b = Day13.parse("[[1],4]")
        assertTrue(a < b)
    }

    @Test
    fun c() {
        val a = Day13.parse("[9]")
        val b = Day13.parse("[[8,7,6]]")
        assertTrue(a > b)
    }

    @Test
    fun d() {
        val a = Day13.parse("[[4,4],4,4]")
        val b = Day13.parse("[[4,4],4,4,4]")
        assertTrue(a < b)
    }

    @Test
    fun e() {
        val a = Day13.parse("[7,7,7,7]")
        val b = Day13.parse("[7,7,7]")
        assertTrue(a > b)
    }

    @Test
    fun f() {
        val a = Day13.parse("[]")
        val b = Day13.parse("[3]")
        assertTrue(a < b)
    }

    @Test
    fun g() {
        val a = Day13.parse("[[[]]]")
        val b = Day13.parse("[[]]")
        assertTrue(a > b)
    }
}