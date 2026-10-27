object Dependencies {
    object Pods {
        const val FIREBASE_AUTH = "FirebaseAuth"
        const val FIREBASE_ANALYTICS = "FirebaseAnalytics"
        const val GOOGLE_SIGN_IN = "GoogleSignIn"
        const val FIREBASE_CRASHLYTICS = "FirebaseCrashlytics"
        const val FIREBASE_REMOTE_CONFIG = "FirebaseRemoteConfig"
        const val FIREBASE_MESSAGING = "FirebaseMessaging"
        const val AMPLITUDE = "Amplitude"
        const val APPS_FLYER = "AppsFlyerFramework"

        object CleverTap {
            const val MODULE = "CleverTapSDK"
            const val SDK = "CleverTap-iOS-SDK"
        }
    }

    object Compose {
        const val RUNTIME = "org.jetbrains.compose.runtime:runtime:${Versions.COMPOSE}"
        const val FOUNDATION = "org.jetbrains.compose.foundation:foundation:${Versions.COMPOSE}"
        const val MATERIAL = "org.jetbrains.compose.material:material:${Versions.COMPOSE}"
        const val MATERIAL3 = "org.jetbrains.compose.material3:material3:${Versions.COMPOSE}"
        const val ANIMATION = "org.jetbrains.compose.animation:animation:${Versions.COMPOSE}"
        const val ANIMATION_GRAPHICS =
            "org.jetbrains.compose.animation:animation-graphics:${Versions.COMPOSE}"
        const val EXTENDED_ICONS =
            "org.jetbrains.compose.material:material-icons-extended:${Versions.COMPOSE}"
        const val RESOURCES =
            "org.jetbrains.compose.components:components-resources:${Versions.COMPOSE}"
        const val UI_TOOLING = "org.jetbrains.compose.ui:ui-tooling:${Versions.COMPOSE}"
    }

    object Kotlin {
        const val DATE_TIME = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.DATE_TIME}"
    }

    object Koin {
        const val CORE = "io.insert-koin:koin-core:${Versions.KOIN}"
        const val COMPOSE = "io.insert-koin:koin-compose:${Versions.KOIN_COMPOSE}"
        const val ANDROID = "io.insert-koin:koin-android:${Versions.KOIN}"
        const val ANDROID_COMPOSE = "io.insert-koin:koin-androidx-compose:${Versions.KOIN}"
    }

    object Voyager {
        const val NAVIGATOR = "cafe.adriel.voyager:voyager-navigator:${Versions.VOYAGER}"
        const val BOTTOM_SHEET =
            "cafe.adriel.voyager:voyager-bottom-sheet-navigator:${Versions.VOYAGER}"
        const val TAB_NAVIGATOR = "cafe.adriel.voyager:voyager-tab-navigator:${Versions.VOYAGER}"
        const val SCREEN_MODEL = "cafe.adriel.voyager:voyager-screenmodel:${Versions.VOYAGER}"
        const val KOIN = "cafe.adriel.voyager:voyager-koin:${Versions.VOYAGER}"
    }

    object AndroidX {
        const val ACTIVITY_COMPOSE =
            "androidx.activity:activity-compose:${Versions.ACTIVITY_COMPOSE}"
        const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
        const val CORE_KTS = "androidx.core:core-ktx:${Versions.CORE_KTS}"
        const val SECURITY_CRYPTO = "androidx.security:security-crypto:${Versions.SECURITY_CRYPTO}"
        const val SPLASH_SCREEN = "androidx.core:core-splashscreen:${Versions.SPLASH_SCREEN}"
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:${Versions.FIREBASE}"
        const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics"
        const val ANALYTICS = "com.google.firebase:firebase-analytics"
        const val MESSAGING = "com.google.firebase:firebase-messaging-ktx"
        const val PLAY_SERVICES_AUTH =
            "com.google.android.gms:play-services-auth:${Versions.PLAY_SERVICES_AUTH}"
    }

    object Coil {
        const val CORE = "io.coil-kt.coil3:coil:${Versions.COIL}"
        const val COMPOSE = "io.coil-kt.coil3:coil-compose:${Versions.COIL}"
        const val NETWORK = "io.coil-kt.coil3:coil-network-ktor3:${Versions.COIL}"
        const val SVG = "io.coil-kt.coil3:coil-svg:${Versions.COIL}"
    }

    object ExoPlayer {
        const val PLAYER = "androidx.media3:media3-exoplayer:${Versions.EXO_PLAYER}"
        const val HLS = "androidx.media3:media3-exoplayer-hls:${Versions.EXO_PLAYER}"
        const val UI = "androidx.media3:media3-ui:${Versions.EXO_PLAYER}"
        const val SESSION = "androidx.media3:media3-session:${Versions.EXO_PLAYER}"
    }

    object YoutubePlayer {
        const val CORE = "com.pierfrancescosoffritti.androidyoutubeplayer:core:${Versions.YOUTUBE_PLAYER}"
        const val CUSTOM_UI = "com.pierfrancescosoffritti.androidyoutubeplayer:custom-ui:${Versions.YOUTUBE_PLAYER}"
    }

    object Modules {
        const val CORE_APP = ":coreapp"
        const val SAMPLE_APP_SHARED = ":sampleAppShared"
    }

    object GitLive {
        const val CONFIG = "dev.gitlive:firebase-config:${Versions.GIT_LIVE}"
    }

    object Camera {
        const val CAMERA_VIEW = "androidx.camera:camera-view:${Versions.CAMERA_X}"
        const val CAMERA2 = "androidx.camera:camera-camera2:${Versions.CAMERA_X}"
        const val CORE = "androidx.camera:camera-core:${Versions.CAMERA_X}"
        const val LIFECYCLE = "androidx.camera:camera-lifecycle:${Versions.CAMERA_X}"
        const val VIDEO = "androidx.camera:camera-video:${Versions.CAMERA_X}"
    }

    const val STRAPI_KMM = "com.metacto:strapi:${Versions.STRAPI_KMM}"
    const val ANDROID_IMAGE_PICKER = "com.github.dhaval2404:imagepicker:${Versions.ANDROID_IMAGE_PICKER}"
    const val LIB_PHONE_NUMBER = "io.github.luca992.libphonenumber-kotlin:libphonenumber:${Versions.LIB_PHONE_NUMBER}"
    const val ANDROID_CROPPER = "com.vanniktech:android-image-cropper:${Versions.ANDROID_CROPPER}"
    const val COMPOTTIE = "io.github.alexzhirkevich:compottie:${Versions.COMPOTTIE}"
    const val WEBVIEW = "io.github.kevinnzou:compose-webview-multiplatform:${Versions.WEBVIEW}"
    const val SHIMMER = "com.valentinilk.shimmer:compose-shimmer:${Versions.SHIMMER}"
    const val KMP_NOTIFIER = "io.github.mirzemehdi:kmpnotifier:${Versions.KMP_NOTIFIER}"
    const val STATELY_COMMON = "co.touchlab:stately-common:2.0.5"
}