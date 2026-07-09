package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.inter_bold
import lavanderiaelcobre.shared.generated.resources.inter_medium
import lavanderiaelcobre.shared.generated.resources.inter_regular
import lavanderiaelcobre.shared.generated.resources.inter_semibold
import lavanderiaelcobre.shared.generated.resources.outfit_bold
import lavanderiaelcobre.shared.generated.resources.outfit_extrabold
import lavanderiaelcobre.shared.generated.resources.outfit_medium
import lavanderiaelcobre.shared.generated.resources.outfit_semibold
import org.jetbrains.compose.resources.Font

// Tipografía de marca: Inter para el cuerpo, Outfit para títulos, con el mismo
// tracking negativo en headings que el CSS de la web (`letter-spacing: -0.02em`).
@Composable
private fun interFamily() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)

@Composable
private fun outfitFamily() = FontFamily(
    Font(Res.font.outfit_medium, FontWeight.Medium),
    Font(Res.font.outfit_semibold, FontWeight.SemiBold),
    Font(Res.font.outfit_bold, FontWeight.Bold),
    Font(Res.font.outfit_extrabold, FontWeight.ExtraBold),
)

private fun estilo(familia: FontFamily, peso: FontWeight, tamano: Float, alto: Float, tracking: Float) =
    TextStyle(fontFamily = familia, fontWeight = peso, fontSize = tamano.sp, lineHeight = alto.sp, letterSpacing = tracking.sp)

@Composable
fun rememberCobreTypography(): Typography {
    val display = outfitFamily()
    val body = interFamily()

    return Typography(
        displayLarge = estilo(display, FontWeight.SemiBold, 40f, 46f, -0.8f),
        displayMedium = estilo(display, FontWeight.SemiBold, 32f, 38f, -0.64f),
        displaySmall = estilo(display, FontWeight.SemiBold, 24f, 32f, -0.48f),
        headlineLarge = estilo(display, FontWeight.SemiBold, 28f, 34f, -0.56f),
        headlineMedium = estilo(display, FontWeight.SemiBold, 24f, 30f, -0.48f),
        titleLarge = estilo(display, FontWeight.SemiBold, 20f, 26f, -0.4f),
        titleMedium = estilo(display, FontWeight.SemiBold, 17f, 24f, -0.34f),
        titleSmall = estilo(display, FontWeight.SemiBold, 15f, 20f, -0.3f),
        bodyLarge = estilo(body, FontWeight.Normal, 16f, 24f, 0f),
        bodyMedium = estilo(body, FontWeight.Normal, 14f, 20f, 0f),
        bodySmall = estilo(body, FontWeight.Normal, 12f, 16f, 0f),
        labelLarge = estilo(body, FontWeight.SemiBold, 14f, 18f, 0.1f),
        labelMedium = estilo(body, FontWeight.SemiBold, 12f, 16f, 0.1f),
        labelSmall = estilo(body, FontWeight.Medium, 11f, 14f, 0.1f),
    )
}
