package com.metacto.core.presentation.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.metacto.core.CoreEnvironment
import com.metacto.core.domain.repos.forceUpdate.AppUpdateSource
import com.metacto.core.domain.repos.forceUpdate.ForceUpdateRepository
import com.metacto.core.navigation.NavManager
import com.metacto.core.permissions.IPermissionManager
import com.metacto.core.presentation.globalState.ICoreGlobalState
import com.metacto.core.presentation.globalState.models.ConfirmationPopupParams
import com.metacto.core.presentation.globalState.models.ForceUpdatePopupParams
import com.metacto.core.presentation.globalState.models.LoadingType
import com.metacto.core.presentation.globalState.models.MessagePopupParams
import com.metacto.core.presentation.globalState.models.SnackBarParams
import com.metacto.core.presentation.globalState.models.SnackBarType
import com.metacto.core.presentation.itemPicker.ItemPickerSheet
import com.metacto.core.presentation.itemPicker.NativeItemPicker
import com.metacto.core.presentation.itemPicker.models.PickerItem
import com.metacto.core.utils.PlatformType
import com.metacto.core.utils.extensions.getPlatformType
import com.metacto.core.utils.extensions.isConnectionLostError
import com.metacto.core.utils.extensions.isInternetConnectionError
import com.metacto.core.utils.extensions.isInternetInterruptedError
import com.metacto.core.utils.extensions.isNetworkConnectionLostError
import com.metacto.core.utils.launchers.IIntentLauncher
import com.metacto.core.utils.resources.IResourceProvider
import com.metacto.coreApp.resources.Res
import com.metacto.coreApp.resources.error
import com.metacto.coreApp.resources.force_update_title
import com.metacto.coreApp.resources.ic_upgrade
import com.metacto.coreApp.resources.sorry_there_was_problem_connecting
import com.metacto.coreApp.resources.ok
import com.metacto.coreApp.resources.server_taking_too_long
import com.metacto.coreApp.resources.session_expired
import com.metacto.coreApp.resources.skip_update_button
import com.metacto.coreApp.resources.update_button
import com.metacto.coreApp.resources.your_session_expired_login_again
import com.metacto.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.metacto.strapikmm.errorhandling.AppException
import com.metacto.strapikmm.util.Logger
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

interface ViewEvent

interface ViewState

interface ViewSideEffect

const val SIDE_EFFECTS_KEY = "side-effects_key"

expect open class CommonViewModel()

