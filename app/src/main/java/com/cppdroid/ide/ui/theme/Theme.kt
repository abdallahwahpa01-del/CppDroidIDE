package com.cppdroid.ide.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// VS Code Dark Theme Colors
val Background = Color(0xFF1E1E1E)
val Surface = Color(0xFF252526)
val SurfaceVariant = Color(0xFF2D2D30)
val Primary = Color(0xFF007ACC)
val PrimaryVariant = Color(0xFF0062A3)
val Secondary = Color(0xFF569CD6)
val Accent = Color(0xFF4EC9B0)
val Error = Color(0xFFF44747)
val Warning = Color(0xFFCCA700)
val Success = Color(0xFF4CAF50)

// Syntax Highlighting Colors
val SyntaxKeyword = Color(0xFF569CD6)
val SyntaxString = Color(0xFFCE9178)
val SyntaxComment = Color(0xFF6A9955)
val SyntaxFunction = Color(0xFFDCDCAA)
val SyntaxNumber = Color(0xFFB5CEA8)
val SyntaxType = Color(0xFF4EC9B0)
val SyntaxOperator = Color(0xFFD4D4D4)
val TextPrimary = Color(0xFFD4D4D4)
val TextSecondary = Color(0xFF858585)
val LineNumber = Color(0xFF858585)
val LineHighlight = Color(0xFF2A2D2E)
val Selection = Color(0xFF264F78)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = Color.White,
    outline = Color(0xFF454545),
    outlineVariant = Color(0xFF3C3C3C)
)

@Composable
fun CppDroidIDETheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
