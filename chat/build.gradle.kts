@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    id(libs.plugins.mavenPublish.get().pluginId)
}

android {
    namespace = "de.yanos.chat"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(mapOf("path" to ":CrashLog")))
    implementation(project(mapOf("path" to ":FirestoreWrapper")))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
}



publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "de.yanos"
            artifactId = "chat"
            version = libs.versions.core.lib.get()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}