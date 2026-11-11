import java.util.Properties

val vietiniai = Properties().apply {
    val failas = rootProject.file("local.properties")
    if (failas.exists()) failas.inputStream().use { load(it) }
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.lab9"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.lab9"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["MAPS_API_KEY"] = vietiniai.getProperty("MAPS_API_KEY", "")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}