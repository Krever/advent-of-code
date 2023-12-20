plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
    implementation("io.arrow-kt:arrow-mtl-data:0.11.0")
    implementation("com.github.andrewoma.dexx:kollection:0.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}