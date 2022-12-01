import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "com.github.juggernaut0"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks {
    (1..25).forEach { n ->
        register("runDay$n", JavaExec::class) {
            classpath = sourceSets.main.get().runtimeClasspath
            mainClass.set("Day${n}Kt")
        }
    }

    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "17"
    }
}
