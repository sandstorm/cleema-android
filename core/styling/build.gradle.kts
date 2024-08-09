plugins {
    id("cleema.android.library")
    id("cleema.android.library.compose")
}

android {
//    lint {
//        checkDependencies = true
//    }
    namespace = "de.cleema.android.core.styling"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    debugApi(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.runtime)
//    lintPublish(project(":lint"))
//    androidTestImplementation(project(":core:testing"))
}
