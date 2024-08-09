plugins {
    id("cleema.android.library")
    id("cleema.android.library.compose")
    id("cleema.android.hilt")
}

android {
    namespace = "de.cleema.android.core.components"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:models"))
    implementation(project(":core:styling"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.osmdroid)
    implementation(libs.konfetti)
    implementation(libs.markwon.core)
    implementation(libs.markwon.tables)
    implementation(libs.markwon.strikethrough)
    implementation(libs.markwon.html)
    implementation(libs.markwon.linkify)
    implementation(libs.markwon.coil)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    testImplementation(project(":core:testing"))
}
