plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
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
  composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }

  packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
  // Compose BOM
  implementation(platform("androidx.compose:compose-bom:2024.09.02"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.foundation:foundation:1.7.3")

  // Activity / Lifecycle
  implementation("androidx.activity:activity-compose:1.9.3")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.core:core-splashscreen:1.0.1")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  // WorkManager
  implementation("androidx.work:work-runtime-ktx:2.9.1")

  // Hilt (Dagger + AndroidX Hilt) — both compilers on kapt
  implementation("com.google.dagger:hilt-android:2.54")
  kapt("com.google.dagger:hilt-compiler:2.54")

  implementation("androidx.hilt:hilt-work:1.2.0")
  kapt("androidx.hilt:hilt-compiler:1.2.0")

  // Room (KSP)
  implementation("androidx.room:room-runtime:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")

  // SQLite (aligned with Room)
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
  implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.4-beta")

  // Tooling/tests
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

kapt {
  correctErrorTypes = true
}

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
  arg("room.generateKotlin", "true")
}
