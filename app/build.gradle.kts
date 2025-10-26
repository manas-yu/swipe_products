plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
  id("dagger.hilt.android.plugin")
}

android {
  namespace = "com.example.swipe_assignment"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.swipe_assignment"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
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
  kotlinOptions { jvmTarget = "1.8" }

  buildFeatures { compose = true }

  // Compose compiler 1.5.15 requires Kotlin 1.9.25 (you have that at root)
  composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }

  packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
  // ✅ Real Compose BOM (has pullRefresh in foundation 1.7.x line)
  implementation(platform("androidx.compose:compose-bom:2024.09.02"))

  // Core Compose
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")

  // ✅ Foundation includes androidx.compose.foundation.pullrefresh.*
  implementation("androidx.compose.foundation:foundation:1.7.3")

  implementation("androidx.compose.material3:material3")

  // Activity / Lifecycle
  implementation("androidx.activity:activity-compose:1.9.3")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
  implementation("androidx.core:core-ktx:1.13.1")

  // WorkManager
  implementation("androidx.work:work-runtime-ktx:2.9.1")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  // Hilt
  implementation("com.google.dagger:hilt-android:2.54")
  ksp("com.google.dagger:hilt-compiler:2.54")

  // Room — Kotlin 1.9.x compatible line
  implementation("androidx.room:room-runtime:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")

  // Align SQLite with Room 2.6.1
  implementation("androidx.sqlite:sqlite:2.3.1")
  implementation("androidx.sqlite:sqlite-framework:2.3.1")

  // Retrofit / OkHttp
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

  // Coil
  implementation("io.coil-kt:coil-compose:2.4.0")

  // Datastore
  implementation("androidx.datastore:datastore-preferences:1.1.1")

  implementation("com.google.accompanist:accompanist-swiperefresh:0.31.4-beta")

  // Tooling/tests
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

// Optional: keep Room schema JSONs to silence export warning (create app/schemas folder)
ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
  arg("room.generateKotlin", "true")
}
