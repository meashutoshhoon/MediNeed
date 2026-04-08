plugins {
    // Android basic
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    // Kotlin Serialization
    alias(libs.plugins.kotlin.serialization) apply false

    // KSP
    alias(libs.plugins.ksp) apply false

    // Compose compiler
    alias(libs.plugins.compose.compiler) apply false

    // Room database for local storage
    alias(libs.plugins.room) apply false
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}