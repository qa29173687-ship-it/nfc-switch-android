package tw.nfcswitch.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Green = Color(0xFF0C9A6A)
val GreenDeep = Color(0xFF087A54)
val GreenSoft = Color(0xFFE7F7F0)
val Blue = Color(0xFF1A73E8)
val BlueDeep = Color(0xFF1557B0)
val BlueSoft = Color(0xFFE8F0FE)
val Sheet = Color(0xFFF2F3F5)
val Ink = Color(0xFF1C1C1E)
val Muted = Color(0xFF6B6F76)
val Amber = Color(0xFF8A5A00)
val AmberBg = Color(0xFFFFF4D6)
val Line = Color(0xFFE7E8EC)

@Composable
fun NfcSwitchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Green,
            onPrimary = Color.White,
            background = Sheet,
            surface = Color.White,
            onBackground = Ink,
            onSurface = Ink,
            secondary = Blue,
        ),
        content = content
    )
}