abstract class CoreViewModel<S : ViewState, E : ViewEvent, SF : ViewSideEffect> :
    CommonViewModel(), ScreenModel, KoinComponent {

    // Inject main objects
    protected val coreGlobalState by inject<ICoreGlobalState>()
    protected val navManager by inject<NavManager>()
    protected val resourceProvider by inject<IResourceProvider>()
    protected val logger by inject<Logger>()
    protected val forceUpdateRepository by inject<ForceUpdateRepository>()
    protected val intentLauncher by inject<IIntentLauncher>()
    val permissionManager by inject<IPermissionManager>()
    private val nativeItemPicker by inject<NativeItemPicker>()
    private var isObservingItemPicker: Boolean = false
    private var onPickerItemSelected: ((PickerItem) -> Unit)? = null
    private var loadingCount = 0
    private val mutex = Mutex()

    abstract fun setInitialState(): S
    abstract fun handleEvents(event: E): Any

    private val initialState: S by lazy { setInitialState() }

    private val _viewState: MutableState<S> = mutableStateOf(initialState)

    @OptIn(ExperimentalAtomicApi::class)
    private val stateWriteLock = AtomicBoolean(false)
    val viewState: State<S> = _viewState
    val currentState: S get() = viewState.value

    private val _event: MutableSharedFlow<E> = MutableSharedFlow()

    private val _effect: Channel<SF> = Channel()
    val effect = _effect.receiveAsFlow()


    init {
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        screenModelScope.launch {
            _event.collect {
                onReceiveEvent(it)
                handleEvents(it)
            }
        }
    }

    fun setEvent(event: E) {
        screenModelScope.launch { _event.emit(event) }
    }

    /**
     * Jobs run on [Dispatchers.Default], so two of them can reduce the same state at once and the
     * later write would drop the earlier one's change. The lock makes each read-reduce-write whole.
     */
    @OptIn(ExperimentalAtomicApi::class)
    protected fun setState(reducer: S.() -> S) {
        while (!stateWriteLock.compareAndSet(expectedValue = false, newValue = true)) {
            // Spin: a reducer is a data class copy, so the lock is held for microseconds.
        }
        val newState = try {
            viewState.value.reducer().also { _viewState.value = it }
        } finally {
            stateWriteLock.store(false)
        }
        onChangeState(newState)
    }

    protected fun setStateLocked(reducer: S.() -> S) {
        screenModelScope.launch {
            mutex.withLock {
                setState { reducer() }
            }
        }
    }

    protected fun setEffect(builder: () -> SF) {
        val effectValue = builder()
        onSendSideEffect(effectValue)
        screenModelScope.launch { _effect.send(effectValue) }
    }

    open fun <T> executeCatching(
        block: suspend () -> T,
        loadingType: LoadingType = defaultLoadingType,
        errorType: ErrorType = defaultErrorType,
        scope: CoroutineScope = screenModelScope,
        context: CoroutineContext = defaultDispatcher,
        debounce: Long = 0,
        oldDebounceJob: Job? = null,
        onCreated: (Job) -> Unit = {},
        shouldShowErrorMessage: (Throwable) -> Boolean = { true },
        onError: ((Throwable, String?) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
    ): Job {
        // Cancel old job if required
        if (debounce != 0L) oldDebounceJob?.cancel()

        // Create the new job
        val newJob = scope.launch(context) {
            // Debounce if required
            if (debounce != 0L) delay(debounce)

            val hasLoading = loadingType != LoadingType.NoLoading

            try {
                if (hasLoading) showLoading(loadingType)
                block.invoke()
                if (hasLoading) hideLoading()
            } catch (_: kotlin.coroutines.cancellation.CancellationException) {
            } catch (_: CancellationException) {
            } catch (throwable: Throwable) {
                logger.log("Error: ${throwable.message}")

                // Ignore if it's a cancelled job
                if (throwable.message.equals("Job was cancelled", true)) {
                    return@launch
                }

                // Handle auth errors
                if (isAuthError(throwable)) {
                    handleAuthError()
                    return@launch
                }
                // Handle network internet errors
                if (throwable.isInternetInterruptedError()) {
                    handleNetworkError()
                    return@launch
                }

                // Handle network connection lost errors
                if (throwable.isNetworkConnectionLostError()) {
                    handleNetworkError()
                    return@launch
                }

                // Handle connection lost errors message
                if (throwable.isConnectionLostError()) {
                    handleNetworkError()
                    return@launch
                }

                // Handle network errors
                if (throwable.isInternetConnectionError()) {
                    handleNetworkError()
                    return@launch
                }

                // Handle other errors
                val errorMessage = when (throwable) {
                    is AppException -> {
                        throwable.getErrorMessage().orEmpty()
                    }

                    is SocketTimeoutException,
                    is HttpRequestTimeoutException,
                    is ConnectTimeoutException -> {
                        resourceProvider.getString(Res.string.server_taking_too_long)
                    }

                    else -> {
                        throwable.message.orEmpty()
                    }
                }
                if (hasLoading) hideLoading()
                if (shouldShowErrorMessage(throwable)) {
                    showError(
                        error = errorMessage,
                        errorType = errorType
                    )
                }
                onError?.invoke(throwable, errorMessage)
            } finally {
                onComplete?.invoke()
            }
        }

        // Invoke on created and return the new job
        onCreated.invoke(newJob)
        return newJob
    }

    open fun <T> executeSilent(
        block: suspend () -> T,
        scope: CoroutineScope = screenModelScope,
        context: CoroutineContext = defaultDispatcher,
        debounce: Long = 0,
        oldDebounceJob: Job? = null,
        onCreated: (Job) -> Unit = {},
        onError: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ): Job {
        // Cancel old job if required
        if (debounce != 0L) oldDebounceJob?.cancel()

        // Create the new job
        val newJob = scope.launch(context) {
            // Debounce if required
            if (debounce != 0L) delay(debounce)

            try {
                block.invoke()
            } catch (_: kotlin.coroutines.cancellation.CancellationException) {
            } catch (_: CancellationException) {
            } catch (throwable: Throwable) {
                logger.log("Error: ${throwable.message}")

                if (isAuthError(throwable)) {
                    handleAuthError()
                    return@launch
                }
                onError?.invoke()
            } finally {
                onComplete?.invoke()
            }
        }

        // Invoke on created and return the new job
        onCreated.invoke(newJob)
        return newJob
    }

    private fun isAuthError(throwable: Throwable): Boolean {
        return throwable is AppException && throwable.getHttpErrorCode() == 401
    }

    open suspend fun logout() {}

    open fun handleAuthError() {
        coreGlobalState.confirmationPopup(
            ConfirmationPopupParams(
                title = resourceProvider.getString(Res.string.session_expired),
                body = resourceProvider.getString(Res.string.your_session_expired_login_again),
                positiveButtonText = resourceProvider.getString(Res.string.ok),
                isCancellable = false,
                onPositiveClick = {
                    executeSilent({
                        logout()
                    })
                },
                onNegativeClick = {
                    executeSilent({
                        logout()
                    })
                }
            )
        )
    }


    open fun handleNetworkError() {
        hideLoading()
        coreGlobalState.snackBar(
            SnackBarParams(
                message = resourceProvider.getString(Res.string.sorry_there_was_problem_connecting),
                type = SnackBarType.ERROR
            )
        )
    }

    open fun showLoading(type: LoadingType = defaultLoadingType) {
        coreGlobalState.loading(type)
        loadingCount++
    }

    open fun hideLoading() {
        coreGlobalState.loading(LoadingType.NoLoading)
        loadingCount--
    }

    protected fun isLoading(): Boolean {
        return loadingCount > 0
    }

    protected fun showError(
        errorRes: StringResource,
        errorType: ErrorType = defaultErrorType
    ) {
        showError(
            error = resourceProvider.getString(errorRes),
            errorType = errorType
        )
    }

    protected fun showError(
        error: String,
        errorType: ErrorType = defaultErrorType
    ) {
        when (errorType) {
            ErrorType.Popup -> coreGlobalState.messagePopup(
                MessagePopupParams(
                    title = resourceProvider.getString(Res.string.error),
                    body = error
                )
            )

            ErrorType.SnackBar -> coreGlobalState.snackBar(
                SnackBarParams(
                    message = error,
                    type = SnackBarType.ERROR
                )
            )

            ErrorType.NoError -> {}
        }
    }

    protected inline fun <reified D, reified R> NavManager.collectNavResult(
        crossinline onResult: (R) -> Unit
    ) {
        executeSilent({
            // Get nav manager
            val navManager = this@collectNavResult

            // Collect results with view model scope
            navManager.onNavResult<D, R>(callback = onResult)
        })
    }

    suspend fun checkAppUpdates(
        appUpdateSource: AppUpdateSource,
        showTitle: Boolean = true,
        title: String? = null,
        updateButtonText: String? = null,
        skipUpdateButtonText: String? = null,
        image: DrawableResource? = null,
        showImage: Boolean = true,
        onUpdateClick: (() -> Unit)? = null,
        onSkipUpdateClick: (() -> Unit)? = null,
        onProceedAction: () -> Unit
    ) {
        // check if the title is enabled and handle the title
        val forceUpdateTitle = if (showTitle) title
            ?: resourceProvider.getString(Res.string.force_update_title) else null

        // check for app updates first
        val response = forceUpdateRepository.checkForceUpdate(appUpdateSource = appUpdateSource)

        if (response != null) {
            // Handle image based on showImage flag
            val forceUpdateImage = if (showImage) {
                image ?: Res.drawable.ic_upgrade
            } else {
                image
            }

            coreGlobalState.forceUpdatePopup(
                params = ForceUpdatePopupParams(
                    isRequired = response.isRequired,
                    title = forceUpdateTitle,
                    body = response.message,
                    image = forceUpdateImage,
                    updateButtonText = updateButtonText ?: resourceProvider.getString(Res.string.update_button),
                    skipUpdateButtonText = skipUpdateButtonText ?: resourceProvider.getString(Res.string.skip_update_button),
                    onDismiss = {
                        if (response.isRequired.not()) {
                            onProceedAction.invoke()
                        }
                    },
                    onUpdateClick = {
                        if (onUpdateClick != null) {
                            onUpdateClick.invoke()
                        } else {
                            intentLauncher.launchAppInStore(response.appId)
                        }
                    },
                    onSkipUpdateClicked = {
                        onSkipUpdateClick?.invoke()
                    }
                )
            )
        } else {
            onProceedAction()
        }
    }

    protected fun nativeItemPicker(
        items: List<PickerItem>,
        selectedItem: PickerItem? = null,
        onItemSelected: (PickerItem) -> Unit
    ) {
        if (getPlatformType() == PlatformType.IOS) {
            // Pick using native item picker
            pickItemUsingNativePicker(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected
            )
        } else {
            // Pick using item picker sheet
            pickItemUsingPickerSheet(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected
            )
        }
    }

    private fun pickItemUsingNativePicker(
        items: List<PickerItem>,
        selectedItem: PickerItem? = null,
        onItemSelected: (PickerItem) -> Unit
    ) {
        nativeItemPicker.display(
            items = items,
            selectedItem = selectedItem,
            onItemSelected = onItemSelected
        )
    }

    private fun pickItemUsingPickerSheet(
        items: List<PickerItem>,
        selectedItem: PickerItem? = null,
        onItemSelected: (PickerItem) -> Unit
    ) {
        // Observe item picker results
        onPickerItemSelected = onItemSelected
        observePickerItemResultsIfRequired()

        // Open item picker sheet
        navManager.navigateToBottomSheet(
            ItemPickerSheet(
                items = items,
                selectedItem = selectedItem
            )
        )
    }

    private fun observePickerItemResultsIfRequired() {
        // Validate, if we should observe
        // Should observe one time only to avoid issues
        if (isObservingItemPicker) return

        // Observe
        navManager.collectNavResult<ItemPickerSheet, PickerItem> {
            onPickerItemSelected?.invoke(it)
            onPickerItemSelected = null
        }

        // Update the flag
        isObservingItemPicker = true
    }

    override fun onDispose() {
        screenModelScope.cancel()
        super.onDispose()
    }

    open val defaultLoadingType: LoadingType = LoadingType.LottieBlocking()

    open val defaultErrorType: ErrorType = ErrorType.Popup

    open val defaultDispatcher: CoroutineContext = Dispatchers.Default

    open fun onReceiveEvent(event: E) {
    }

    open fun onChangeState(newState: S) {
    }

    open fun onSendSideEffect(effect: SF) {
    }
}

private fun extractErrorCodeAndMessage(jsonString: String): Pair<String, Int> {
    if (jsonString.trim().isEmpty()) return "" to -1
    try {
        // Parse the JSON string safely
        val jsonObject = JsonWithIgnoredUnknownKeys.parseToJsonElement(jsonString).let {
            // Ensure the parsed element is a JsonObject
            it as? JsonObject ?: return jsonString to -1
        }

        // Extract the message and code, with default values if not found or in the wrong format
        val message = jsonObject["message"]?.jsonPrimitive?.content.orEmpty()
        val code = jsonObject["code"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0

        return message to code
    } catch (throwable: Throwable) {
        // Handle parsing errors gracefully
        return jsonString to -1
    }
}

expect fun AppException.getHttpErrorCode(): Int?
expect fun AppException.getErrorCode(): Int?
expect fun AppException.getErrorMessage(): String?
expect fun AppException.getErrorBody(): String?
expect fun CoreEnvironment.getStoreId(): String