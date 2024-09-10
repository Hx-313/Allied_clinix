import android.databinding.tool.writer.ViewBinding

val kotlin_version = "1.8.0"



plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}

android {
    namespace = "com.example.clininallied"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clininallied"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"


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
    viewBinding{
        enable = true;
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.material.v1130alpha04)
    implementation(libs.cardview)
    implementation (libs.glide)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.google.firebase.firestore)
   implementation(libs.gms.play.services.maps)
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlin_version"))
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation ("com.github.dangiashish:Google-Direction-Api:1.6")
    implementation(libs.support.annotations)
    annotationProcessor (libs.compiler)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}