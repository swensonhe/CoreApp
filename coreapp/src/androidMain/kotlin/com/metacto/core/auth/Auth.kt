package com.metacto.core.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.metacto.core.utils.extensions.getActivity
import com.metacto.strapikmm.auth.AuthOptions
import dev.gitlive.firebase.auth.PhoneVerificationProvider
import java.util.concurrent.TimeUnit

actual class AuthOptionsFactory(
    private val context: Context,
    private val launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    private var currentAuthOptions: AuthOptions? = null

    actual fun createAuthOptions(onCancelled: () -> Unit): AuthOptions {
        currentAuthOptions = AuthOptions(
            activity = context.getActivity()!!,
            launcher = launcher,
            onCanceled = {
                onCancelled.invoke()
            }
        )

        return currentAuthOptions!!
    }

    internal fun onResult(result: ActivityResult) {
        currentAuthOptions?.onResult?.invoke(result)
    }
}

@Composable
actual fun rememberAuthOptionsFactory(): AuthOptionsFactory {
    // Init the factory
    var factory: AuthOptionsFactory? = null
    val context = LocalContext.current

    // Create the activity launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            factory?.onResult(it)
        }
    )

    // Create or get the factory
    factory = remember {
        AuthOptionsFactory(
            context = context,
            launcher = launcher
        )
    }

    // Then return it
    return factory
}

@Composable
actual fun rememberPhoneVerificationProvider(): PhoneVerificationProvider {
    val activity = LocalContext.current.getActivity()!!
    return remember {
        object : PhoneVerificationProvider {
            override val activity: Activity = activity
            override val timeout: Long = 60
            override val unit: TimeUnit = TimeUnit.SECONDS

            override fun codeSent(triggerResend: (Unit) -> Unit) {
            }

            override suspend fun getVerificationCode(): String {
                return ""
            }
        }
    }
}