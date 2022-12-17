import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

fun main() {
    val json = Json { ignoreUnknownKeys = true }
    val leaderboard = File("/home/tom/666688.json").inputStream().use { json.decodeFromStream(Leaderboard.serializer(), it) }
    val size = leaderboard.members.size

    val totalScores = mutableMapOf<String, Int>()
    for (day in 1..17) {
        val scores = mutableMapOf<String, Int>()
        for (part in listOf("1", "2")) {
            val ranking = leaderboard.members.values
                .mapNotNull { it.timestamp(day, part)?.let { ts -> it.displayName() to ts } }
                .sortedBy { it.second }
                .map { it.first }

            for ((name, points) in ranking.zip(size downTo 1)) {
                scores.merge(name, points, Int::plus)
            }
        }
        println("Day $day")
        println(scores)

        for (score in scores) {
            totalScores.merge(score.key, score.value, Int::plus)
        }

        println("total")
        println(totalScores.entries.sortedByDescending { it.value })
    }

}

@Serializable
class Leaderboard(val members: Map<String, Member>)
@Serializable
class Member(val id: Int, val name: String?, @SerialName("completion_day_level") val completionDayLevel: Map<String, Map<String, CompletionDay>>) {
    fun displayName() = name ?: "Anonymous user #$id"

    fun timestamp(day: Int, part: String): Int? = completionDayLevel[day.toString()]?.get(part)?.getStarTs
}
@Serializable
class CompletionDay(@SerialName("star_index") val starIndex: Int, @SerialName("get_star_ts") val getStarTs: Int)