plugins {
    kotlin("jvm") version "2.0.10"
}

group = "com.tonyGnk.tools.osmDataParserToSqlite"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    implementation("org.openstreetmap.osmosis:osmosis-core:0.49.2")
    implementation("org.openstreetmap.osmosis:osmosis-xml:0.49.2")

    implementation("org.slf4j:slf4j-simple:2.0.9")
}

tasks.test {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}
