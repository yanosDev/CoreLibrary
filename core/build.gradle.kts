@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    id(libs.plugins.hilt.get().pluginId)
    id(libs.plugins.kapt.get().pluginId)
    id(libs.plugins.mavenPublish.get().pluginId)
}

android {
    namespace = "de.yanos.core"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = libs.versions.compileSdk.get().toInt()
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    //Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.materialWindow)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(composeBom)

    //DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation("androidx.compose.material3:material3:1.1.0-alpha06")
    implementation("com.google.accompanist:accompanist-adaptive:0.29.1-alpha")
    implementation(libs.accompanist.systemuicontroller)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "de.yanos"
            artifactId = "core"
            version = libs.versions.yanos.lib.get()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}