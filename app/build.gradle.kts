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
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }

  buildFeatures { compose = true }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.15"
  }

  packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
  // Core + Compose
  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
  implementation("androidx.activity:activity-compose:1.7.0")
  implementation(platform("androidx.compose:compose-bom:2023.03.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.9.5")
  implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

  // Hilt
  implementation("com.google.dagger:hilt-android:2.54")
  ksp("com.google.dagger:hilt-compiler:2.54")

  // Room
  implementation("androidx.room:room-runtime:2.8.3")
  ksp("androidx.room:room-compiler:2.8.3")
  implementation("androidx.room:room-ktx:2.8.3")

  // Retrofit
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")

  // Coil
  implementation("io.coil-kt:coil-compose:2.4.0")

  // Datastore
  implementation("androidx.datastore:datastore-preferences:1.1.7")

  // Foundation & Accompanist
  implementation("androidx.compose.foundation:foundation:1.9.4")
  implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.4-beta")

  // Splash
  implementation("androidx.core:core-splashscreen:1.0.1")
}
