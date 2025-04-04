plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    //id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.radiowaveproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.radiowaveproject"
        minSdk = 31

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        viewBinding = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.0"
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    buildToolsVersion = "35.0.0"
    ndkVersion = "25.2.9519653"
}

dependencies {
    // Core AndroidX dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM (Bill of Materials)
    implementation(platform(libs.androidx.compose.bom))
    // Jetpack Compose Navigation dependency
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // Compose dependencies
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.appcompat)
    implementation(libs.support.annotations)
    implementation(libs.androidx.legacy.support.v13)
    implementation(libs.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media)
    // Material Icons Extended dependency for Icons.Default.*
    implementation("androidx.compose.material:material-icons-extended:1.4.3")

    // DNS
    implementation("dnsjava:dnsjava:3.5.2")


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
