/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.*

plugins {
    id("cleema.android.application")
    id("cleema.android.application.compose")
    id("cleema.android.hilt")
    kotlin("kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

secrets {
    propertiesFileName = "secret.properties"
    defaultPropertiesFileName = "secret.defaults.properties"
    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

android {
    namespace = "de.cleema.android"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "de.cleema.android"

        // Dynamically set versionCode
        versionCode = if (project.hasProperty("newVersionCode")) {
            project.property("newVersionCode").toString().toInt()
        } else {
            1
        }
        versionName = "1.1.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // Retrieve properties or use default values
            val storeFilePath = project.findProperty("ANDROID_RELEASE_KEYSTORE_FILE_PATH") as String? ?: "prod.keystore"
            val storePassword = project.findProperty("ANDROID_RELEASE_KEYSTORE_PASSWORD") as String? ?: ""
            val keyAlias = project.findProperty("ANDROID_RELEASE_KEYSTORE_KEY_ALIAS") as String? ?: ""
            val keyPassword = project.findProperty("ANDROID_RELEASE_KEYSTORE_KEY_PASSWORD") as String? ?: ""

            storeFile = file(storeFilePath)
            this.storeFile = storeFile
            this.storePassword = storePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        val debug by getting {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:styling"))
    implementation(project(":core:models"))
    implementation(project(":core:components"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))

    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.arrow.core)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.kotlinx.datetime)
    implementation(libs.konfetti)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)

    testImplementation(libs.hilt.android.testing)
    // AndroidX Test - JVM testing
    testImplementation(libs.androidx.test.rules)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    // AndroidX Test - Instrumented testing
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
//    androidTestImplementation(libs.androidx.navigation.testing)
//    androidTestImplementation(libs.accompanist.testharness)
//    androidTestImplementation(kotlin("test"))
//    debugImplementation(libs.androidx.compose.ui.testManifest)
}



kapt {
    correctErrorTypes = true
}

// androidx.test is forcing JUnit, 4.12. This forces it to use 4.13
configurations.configureEach {
    resolutionStrategy {
        force(libs.junit4)
        // Temporary workaround for https://issuetracker.google.com/174733673
        force("org.objenesis:objenesis:2.6")
    }
}
