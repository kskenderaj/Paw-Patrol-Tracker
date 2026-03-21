/**
 * PawTracker — root Gradle project.
 *
 * Plugin and toolchain versions are in [gradle.properties] (kotlin.version, agp.version, compose.version).
 * Real build logic lives in [shared/build.gradle.kts] and [androidApp/build.gradle.kts].
 *
 * This file only registers plugins on the classpath (`.apply(false)`); it does not add dependencies.
 * That is normal for Kotlin Multiplatform + Compose Multiplatform setups.
 */
plugins {
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
}
