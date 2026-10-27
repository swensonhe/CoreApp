package com.metacto.core.auth

import androidx.compose.runtime.Composable
import com.metacto.strapikmm.auth.AuthOptions
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import cocoapods.FirebaseAuth.FIRAuthUIDelegateProtocol
import dev.gitlive.firebase.auth.PhoneVerificationProvider
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

actual class AuthOptionsFactory(
    private val viewController: UIViewController
) {
    actual fun createAuthOptions(onCancelled: () -> Unit): AuthOptions {
        return AuthOptions(
            presentingViewController = viewController
        )
    }
}

@Composable
actual fun rememberAuthOptionsFactory(): AuthOptionsFactory {
    val currentViewController = LocalUIViewController.current
    return remember(currentViewController) {
        AuthOptionsFactory(
            viewController = currentViewController
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberPhoneVerificationProvider(): PhoneVerificationProvider {
    return remember {
        object : PhoneVerificationProvider {
            override val delegate: FIRAuthUIDelegateProtocol? = null

            override suspend fun getVerificationCode(): String {
                return ""
            }
        }
    }
}