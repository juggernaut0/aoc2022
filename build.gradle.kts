import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

group = "com.github.juggernaut0"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    testImplementation(kotlin("test"))
}

tasks {
    (1..25).forEach { n ->
        register("runDay$n", JavaExec::class) {
            description = "Run Day $n Solution"
            group = "Advent of Code"
            classpath = sourceSets.main.get().runtimeClasspath
            mainClass.set("Day${n}Kt")
            systemProperty("aoc.day", n)
        }
    }

    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "17"
    }
}
