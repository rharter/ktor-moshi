# Ktor: Moshi

The Moshi feature allows you to handle JSON content in your application easily using the [Moshi](https://github.com/square/moshi/) library.

This feature provides a [ContentNegotiation](http://ktor.io/servers/features/content-negotiation.html) converter.

## Usage

Install the feature by registering a JSON content converter using Moshi:

```kotlin
install(ContentNegotiation) {
  moshi {
    // Configure the Moshi.Builder here.
    add(Date::class.java, Rfc3339DateJsonAdapter())
  }
}
```

Inside the `moshi` block you have access to the [Moshi.Builder](http://square.github.io/moshi/1.x/moshi/com/squareup/moshi/Moshi.Builder.html), which you can configure as needed for your application. 

If you already have an instance of Moshi you can simply provide that and it will be used, instead of creating a new one.

```kotlin
install(ContentNegotiation) {
  moshi(myInjectedMoshi)
}
```

Once the Moshi converter is installed you use it like you would any other ContentNegotiation converter, by using `call.respond(myObject)` and `call.receive<MyType>()`. 

```kotlin
routing {
  get("/") {
    // Simply pass an object to `call.respond` and it will be
    // converted to JSON if the client accepts `application/json`
    val myResponseObject = ...
    call.respond(myResponseObject)
  }
  post("/") {
    // Use `call.receive` to get the JSON request as a
    // deserialized object.
    val request = call.receive<MyRequestObject>()
    ...
  }
}
```

## Download

Add a gradle dependency to your project:

```groovy
implementation 'com.hypercubetools:ktor-moshi-server:1.0.1'
```

## Fork

[Ryan Harter's `ktor-moshi`][old_repo] is the original source for this project. The project has been expanded since it's
initial state.

[old_repo]: https://github.com/rharter/ktor-moshi
