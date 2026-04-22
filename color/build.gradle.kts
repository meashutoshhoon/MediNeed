plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17) // ✅ Keep consistent (or upgrade fully to 21)
}

android {
    namespace = "com.jb.medineed.app.color"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)

    // Core
    implementation(libs.androidx.core.ktx)
}