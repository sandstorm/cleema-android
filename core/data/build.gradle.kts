plugins {
    id("cleema.android.library")
//    id("cleema.android.library.jacoco")
    id("cleema.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "de.cleema.android.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:models"))
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.security.crypto)

    implementation(libs.androidx.core.ktx)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
