plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("app.cash.sqldelight") version "2.0.0"
    kotlin("plugin.serialization") version "1.9.0"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            implementation("dev.gitlive:firebase-firestore:1.8.1")
            implementation("dev.gitlive:firebase-common:1.8.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            implementation("dev.gitlive:firebase-auth:1.11.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation("app.cash.sqldelight:android-driver:2.0.0")
            implementation("dev.gitlive:firebase-auth:1.11.1")

        }
        iosMain.dependencies {
            implementation("app.cash.sqldelight:native-driver:2.0.0")
            implementation("dev.gitlive:firebase-auth:1.11.1")

        }

    }
}

repositories {
    google()
    mavenCentral()
}

sqldelight {
    databases {
        create("NoteDatabase") {
            packageName.set("com.ncs.notesapp")
        }
    }
}


android {
    namespace = "com.ncs.notesapp"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.common.ktx)
}
