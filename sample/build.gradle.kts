plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
    id("org.jmailen.kotlinter")
}
dependencies {
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")

    implementation(project(":library"))
    implementation("io.ktor:ktor-server-netty:2.1.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.1.0")
    implementation("io.ktor:ktor-server-call-logging:2.1.0")
    implementation("ch.qos.logback:logback-classic:1.4.0")
    implementation("com.squareup.moshi:moshi-adapters:1.13.0")
    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
}
kapt {
    useBuildCache = true
    includeCompileClasspath = false
    javacOptions {
        option("--illegal-access", "permit")
    }
}
kotlin {
    kotlinDaemonJvmArgs = listOf("-Dfile.encoding=UTF-8", "--illegal-access=permit")
}
