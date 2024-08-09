plugins {
    id("kotlin")
    id("kotlinx-serialization")
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit4)
    testImplementation(kotlin("test"))
}
