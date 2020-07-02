package com.hypercubetools.ktor.moshi

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonClass
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import kotlin.test.fail

class MoshiConverterTest {

  @Test fun reflection() = withTestApplication {
    application.install(ContentNegotiation) {
      moshi { }
    }
    application.routing {
      val foo = Foo(id = 42, name = "Foosius")
      get("/") {
        call.respond(foo)
      }
      post("/") {
        val request = call.receive<Foo>()
        val text = request.toString()
        call.respond(text)
      }
    }

    handleRequest(HttpMethod.Get, "/") {
      addHeader("Accept", "application/json")
    }.response.let { response ->
      assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
      assertThat(response.content).isNotNull()
      assertThat(response.content).isEqualTo("""{"id":42,"name":"Foosius"}""")
      assertThat(response.contentType()).isEqualTo(ContentType.Application.Json.withCharset(Charsets.UTF_8))
    }

    handleRequest(HttpMethod.Post, "/") {
      addHeader("Accept", "application/json")
      addHeader("Content-Type", "application/json")
      setBody("""{"id":43,"name":"Finnius"}""")
    }.response.let { response ->
      assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
      assertThat(response.content).isNotNull()
      assertThat(response.content).isEqualTo("Foo(id=43, name=Finnius)")
      assertThat(response.contentType()).isEqualTo(ContentType.Text.Plain.withCharset(Charsets.UTF_8))
    }
  }

  @Test fun codegen() = withTestApplication {
    application.install(ContentNegotiation) {
      moshi { }
    }
    application.routing {
      val bar = Bar(id = "bar-123", count = 50)
      get("/") {
        call.respond(bar)
      }
      post("/") {
        val request = call.receive<Bar>()
        val text = request.toString()
        call.respond(text)
      }
    }

    handleRequest(HttpMethod.Get, "/") {
      addHeader("Accept", "application/json")
    }.response.let { response ->
      assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
      assertThat(response.content).isNotNull()
      assertThat(response.content).isEqualTo("""{"id":"bar-123","count":50}""")
      assertThat(response.contentType()).isEqualTo(ContentType.Application.Json.withCharset(Charsets.UTF_8))
    }

    handleRequest(HttpMethod.Post, "/") {
      addHeader("Accept", "application/json")
      addHeader("Content-Type", "application/json")
      setBody("""{"id":"bar-543","count":-1}""")
    }.response.let { response ->
      assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
      assertThat(response.content).isNotNull()
      assertThat(response.content).isEqualTo("Bar(id=bar-543, count=-1)")
      assertThat(response.contentType()).isEqualTo(ContentType.Text.Plain.withCharset(Charsets.UTF_8))
    }
  }

}

data class Foo(val id: Int, val name: String)

@JsonClass(generateAdapter = true)
data class Bar(val id: String, val count: Int)