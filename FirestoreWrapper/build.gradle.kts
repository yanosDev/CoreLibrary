@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    id(libs.plugins.mavenPublish.get().pluginId)
}

android {
    namespace = "de.yanos.firestorewrapper"
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
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(libs.coreLibrary)
    implementation(libs.androidx.core.ktx)
    val firebaseBom = platform(libs.firebase.bom)
    implementation(firebaseBom)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    implementation(libs.kotlinx.coroutines.android)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "de.yanos"
            artifactId = "firestorewrapper"
            version = libs.versions.core.lib.get()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}