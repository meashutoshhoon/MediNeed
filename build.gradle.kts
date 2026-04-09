plugins {
    // Android
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    // Kotlin
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    // Build tools
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.room) apply false
}

// Clean root build directory
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}