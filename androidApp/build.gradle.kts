import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    id("com.huawei.agconnect")
    id("com.hihonor.mcs.asplugin")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation("androidx.work:work-runtime-ktx:2.10.0")
            implementation("com.squareup.okhttp3:okhttp:4.12.0")
            implementation("com.google.code.gson:gson:2.10.1")
            implementation("androidx.fragment:fragment:1.7.0")
            implementation("androidx.appcompat:appcompat:1.7.0")
            implementation("com.google.android.material:material:1.12.0")
            implementation("commons-codec:commons-codec:1.6")

            implementation(project(":HiMiuix"))

            implementation("com.tencent.shiply:upgrade:2.2.1-RC01") {
                exclude(group = "androidx.appcompat", module = "appcompat")
                exclude(group = "androidx.fragment", module = "fragment")
            }
            implementation("com.tencent.shiply:upgrade-ui:2.2.1-RC01") {
                exclude(group = "com.tencent.shiply", module = "upgrade")
            }
            implementation(libs.huawei.push)
            implementation("com.hihonor.mcs:push:8.0.12.307")
            implementation("io.github.scwang90:refresh-layout-kernel:2.1.0")
            implementation("io.github.scwang90:refresh-header-classics:2.1.0")
            implementation("io.github.scwang90:refresh-footer-classics:2.1.0")
        }
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "com.mars.wxpusher"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mars.wxpusher"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 100
        versionName = "1.0.0"
        setProperty("archivesBaseName", "wxpusher-android-v$versionName")

        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.jks")
            storePassword = "smjcco"
            keyAlias = "smjcco"
            keyPassword = "smjcco"
        }
        val secretsDir = rootProject.file("secrets/android")
        val keyPropertiesFile = File(secretsDir, "key.properties")
        if (keyPropertiesFile.exists()) {
            val keyProperties = Properties()
            keyProperties.load(keyPropertiesFile.inputStream())
            val storeFileName = keyProperties.getProperty("storeFile")
            val storePassword = keyProperties.getProperty("storePassword")
            val keyAlias = keyProperties.getProperty("keyAlias")
            val keyPassword = keyProperties.getProperty("keyPassword")
            if (!storeFileName.isNullOrEmpty() && !storePassword.isNullOrEmpty() && !keyAlias.isNullOrEmpty() && !keyPassword.isNullOrEmpty()) {
                create("release") {
                    this.storeFile = File(secretsDir, storeFileName)
                    this.storePassword = storePassword
                    this.keyAlias = keyAlias
                    this.keyPassword = keyPassword
                }
            }
        }
    }

    buildTypes {
        getByName("release") {
            // 关闭混淆 & 关闭资源压缩
            isMinifyEnabled = false
            isShrinkResources = false
            // 禁用混淆配置文件
            // proguardFiles(
            //     getDefaultProguardFile("proguard-android-optimize.txt"),
            //     "proguard-rules.pro"
            // )
            signingConfig = try {
                signingConfigs.getByName("release")
            } catch (_: UnknownDomainObjectException) {
                signingConfigs.getByName("debug")
            }
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    flavorDimensions.add("env")

    productFlavors {
        create("offline") {
            dimension = "env"
            versionNameSuffix = ".test"
        }
        create("prod") {
            dimension = "env"
        }
    }

    sourceSets {
        getByName("offline") {
            manifest.srcFile("src/androidOffline/AndroidManifest.xml")
        }
    }
}

dependencies {
    implementation(fileTree("libs") {
        include("*.jar")
        include("*.aar")
    })
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.7.1")
    implementation("androidx.fragment:fragment-ktx:1.5.7")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("io.github.scwang90:refresh-layout-kernel:3.0.0-alpha")
    implementation("io.github.scwang90:refresh-header-classics:3.0.0-alpha")
    implementation("com.google.zxing:core:3.3.3")
    implementation("com.tencent.mm.opensdk:wechat-sdk-android:6.8.34")
}
