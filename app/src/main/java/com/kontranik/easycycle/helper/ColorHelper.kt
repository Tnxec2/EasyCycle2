package com.kontranik.easycycle.helper

import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt


/**
 * Bestimmt, ob auf einer gegebenen Farbe heller oder dunkler Text
 * für eine bessere Lesbarkeit verwendet werden sollte. * @param color Die Hintergrundfarbe als Hex-String (z. B. "#ffdec2").
 * @return `Color.Black` für helle Hintergründe und `Color.White` für dunkle Hintergründe.
 */
@Composable
fun getTextColorForBackground(color: String?, defaultColor: Color = MaterialTheme.colors.onSurface): Color {
    if (color == null) {
        return MaterialTheme.colors.onSurface // Standardfarbe, falls kein Hintergrund vorhanden
    }

    val parsedColor = color.toColorInt()

    val contentColor = MaterialTheme.colors.contentColorFor(
        Color(parsedColor)
    )

    if (contentColor != Color.Unspecified) return contentColor

    try {
        // Berechnet die wahrgenommene Helligkeit der Farbe (Luminanz)
        val luminance = (0.299 * android.graphics.Color.red(parsedColor) +
                0.587 * android.graphics.Color.green(parsedColor) +
                0.114 * android.graphics.Color.blue(parsedColor)) / 255

        // Wenn die Luminanz > 0.5 ist, ist der Hintergrund hell -> dunkler Text
        // sonst ist der Hintergrund dunkel -> heller Text
        return if (luminance > 0.5) Color.Black else Color.White
    } catch (e: IllegalArgumentException) {
        // Fallback, falls der Farbstring ungültig ist
        return Color.Black
    }
}
