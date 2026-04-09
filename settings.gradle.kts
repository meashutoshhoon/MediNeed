@file:Suppress("UnstableApiUsage") // Ignore warnings for APIs still marked unstable

pluginManagement {
    repositories {
        google {
            content {
                // Allow only Android/Google related plugins from Google repo
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()        // Central Maven repository
        gradlePluginPortal()  // Gradle plugins
    }
}

plugins {
    // Helps Gradle resolve Java toolchains automatically
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    // Prevent modules from defining their own repositories
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()        // Android dependencies
        mavenCentral()  // Common libraries
    }
}

// Root project name
rootProject.name = "MediNeed"

// Included modules
include(":app")
include(":color")