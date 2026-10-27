@file:Suppress("OPT_IN_USAGE")

import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin(Plugins.MULTIPLATFORM_PLUGIN)
    id(Plugins.ANDROID_LIBRARY_PLUGIN)
    id(Plugins.COMPOSE_PLUGIN) version Versions.COMPOSE
    id(Plugins.COMPOSE_COMPILER_PLUGIN) version Versions.KOTLIN
    id(Plugins.SERIALIZATION_PLUGIN)
    id(Plugins.PARCELIZE_PLUGIN)
    id(Plugins.SWIFT_KLIB) version Versions.SWIFT_KLIB
    id(Plugins.MAVEN_PUBLISH)
    id(Plugins.SIGNING)
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

        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }

    metadata {
        compilations.matching { it.name == "iosMain" }.all {
            compileTaskProvider.configure { enabled = false }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.materialIconsExtended)
                api(compose.components.resources)

                // Voyager
                api(Dependencies.Voyager.NAVIGATOR)
                api(Dependencies.Voyager.BOTTOM_SHEET)
                api(Dependencies.Voyager.TAB_NAVIGATOR)
                api(Dependencies.Voyager.SCREEN_MODEL)

                // Koin
                api(Dependencies.Koin.CORE)
                api(Dependencies.Koin.COMPOSE)

                // Kotlin
                api(Dependencies.Kotlin.DATE_TIME)

                // Coil
                api(Dependencies.Coil.CORE)
                api(Dependencies.Coil.COMPOSE)
                api(Dependencies.Coil.NETWORK)
                api(Dependencies.Coil.SVG)

                // GitLive
                api(Dependencies.GitLive.CONFIG)

                // Others
                api(Dependencies.STRAPI_KMM)
                api(Dependencies.LIB_PHONE_NUMBER)
                api(Dependencies.COMPOTTIE)
                api(Dependencies.WEBVIEW)
                api(Dependencies.SHIMMER)
                implementation(Dependencies.KMP_NOTIFIER)
                implementation("com.benasher44:uuid:0.8.4")  // Needed for KmpNotifier (https://github.com/mirzemehdi/KMPNotifier/issues/30)
                implementation(Dependencies.STATELY_COMMON) // Needed for KmpNotifier (https://github.com/mirzemehdi/KMPNotifier/issues/30)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                // Compose
                api(compose.uiTooling)

                // Android X
                api(Dependencies.AndroidX.ACTIVITY_COMPOSE)
                api(Dependencies.AndroidX.APP_COMPAT)
                api(Dependencies.AndroidX.CORE_KTS)
                api(Dependencies.AndroidX.SECURITY_CRYPTO)
                api(Dependencies.AndroidX.SPLASH_SCREEN)

                // Firebase
                api(project.dependencies.platform(Dependencies.Firebase.BOM))
                api(Dependencies.Firebase.DYNAMIC_LINKS)
                api(Dependencies.Firebase.CRASHLYTICS)
                api(Dependencies.Firebase.ANALYTICS)
                api(Dependencies.Firebase.MESSAGING)
                api(Dependencies.Firebase.PLAY_SERVICES_AUTH)

                // Koin
                api(Dependencies.Koin.ANDROID)
                api(Dependencies.Koin.ANDROID_COMPOSE)

                // Voyager
                api(Dependencies.Voyager.KOIN)

                // Exo Player
                api(Dependencies.ExoPlayer.PLAYER)
                api(Dependencies.ExoPlayer.HLS)
                api(Dependencies.ExoPlayer.UI)
                api(Dependencies.ExoPlayer.SESSION)

                // Youtube Player
                api(Dependencies.YoutubePlayer.CORE)
                api(Dependencies.YoutubePlayer.CUSTOM_UI)

                // Camera X
                api(Dependencies.Camera.CAMERA_VIEW)
                api(Dependencies.Camera.CAMERA2)
                api(Dependencies.Camera.CORE)
                api(Dependencies.Camera.LIFECYCLE)
                api(Dependencies.Camera.VIDEO)

                // Others
                api(Dependencies.ANDROID_IMAGE_PICKER)
                api(Dependencies.ANDROID_CROPPER)
            }
        }

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                api(compose.material)
            }
        }
    }
}

android {
    namespace = Configs.CORE_APP_ID
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
        jvmToolchain(Versions.JVM.toString().toInt())
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "${Configs.CORE_APP_ID}.resources"
    generateResClass = always
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        // The release workflow commits this directory to the repository's `maven` branch,
        // which consumers read over raw.githubusercontent.com without credentials.
        maven {
            name = "MavenBranch"
            url = uri(layout.buildDirectory.dir("maven-branch"))
        }
    }

    publications.withType<MavenPublication> {
        artifact(javadocJar)
        groupId = "com.metacto"
        version = localProperties.getProperty("PUBLISH_VERSION")

        pom {
            name.set("coreApp")
            description.set("Compose Multiplatform library for MetaCTO core app module")
            url.set("https://github.com/Meta-CTO/CoreApp")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("ahmedsalemelzeiny")
                    name.set("Ahmed Salem Elzeiny")
                    email.set("ahmedsalemelzeiny2013@gmail.com")
                }
            }
            scm {
                url.set("https://github.com/Meta-CTO/CoreApp")
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
}