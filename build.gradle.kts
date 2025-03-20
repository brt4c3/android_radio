// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    //alias(libs.plugins.android.application) apply false
    //alias(libs.plugins.kotlin.android) apply false
    //alias(libs.plugins.kotlin.compose) apply false
    id("com.android.application") version "8.9.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false

}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}