import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

val apiBaseUrlFromProperty = ((project.findProperty("API_BASE_URL") as String?)
    ?: localProperties.getProperty("API_BASE_URL"))
    ?.trim()
    ?.removeSuffix("/")
    ?.plus("/")
    ?: "http://10.0.2.2:3000/"

android {
    namespace = "com.example.coffeu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.coffeu"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrlFromProperty\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Dependencia de Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Dependencia de ViewModel Compose (para la función viewModel() )
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // --- Retrofit y Librerías de Red ---
    // 1. Cliente HTTP (Retrofit)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // 2. Conversor JSON (Gson) - Necesario para convertir tus Modelos a JSON y viceversa
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // Logging de red solo en debug
    debugImplementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")

    // Coil para carga asíncrona de imágenes desde URL
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    ksp("com.google.dagger:hilt-android-compiler:2.56.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

        // Dependecias para FIREBASE AUTETICADOR DE MSJ
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")
    // Firebase BOM + Auth (el BOM va primero para gestionar versiones)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Facebook Login SDK
    implementation("com.facebook.android:facebook-login:17.0.2")
}
