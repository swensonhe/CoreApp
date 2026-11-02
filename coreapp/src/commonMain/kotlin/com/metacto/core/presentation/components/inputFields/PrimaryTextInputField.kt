package com.metacto.core.presentation.components.inputFields

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.metacto.core.presentation.theme.CoreTheme.colors
import com.metacto.core.presentation.theme.CoreTheme.typography
import com.metacto.core.presentation.theme.CoreTheme.spacings
import com.metacto.core.presentation.theme.CoreTheme.shapes
import kotlin.time.Duration

@Composable
fun PrimaryTextInputField(
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    minHeight: Dp = spacings.primaryTextInputField.minHeight,
    error: String? = null,
    textStyle: TextStyle = typography.primaryTextInputField.textStyle,
    endIconVector: ImageVector? = null,
    endIconPainter: Painter? = null,
    endIconSize: Dp = spacings.primaryTextInputField.endIconSize,
    onEndIconClick: (() -> Unit)? = null,
    startIconVector: ImageVector? = null,
    startIconPainter: Painter? = null,
    startIconSize: Dp = spacings.primaryTextInputField.startIconSize,
    onStartIconClick: (() -> Unit)? = null,
    iconTintColor: Color? = colors.primaryTextInputField.iconColor,
    placeholder: String? = null,
    placeholderMaxLines: Int = 1,
    shape: RoundedCornerShape = shapes.primaryTextInputField.shape,
    textAlign: TextAlign? = null,
    allowDigitsOnly: Boolean = false,
    isStaticLabel: Boolean = false,
    requestFocus: Boolean = false,
    requestFocusDelay: Duration = DEFAULT_REQUEST_FOCUS_DELAY,
    backgroundColor: Color = colors.primaryTextInputField.bgColor,
    focusedBorderColor: Color = colors.primaryTextInputField.focusedBorderColor,
    unFocusedBorderColor: Color = colors.primaryTextInputField.unFocusedBorderColor,
    textColor: Color = colors.primaryTextInputField.textColor,
    placeholderTextStyle: TextStyle = typography.primaryTextInputField.placeholderTextStyle,
    placeholderTextColor: Color = colors.primaryTextInputField.placeholderColor,
    labelTextStyle: TextStyle = typography.primaryTextInputField.labelTextStyle,
    labelTextColor: Color = colors.primaryTextInputField.labelColor,
    errorTextStyle: TextStyle = typography.primaryTextInputField.errorTextStyle,
    errorTextColor: Color = colors.primaryTextInputField.errorColor,
    elevation: Dp = spacings.primaryTextInputField.elevation,
    shadowColor: Color = colors.primaryTextInputField.shadowColor,
    floatingLabelSpacing: Dp = spacings.primaryTextInputField.floatingLabelSpacing,
    contentPadding: PaddingValues = spacings.primaryTextInputField.contentPadding,
    focusedBorderThickness: Dp = spacings.primaryTextInputField.focusedBorderThickness,
    unfocusedBorderThickness: Dp = spacings.primaryTextInputField.unfocusedBorderThickness,
    minWidth: Dp = spacings.primaryTextInputField.minWidth,
    platformImeOptions: PlatformImeOptions? = null,
) {
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
        shadowColor = shadowColor,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        capitalization = capitalization,
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        maxLength = maxLength,
        maxLines = maxLines,
        minHeight = minHeight,
        error = error,
        endIconVector = endIconVector,
        endIconPainter = endIconPainter,
        endIconSize = endIconSize,
        onEndIconClick = onEndIconClick,
        startIconVector = startIconVector,
        startIconPainter = startIconPainter,
        elevation = elevation,
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
        labelTextStyle = labelTextStyle,
        errorTextStyle = errorTextStyle,
        placeholderTextColor = placeholderTextColor,
        labelTextColor = labelTextColor,
        errorTextColor = errorTextColor,
        floatingLabelSpacing = floatingLabelSpacing,
        contentPadding = contentPadding,
        focusedBorderThickness = focusedBorderThickness,
        unfocusedBorderThickness = unfocusedBorderThickness,
        minLines = minLines,
        minWidth = minWidth
    )
}