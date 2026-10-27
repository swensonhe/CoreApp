@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin(Plugins.MULTIPLATFORM_PLUGIN)
    kotlin(Plugins.COCOAPODS_PLUGIN)
    id(Plugins.ANDROID_LIBRARY_PLUGIN)
    id(Plugins.COMPOSE_PLUGIN) version Versions.COMPOSE
    id(Plugins.COMPOSE_COMPILER_PLUGIN) version Versions.KOTLIN
    id(Plugins.SERIALIZATION_PLUGIN)
    id(Plugins.PARCELIZE_PLUGIN)
}

kotlin {
    applyDefaultHierarchyTemplate()

    iosArm64()
    iosSimulatorArm64()

    androidTarget {
        compilerOptions {
            jvmTarget.value(JvmTarget.JVM_17)
            freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.metacto.core.utils.CommonParcelize")
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "Sample app shared module"
        homepage = "https://www.metacto.com/"
        ios.deploymentTarget = Configs.IOS_DEPLOYMENT_TARGET
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = Configs.SAMPLE_APP_FRAMEWORK_NAME
            isStatic = true
            linkerOpts.add("-lsqlite3")

            if (System.getenv("XCODE_VERSION_MAJOR") == "1500") {
                linkerOpts += "-ld64"
            }

            export(project(Dependencies.Modules.CORE_APP))
        }
        pod(
            name = Dependencies.Pods.FIREBASE_AUTH,
            linkOnly = true
        )
        pod(
            name = Dependencies.Pods.AMPLITUDE,
            linkOnly = true
        )
        pod(name = Dependencies.Pods.CleverTap.SDK) {
            moduleName = Dependencies.Pods.CleverTap.MODULE
            linkOnly = true
        }
        pod(
            name = Dependencies.Pods.GOOGLE_SIGN_IN,
            version = Versions.GOOGLE_SIGN_IN_POD,
            linkOnly = true
        )
        pod(
            name = Dependencies.Pods.FIREBASE_DYNAMIC_LINKS,
            linkOnly = true
        )
        pod(
            name = Dependencies.Pods.FIREBASE_CRASHLYTICS,
            linkOnly = true
        )
        pod(
            name = Dependencies.Pods.FIREBASE_REMOTE_CONFIG,
            linkOnly = true
        )
        pod(
            name = Dependencies.Pods.FIREBASE_MESSAGING,
            linkOnly = true
        )
        pod(
            name = Dependencies.Pods.FIREBASE_ANALYTICS,
            linkOnly = true
        )
        pod(
            name = Dependencies.Pods.APPS_FLYER,
            linkOnly = true
        )

        xcodeConfigurationToNativeBuildType["Debug"] = NativeBuildType.RELEASE
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(Dependencies.Modules.CORE_APP))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
            }
        }

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
            }
        }
    }
}

android {
    namespace = Configs.SAMPLE_APP_SHARED_NAMESPACE
    compileSdk = Configs.COMPILE_SDK_VERSION

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")

    defaultConfig {
        minSdk = Configs.MIN_SDK_VERSION
    }

    compileOptions {
        sourceCompatibility = Versions.JVM
        targetCompatibility = Versions.JVM
    }

    kotlin {
        jvmToolchain(Versions.JVM.majorVersion.toInt())
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "${Configs.SAMPLE_APP_NAMESPACE}.resources"
    generateResClass = always
}