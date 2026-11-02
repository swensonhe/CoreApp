package com.metacto.core.presentation.components.inputFields

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.metacto.core.presentation.theme.CoreTheme
import com.metacto.core.presentation.theme.CoreTheme.colors
import com.metacto.core.presentation.theme.CoreTheme.typography
import com.metacto.core.presentation.theme.CoreTheme.spacings
import com.metacto.core.presentation.theme.CoreTheme.shapes
import kotlin.time.Duration

@Composable
fun PasswordTextInputField(
    modifier: Modifier = Modifier,
    text: String,
    label: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    minHeight: Dp = spacings.passwordTextInputField.minHeight,
    error: String? = null,
    endIconSize: Dp = spacings.passwordTextInputField.endIconSize,
    startIconVector: ImageVector? = null,
    startIconPainter: Painter? = null,
    startIconSize: Dp = spacings.passwordTextInputField.startIconSize,
    onStartIconClick: (() -> Unit)? = null,
    iconTintColor: Color? = colors.passwordTextInputField.iconColor,
    placeholder: String? = null,
    placeholderMaxLines: Int = 1,
    shape: RoundedCornerShape = shapes.passwordTextInputField.shape,
    textAlign: TextAlign? = null,
    allowDigitsOnly: Boolean = false,
    isStaticLabel: Boolean = false,
    requestFocus: Boolean = false,
    requestFocusDelay: Duration = DEFAULT_REQUEST_FOCUS_DELAY,
    showPasswordVisibilityToggle: Boolean = false,
    textStyle: TextStyle = typography.passwordTextInputField.textStyle,
    textColor: Color = colors.passwordTextInputField.textColor,
    placeholderTextStyle: TextStyle = typography.passwordTextInputField.placeholderTextStyle,
    placeholderTextColor: Color = colors.passwordTextInputField.placeholderColor,
    labelTextStyle: TextStyle = typography.passwordTextInputField.labelTextStyle,
    labelTextColor: Color = colors.passwordTextInputField.labelColor,
    errorTextStyle: TextStyle = typography.passwordTextInputField.errorTextStyle,
    errorTextColor: Color = colors.passwordTextInputField.errorColor,
    focusedBorderColor: Color = colors.passwordTextInputField.focusedBorderColor,
    unFocusedBorderColor: Color = colors.passwordTextInputField.unFocusedBorderColor,
    backgroundColor: Color = colors.passwordTextInputField.bgColor,
    elevation: Dp = spacings.passwordTextInputField.elevation,
    shadowColor: Color = colors.passwordTextInputField.shadowColor,
    floatingLabelSpacing: Dp = spacings.passwordTextInputField.floatingLabelSpacing,
    contentPadding: PaddingValues = spacings.passwordTextInputField.contentPadding,
    focusedBorderThickness: Dp = spacings.passwordTextInputField.focusedBorderThickness,
    unfocusedBorderThickness: Dp = spacings.passwordTextInputField.unfocusedBorderThickness,
    minWidth: Dp = spacings.passwordTextInputField.minWidth,
    platformImeOptions: PlatformImeOptions? = null,
) {
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    val visualTransformation = remember(isPasswordVisible, text) {
        /**
         * Compose multiplatform input field has a bug that when a custom visual transformation
         * is used then copy & paste menu doesn't appear in iOS.
         * So as a workaround to fix the issue, we set the visual transformation to None when
         * the text is empty to show the copy & paste menu
         */
        if (isPasswordVisible || text.isEmpty()) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    }

    val endIcon = if (isPasswordVisible) {
        Icons.Default.Visibility
    } else {
        Icons.Default.VisibilityOff
    }

    BaseTextInputField(
        platformImeOptions = platformImeOptions,
        requestFocus = requestFocus,
        requestFocusDelay = requestFocusDelay,
        text = text,
        label = label,
        onValueChange = onValueChange,
        onClick = onClick,
        modifier = modifier,
        keyboardType = keyboardType,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        capitalization = capitalization,
        elevation = elevation,
        singleLine = singleLine,
        enabled = enabled,
        shadowColor = shadowColor,
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        maxLength = maxLength,
        maxLines = maxLines,
        minHeight = minHeight,
        error = error,
        endIconVector = if (showPasswordVisibilityToggle) endIcon else null,
        endIconPainter = null,
        endIconSize = endIconSize,
        startIconVector = startIconVector,
        startIconPainter = startIconPainter,
        startIconSize = startIconSize,
        onStartIconClick = onStartIconClick,
        iconTintColor = iconTintColor,
        placeholder = placeholder,
        placeholderMaxLines = placeholderMaxLines,
        shape = shape,
        allowDigitsOnly = allowDigitsOnly,
        isStaticLabel = isStaticLabel,
        backgroundColor = backgroundColor,
        focusedBorderColor = focusedBorderColor,
        unFocusedBorderColor = unFocusedBorderColor,
        textStyle = textStyle,
        textAlign = textAlign,
        textColor = textColor,
        placeholderTextStyle = placeholderTextStyle,
        placeholderTextColor = placeholderTextColor,
        labelTextStyle = labelTextStyle,
        labelTextColor = labelTextColor,
        errorTextStyle = errorTextStyle,
        errorTextColor = errorTextColor,
        floatingLabelSpacing = floatingLabelSpacing,
        contentPadding = contentPadding,
        focusedBorderThickness = focusedBorderThickness,
        unfocusedBorderThickness = unfocusedBorderThickness,
        minLines = minLines,
        minWidth = minWidth,
        onEndIconClick = {
            isPasswordVisible = isPasswordVisible.not()
        }
    )
}