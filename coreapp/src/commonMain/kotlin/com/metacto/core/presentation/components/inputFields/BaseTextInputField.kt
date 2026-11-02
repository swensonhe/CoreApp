package com.metacto.core.presentation.components.inputFields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.metacto.core.presentation.theme.CoreTheme
import com.metacto.core.presentation.theme.CoreTheme.spacings
import com.metacto.core.utils.extensions.DelayedLaunchedEffect
import com.metacto.core.utils.extensions.focusRequesterIfNotNull
import com.metacto.core.utils.extensions.noRippleClickable
import com.metacto.core.utils.extensions.rememberFocusRequester
import com.metacto.core.utils.extensions.removeAllNonNumeric
import com.metacto.core.utils.extensions.tintIfNotNull
import com.metacto.core.utils.extensions.toDp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal val DEFAULT_REQUEST_FOCUS_DELAY = 250.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTextInputField(
    text: String,
    label: String?,
    isStaticLabel: Boolean,
    onValueChange: ((String) -> Unit)?,
    onClick: (() -> Unit)?,
    modifier: Modifier,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    capitalization: KeyboardCapitalization,
    backgroundColor: Color,
    focusedBorderColor: Color,
    unFocusedBorderColor: Color,
    singleLine: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    visualTransformation: VisualTransformation,
    textColor: Color,
    placeholderTextColor: Color,
    labelTextColor: Color,
    errorTextColor: Color,
    maxLength: Int,
    minLines: Int,
    maxLines: Int,
    minWidth: Dp,
    minHeight: Dp,
    error: String?,
    endIconVector: ImageVector?,
    endIconPainter: Painter?,
    endIconSize: Dp,
    onEndIconClick: (() -> Unit)?,
    startIconVector: ImageVector?,
    startIconPainter: Painter?,
    startIconSize: Dp,
    onStartIconClick: (() -> Unit)?,
    iconTintColor: Color?,
    placeholder: String?,
    placeholderMaxLines: Int,
    shape: RoundedCornerShape,
    placeholderTextStyle: TextStyle,
    textStyle: TextStyle,
    textAlign: TextAlign?,
    labelTextStyle: TextStyle,
    errorTextStyle: TextStyle,
    allowDigitsOnly: Boolean,
    floatingLabelSpacing: Dp,
    contentPadding: PaddingValues,
    focusedBorderThickness: Dp,
    unfocusedBorderThickness: Dp,
    shadowColor: Color,
    elevation: Dp,
    requestFocus: Boolean,
    requestFocusDelay: Duration = DEFAULT_REQUEST_FOCUS_DELAY,
    platformImeOptions: PlatformImeOptions? = null
) {
    // Prepare isError flag
    val isError = remember(error) {
        error != null
    }

    // Prepare field box size state
    var fieldBoxSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    // Prepare text value state
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = text, selection = TextRange(text.length)
            )
        )
    }

    // Text field value state
    if (textFieldValueState.text != text) {
        textFieldValueState = TextFieldValue(
            text = text, selection = TextRange(text.length)
        )
    }

    // Prepare leading image
    val leadingImage: @Composable (() -> Unit)? = if (startIconVector != null) {
        {
            Image(
                imageVector = startIconVector,
                contentDescription = null,
                colorFilter = tintIfNotNull(iconTintColor),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(startIconSize)
                    .noRippleClickable(onClick = onStartIconClick)
            )
        }
    } else if (startIconPainter != null) {
        {
            Image(
                painter = startIconPainter,
                contentDescription = null,
                colorFilter = tintIfNotNull(iconTintColor),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(startIconSize)
                    .noRippleClickable(onClick = onStartIconClick)
            )
        }
    } else null

    // Prepare trailing image
    val trailingImage: @Composable (() -> Unit)? = if (endIconVector != null) {
        {
            Image(
                imageVector = endIconVector,
                contentDescription = null,
                colorFilter = tintIfNotNull(iconTintColor),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(endIconSize)
                    .noRippleClickable(onClick = onEndIconClick)
            )
        }
    } else if (endIconPainter != null) {
        {
            Image(
                painter = endIconPainter,
                contentDescription = null,
                colorFilter = tintIfNotNull(iconTintColor),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(endIconSize)
                    .noRippleClickable(onClick = onEndIconClick)
            )
        }
    } else null

    // Prepare placeholder text
    val placeholderText: @Composable (() -> Unit)? = if (placeholder != null) {
        {
            Text(
                text = placeholder,
                style = placeholderTextStyle,
                color = placeholderTextColor,
                maxLines = placeholderMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else null

    // Prepare label text
    val labelText: @Composable (() -> Unit)? = if (isStaticLabel.not() && label != null) {
        {
            Text(
                text = label,
                style = labelTextStyle,
                color = labelTextColor,
            )
        }
    } else null

    // Prepare value change handler
    val valueChangeHandler: (TextFieldValue) -> Unit = remember(maxLength) {
        {
            // Get input text
            var inputText = it.text
            // Handle allow digits only validation
            if (allowDigitsOnly) {
                inputText = inputText.removeAllNonNumeric()
            }
            // Handle length validation
            if (inputText.length > maxLength) {
                inputText = inputText.substring(0, maxLength)
            }
            // Then invoke value changed
            onValueChange?.invoke(inputText)
            // Update field state
            textFieldValueState = it
        }
    }

    // Request focus if required
    var focusRequester: FocusRequester? = null
    if (requestFocus) {
        focusRequester = rememberFocusRequester()
        DelayedLaunchedEffect(
            key = requestFocus,
            delay = requestFocusDelay.inWholeMilliseconds
        ) {
            focusRequester.requestFocus()
        }
    }

    // Prepare colors
    val colors = TextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor.copy(alpha = 0.38f),
        errorTextColor = textColor,
        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,
        disabledContainerColor = backgroundColor.copy(alpha = 0.38f),
        errorContainerColor = backgroundColor,
        cursorColor = Color.Unspecified,
        errorCursorColor = CoreTheme.colors.danger,
        errorPlaceholderColor = CoreTheme.colors.danger,
        focusedIndicatorColor = focusedBorderColor,
        unfocusedIndicatorColor = unFocusedBorderColor,
        disabledIndicatorColor = unFocusedBorderColor.copy(alpha = 0.38f),
        errorIndicatorColor = CoreTheme.colors.danger,
        selectionColors = TextSelectionColors(
            handleColor = focusedBorderColor,
            backgroundColor = focusedBorderColor.copy(alpha = 0.4f)
        ),
    )

    // Get interaction source
    val interactionSource = remember { MutableInteractionSource() }

    // Prepare the text color
    val currentTextColor =
        textStyle.color.takeOrElse {
            val focused = interactionSource.collectIsFocusedAsState().value
            when {
                !enabled -> colors.disabledTextColor
                isError -> colors.errorTextColor
                focused -> colors.focusedTextColor
                else -> colors.unfocusedTextColor
            }
        }

    // And merge the text style
    val mergedTextStyle = textStyle.merge(
        TextStyle(
            color = currentTextColor,
            textAlign = textAlign ?: TextAlign.Start
        )
    )

    // Container column
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacings.paddingSmall)
    ) {
        // Static label if needed
        if (label != null && isStaticLabel) {
            Text(
                text = label,
                style = labelTextStyle,
                color = labelTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Container box
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldBoxSize = it }
                .shadow(
                    elevation = elevation,
                    spotColor = shadowColor,
                    ambientColor = shadowColor,
                    shape = shape
                )
        ) {
            CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
                // Render basic text field
                BasicTextField(
                    value = textFieldValueState,
                    onValueChange = { newValue ->
                        textFieldValueState = newValue
                        valueChangeHandler(newValue)
                    },
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = mergedTextStyle,
                    visualTransformation = visualTransformation,
                    keyboardActions = keyboardActions,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction,
                        capitalization = capitalization,
                        platformImeOptions = platformImeOptions
                    ),
                    interactionSource = interactionSource,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    minLines = minLines,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(
                            minWidth = minWidth,
                            minHeight = minHeight
                        )
                        .focusRequesterIfNotNull(focusRequester)
                        .then(
                            if (label != null && isStaticLabel.not()) {
                                Modifier
                                    .semantics(mergeDescendants = true) {}
                                    .padding(top = floatingLabelSpacing)
                            } else {
                                Modifier
                            }
                        ),
                    decorationBox = @Composable { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = textFieldValueState.text,
                            visualTransformation = visualTransformation,
                            innerTextField = innerTextField,
                            placeholder = placeholderText,
                            label = labelText,
                            leadingIcon = leadingImage,
                            trailingIcon = trailingImage,
                            singleLine = singleLine,
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            colors = colors,
                            contentPadding = contentPadding,
                            container = {
                                OutlinedTextFieldDefaults.Container(
                                    enabled = enabled,
                                    isError = isError,
                                    interactionSource = interactionSource,
                                    colors = colors,
                                    shape = shape,
                                    focusedBorderThickness = focusedBorderThickness,
                                    unfocusedBorderThickness = unfocusedBorderThickness,
                                )
                            }
                        )
                    }
                )
            }

            // Check if we need clickable layer
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(fieldBoxSize.height.toDp())
                        .noRippleClickable(onClick = onClick)
                )
            }
        }

        // Render error if required
        AnimatedVisibility(isError) {
            Text(
                text = error.orEmpty(),
                style = errorTextStyle,
                color = errorTextColor,
                maxLines = placeholderMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}