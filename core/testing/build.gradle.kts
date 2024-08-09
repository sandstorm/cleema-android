plugins {
    id("cleema.android.library")
    id("cleema.android.library.compose")
    id("cleema.android.hilt")
}

android {
    namespace = "de.cleema.android.core.testing"
}

dependencies {
    api(libs.junit4)
    api(libs.androidx.test.core)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)

    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.compose.ui.test)
    api(libs.hilt.android.testing)

    debugApi(libs.androidx.compose.ui.testManifest)
}
