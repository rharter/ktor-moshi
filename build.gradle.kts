import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21" apply false
    id("org.jmailen.kotlinter") version "3.10.0" apply false
    id("com.github.ben-manes.versions") version "0.42.0"
}
subprojects {
    group = "com.github.cs125-illinois"
    version = "2022.4.0"
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_16.toString()
        }
    }
    tasks.withType<Test> {
        enableAssertions = true
        useJUnitPlatform()
        jvmArgs("-ea", "-Xmx1G", "-Xss256k", "--illegal-access=permit")
    }
}
allprojects {
    repositories {
        mavenCentral()
    }
}
tasks.dependencyUpdates {
    fun String.isNonStable() = !(
        listOf("RELEASE", "FINAL", "GA", "JRE").any { toUpperCase().contains(it) }
            || "^[0-9,.v-]+(-r)?$".toRegex().matches(this)
        )
    rejectVersionIf { candidate.version.isNonStable() }
    gradleReleaseChannel = "current"
}
