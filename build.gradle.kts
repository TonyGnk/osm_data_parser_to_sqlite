plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("app.cash.sqldelight:sqlite-driver:2.0.0-rc01")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    implementation("org.openstreetmap.osmosis:osmosis-core:0.48.3")
    implementation("org.openstreetmap.osmosis:osmosis-xml:0.48.3")
}

tasks.test {
    useJUnitPlatform()
}

//kotlin {
//    jvmToolchain(17)
//}
