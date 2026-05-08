import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val keyProperties = Properties()
val secretsDir = rootProject.file("secrets/android")
val keyPropertiesFile = File(secretsDir, "key.properties")
if (keyPropertiesFile.exists()) {
    keyProperties.load(keyPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation("com.aliyun.openservices:aliyun-log-android-sdk:2.6.13")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.smjcco.wxpusher.kmp"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
        buildConfigField("String", "ALIYUN_SLS_ACCESS_KEY_ID", "\"${keyProperties.getProperty("aliyun.sls.accessKeyId", "")}\"")
        buildConfigField("String", "ALIYUN_SLS_ACCESS_KEY_SECRET", "\"${keyProperties.getProperty("aliyun.sls.accessKeySecret", "")}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
