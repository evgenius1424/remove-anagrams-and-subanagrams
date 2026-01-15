plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

application {
    mainClass.set("RunKt")
}

sourceSets {
    named("main") {
        kotlin.srcDirs("tests/kotlin", "solutions/kotlin")
    }
}
