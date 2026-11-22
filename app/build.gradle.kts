import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Signing config lives OUTSIDE the repo (default /workspace/keys/nfc-switch.properties).
val ksPropsFile = file(System.getenv("NFC_SWITCH_SIGNING") ?: "/workspace/keys/nfc-switch.properties")
val ksProps = Properties().apply { if (ksPropsFile.exists()) ksPropsFile.inputStream().use { load(it) } }

android {
    namespace = "tw.nfcswitch"
    compileSdk = 35
    defaultConfig {
        applicationId = "tw.nfcswitch"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    signingConfigs {
        if (ksProps.isNotEmpty()) create("release") {
            storeFile = file(ksProps["storeFile"] as String)
            storePassword = ksProps["storePassword"] as String
            keyAlias = ksProps["keyAlias"] as String
            keyPassword = ksProps["keyPassword"] as String
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            if (ksProps.isNotEmpty()) signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    testOptions { unitTests.isIncludeAndroidResources = true; unitTests.all { it.forkEvery = 1 } }
    lint { abortOnError = false; checkReleaseBuilds = false }
    packaging { resources { excludes += setOf("/META-INF/{AL2.0,LGPL2.1}") } }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-material3:1.1.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("androidx.test:core-ktx:1.6.1")
}
