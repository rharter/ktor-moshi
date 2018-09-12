# Ktor: Moshi

The Moshi feature allows you to handle JSON content in your application easily using the [Moshi](https://github.com/square/moshi/) library.

This feature provides a [ContentNegotiation](http://ktor.io/servers/features/content-negotiation.html) converter.

## Usage

Install the feature by registering a JSON content converter using Moshi:

```kotlin
install(ContentNegotation) {
  moshi {
    // Configure the Moshi.Builder here.
    add(Date::class.java, Rfc3339DateJsonAdapter())
  }
}
```

Inside the `moshi` block you have access to the [Moshi.Builder](http://square.github.io/moshi/1.x/moshi/com/squareup/moshi/Moshi.Builder.html), which you can configure as needed for your application. 

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
compile 'com.ryanharter.ktor:ktor-moshi:1.0.0'
```

Snapshots of the latest development version are available in [Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/).

## License

```
Copyright 2018 Ryan Harter.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
