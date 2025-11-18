plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.arrow.bom))
            implementation(libs.arrow.core)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
