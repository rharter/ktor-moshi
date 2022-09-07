import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.JsonClass
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.util.Date

val ratings = mutableListOf(
    Rating("blue", 4.3),
    Rating("red", 4.1),
    Rating("blue", 4.8),
    Rating("green", 3.2),
    Rating("green", 2.0),
    Rating("blue", 5.0),
    Rating("green", 3.8),
    Rating("red", 1.5),
    Rating("blue", 4.4)
)

fun ratingResponse(color: String?): RatingResponse {
    val filtered = ratings.filter { color == null || it.color == color }
    val average = filtered.map { it.rating }.average()
    return RatingResponse(average, filtered)
}

fun main() {
    val server = embeddedServer(Netty, 8080) {
        install(CallLogging)

        install(ContentNegotiation) {
            moshi {
                // Configure the Moshi.Builder here.
                add(Date::class.java, Rfc3339DateJsonAdapter())
            }
        }

        routing {
            // Test with the browser at http://localhost:8080/ratings?color=blue
            get("/ratings") {
                val color = call.parameters["color"]
                call.respond(ratingResponse(color))
            }

            // Test with `curl -X POST -H'Content-Type: application/json' -d '{"color": "red", "rating": 3.7}' http://localhost:8080/ratings`
            post("/ratings") {
                val rating = call.receive<Rating>()
                ratings.add(rating)
                call.respond(ratingResponse(rating.color))
            }
        }
    }
    server.start(wait = true)
}

@JsonClass(generateAdapter = true)
data class RatingResponse(val average: Double, val ratings: List<Rating>)

@JsonClass(generateAdapter = true)
data class Rating(val color: String, val rating: Double)
