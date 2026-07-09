package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ColorScheme de Material 3 construido sobre la paleta de marca, sincronizado con Tailwind CSS de la web.
private val CobreLightColors = lightColorScheme(
    primary = Brand500,
    onPrimary = Color.White,
    primaryContainer = Brand100,
    onPrimaryContainer = Brand900,

    secondary = Copper500,
    onSecondary = Color.White,
    secondaryContainer = Copper100,
    onSecondaryContainer = Copper900,

    tertiary = Copper600,
    onTertiary = Color.White,
    tertiaryContainer = Copper200,
    onTertiaryContainer = Copper900,

    background = BackgroundWarm, // #fdfcfb
    onBackground = ForegroundStone, // #1c1917

    surface = Color.White,
    onSurface = ForegroundStone,
    surfaceVariant = Color(0xFFF9F7F5), // Un tono crema muy suave
    onSurfaceVariant = Brand800,

    outline = Copper200,
    outlineVariant = Brand100,

    error = Copper600,
    onError = Color.White,
    errorContainer = Brand100,
    onErrorContainer = Copper900,
)

private val CobreDarkColors = darkColorScheme(
    // Cobre solo como acento — sobre superficies grises neutras.
    primary = Brand400, // #FB923C
    onPrimary = Color(0xFF1A1206),
    primaryContainer = Brand900,
    onPrimaryContainer = Brand100,

    secondary = Copper300,
    onSecondary = Color(0xFF1A1206),
    secondaryContainer = Copper900,
    onSecondaryContainer = Copper100,

    tertiary = Copper300,
    onTertiary = Neutral900,
    tertiaryContainer = Copper800,
    onTertiaryContainer = Copper100,

    // Gris carbón neutro, sin tinte.
    background = DarkBg,          // #121215
    onBackground = DarkOnSurface, // #ECECEF

    surface = DarkSurface1,       // #1C1C21 (elevación 1)
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurface2, // #26262C (elevación 2)
    onSurfaceVariant = DarkOnSurfaceMuted, // #A0A0A8

    outline = DarkOutline,        // #34343C
    outlineVariant = Color(0xFF474751),

    error = Color(0xFFF87171),
    onError = Color(0xFF1A0A0A),
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = Color(0xFFFFD9D6),
)

/** Preferencia de apariencia elegida en Configuración; espeja el picker Claro/Oscuro/Sistema de la web. */
enum class ModoTema(val etiqueta: String, val descripcion: String) {
    CLARO("Claro", "Fondo claro en toda la app"),
    OSCURO("Oscuro", "Fondo oscuro en toda la app"),
    SISTEMA("Sistema", "Sigue la preferencia de tu dispositivo"),
}

// Tema oscuro *resuelto* (tras aplicar la preferencia de Configuración), no el del
// sistema operativo. El resto de la UI debe leer [cobreIsDark], no `isSystemInDarkTheme()`
// directamente, o el picker Claro/Oscuro/Sistema queda sin efecto fuera de MaterialTheme.
private val LocalCobreDarkTheme = compositionLocalOf { false }

/** Punto único de verdad para "¿está en oscuro?" dentro de la app. */
@Composable
fun cobreIsDark(): Boolean = LocalCobreDarkTheme.current

@Composable
fun CobreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalCobreDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) CobreDarkColors else CobreLightColors,
            typography = rememberCobreTypography(),
            content = content,
        )
    }
}
