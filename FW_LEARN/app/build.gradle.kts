import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.four_ways_learn"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.four_ways_learn"
        minSdk = 27
        targetSdk = 36
        versionCode = 4
        versionName = "3.0"
        val apiBase = providers.gradleProperty("FW_API_BASE_URL").getOrElse("http://192.168.1.72/four_ways_api/api/")
        require(apiBase.endsWith("/")) { "FW_API_BASE_URL debe terminar en /" }
        buildConfigField("String", "API_BASE_URL", "\"$apiBase\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.recyclerview)

    // FireBase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.livedata)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
}
