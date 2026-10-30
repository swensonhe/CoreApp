package com.metacto.core.utils.extensions

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.michaelrocks.libphonenumber.kotlin.MetadataLoader
import io.michaelrocks.libphonenumber.kotlin.metadata.source.AssetsMetadataLoader

@Composable
internal actual fun defaultMetadataLoader(): MetadataLoader {
    return AssetsMetadataLoader(LocalContext.current.applicationContext.assets)
}

@SuppressLint("ComposableNaming")
@Composable
actual fun openUrlInBrowser(url: String) {
    LocalContext.current.openUrl(url)
}

@Composable
actual fun getNotchHeight() = 0.dp

@Composable
actual fun getBottomHandleHeight() = 0.dp

@Composable
actual fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    return remember(bytes) {
        if (bytes != null) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        } else {
            null
        }
    }
}

@Composable
actual fun isGesturesNavBarEnabled(): Boolean {
    return LocalContext.current.getNavigationBarInteractionMode() == NAVIGATION_BAR_INTERACTION_MODE_GESTURE
}

@SuppressLint("ComposableNaming")
@Composable
actual fun setStatusBarColor(isDark: Boolean) {
    val insetsController = getInsetsController()
    LaunchedEffect(isDark) {
        insetsController?.isAppearanceLightStatusBars = isDark.not()
    }
}

@SuppressLint("ComposableNaming")
@Composable
actual fun setNavigationBarColor(isDark: Boolean) {
    val insetsController = getInsetsController()
    LaunchedEffect(isDark) {
        insetsController?.isAppearanceLightNavigationBars = isDark.not()
    }
}

@Composable
fun getInsetsController(): WindowInsetsControllerCompat? {
    val view = LocalView.current
    val window = LocalContext.current.getActivity()?.window

    return window?.let {
        WindowCompat.getInsetsController(window, view)
    }
}

@SuppressLint("ComposableNaming")
@Composable
actual fun dismissKeyboard() {
    LocalView.current.hideKeyboard()
    LocalSoftwareKeyboardController.current?.hide()
}

@Composable
actual fun OnLifecycleEvent(
    onCreate: () -> Unit,
    onStart: () -> Unit,
    onResume: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onDestroy: () -> Unit,
    onAny: () -> Unit,
    onDispose: () -> Unit
) {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate.invoke()
                Lifecycle.Event.ON_START -> onStart.invoke()
                Lifecycle.Event.ON_RESUME -> onResume.invoke()
                Lifecycle.Event.ON_PAUSE -> onPause.invoke()
                Lifecycle.Event.ON_STOP -> onStop.invoke()
                Lifecycle.Event.ON_DESTROY -> onDestroy.invoke()
                Lifecycle.Event.ON_ANY -> onAny.invoke()
            }
        }

        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            onDispose.invoke()
            lifecycle.removeObserver(observer)
        }
    }
}