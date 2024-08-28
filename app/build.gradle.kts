plugins {
    // Sử dụng alias để tránh lỗi trùng plugin
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.fontendpicshare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fontendpicshare"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Jetpack Compose và Material Design 3
    implementation("androidx.compose.material3:material3:1.0.0")
    implementation("androidx.compose.material:material-icons-core:1.5.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.navigation:navigation-compose")


    // Các thư viện cần thiết khác cho Jetpack Compose
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.material:material:1.5.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation ("androidx.navigation:navigation-compose:2.7.1")

    // CameraX Core library
    implementation("androidx.camera:camera-core:1.2.2")
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")
    implementation("androidx.camera:camera-extensions:1.2.2")
    implementation ("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-core")
    implementation("androidx.camera:camera-camera2")
    implementation("androidx.camera:camera-lifecycle")
    implementation("androidx.camera:camera-view")
    implementation("androidx.camera:camera-extensions")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose")

    // LiveData and ViewModel
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1") // Check for the latest version


    // Other dependencies
    implementation("com.google.guava:guava:31.1-jre")
    implementation ("androidx.compose.material3:material3:1.2.0")  // Check for the latest version
    implementation ("androidx.compose.material:material:1.5.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation ("com.google.accompanist:accompanist-permissions:0.29.0-alpha")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.29.0-alpha")
    implementation ("androidx.compose.foundation:foundation:1.4.0")

    // Add this for the Activity Result API
    implementation ("androidx.activity:activity-ktx:1.7.0")


    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation(libs.firebase.auth)
    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation ("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation ("com.google.firebase:firebase-firestore:25.0.0")
    implementation ("com.google.firebase:firebase-storage:21.0.0")
    implementation ("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation ("com.google.firebase:firebase-firestore-ktx:25.0.0")

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)


}
