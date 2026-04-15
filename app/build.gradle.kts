@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.ktfmt.gradle)
}


// Version / Keystore
val keystoreFile = rootProject.file("keystore.properties")
val baseVersionName = currentVersion.name

android {
    namespace = "com.jb.medineed.app"
    compileSdk = 37

    // Signing (optional)
    if (keystoreFile.exists()) {
        val props = Properties().apply {
            load(FileInputStream(keystoreFile))
        }

        signingConfigs {
            create("githubPublish") {
                keyAlias = props["keyAlias"].toString()
                keyPassword = props["keyPassword"].toString()
                storeFile = file(props["storeFile"]!!)
                storePassword = props["storePassword"].toString()
            }
        }
    }

    // Feature
    buildFeatures { buildConfig = true }
    androidResources { generateLocaleConfig = true }

    // Default config
    defaultConfig {
        applicationId = "com.jb.medineed.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = baseVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    // Build types
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("githubPublish")
            }
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("githubPublish")
            }
        }
    }

    // Flavors
    flavorDimensions += "publishChannel"

    productFlavors {
        create("generic") {
            dimension = "publishChannel"
            isDefault = true
        }

        create("githubPreview") {
            dimension = "publishChannel"
            applicationIdSuffix = ".preview"
        }
    }

    // Lint / Packaging
    lint {
        disable += listOf(
            "MissingTranslation",
            "ExtraTranslation",
            "MissingQuantity"
        )
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

// Build config
base {
    archivesName = "MediNeed-${android.defaultConfig.versionName}"
}

kotlin { jvmToolchain(21) }

ktfmt { kotlinLangStyle() }

room { schemaDirectory("$projectDir/schemas") }

ksp { arg("room.incremental", "true") }

// Dependencies
dependencies {

    implementation(project(":color"))

    // Core
    implementation(libs.bundles.core)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidxCompose)
    implementation(libs.bundles.accompanist)
    implementation(libs.androidx.compose.ui.tooling)

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    // ShapeIndicator
    implementation(libs.shapeIndicator)

    // DI
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Work
    implementation(libs.androidx.work.runtime.ktx)

    // Network
    implementation(libs.okhttp)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Adaptive Navigation
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

    // ML Kit Barcode Scanning
    implementation(libs.barcode.scanning)

    // Image
    implementation(libs.coil.kt.compose)

    // Storage
    implementation(libs.mmkv)

    // PDF
    implementation(libs.itext.core)

    // Tests
    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}