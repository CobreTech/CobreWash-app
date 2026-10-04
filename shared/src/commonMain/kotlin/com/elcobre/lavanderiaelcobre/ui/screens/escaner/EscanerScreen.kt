package com.elcobre.lavanderiaelcobre.ui.screens.escaner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurface
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/**
 * Escaneo de la comanda por su código QR (RF-SP02 / RF-SP09). El prototipo no
 * accede a la cámara: ofrece un escáner simulado (animación + botón que abre la
 * siguiente comanda pendiente) y, como respaldo, la entrada manual del código.
 */
@Composable
fun EscanerScreen(
    onVolver: () -> Unit,
    escaneoSimulado: () -> Pedido?,
    buscarComanda: (String) -> Pedido?,
    onAbrir: (Pedido) -> Unit,
) {
    var codigo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val haptic = LocalHapticFeedback.current

    CobreBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            EncabezadoSimple(
                titulo = "Escanear comanda",
                subtitulo = "Apunta al código QR del pedido",
                onVolver = onVolver,
            )

            VisorEscaner()

            Button(
                onClick = {
                    val pedido = escaneoSimulado()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (pedido != null) {
                        onAbrir(pedido)
                    } else {
                        error = "No hay comandas pendientes para escanear."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(BrandCtaGradient, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            ) {
                Icon(CobreIcons.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Simular escaneo",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }

            EntradaManual(
                codigo = codigo,
                onCodigoChange = { codigo = it; error = null },
                onAbrir = {
                    val pedido = buscarComanda(codigo)
                    if (pedido != null) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        codigo = ""
                        onAbrir(pedido)
                    } else {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        error = "No se encontró la comanda \"${codigo.trim()}\"."
                    }
                },
            )

            AnimatedVisibility(visible = error != null) {
                val rojo = if (cobreIsDark()) StatusRedDark else StatusRed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rojo.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = rojo, modifier = Modifier.size(18.dp))
                    Text(error.orEmpty(), style = MaterialTheme.typography.bodySmall, color = rojo)
                }
            }
        }
    }
}

/** Marco del "visor" con esquinas de mira y una línea de escaneo que sube y baja. */
@Composable
private fun VisorEscaner() {
    val isDark = cobreIsDark()
    val transicion = rememberInfiniteTransition(label = "escaner")
    val progreso by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600), repeatMode = RepeatMode.Reverse),
        label = "linea",
    )
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(24.dp),
        elevation = 14.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color.Black.copy(alpha = 0.35f) else Brand500.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    CobreIcons.QrCodeScanner,
                    contentDescription = null,
                    tint = (if (isDark) Brand400 else Brand500).copy(alpha = 0.28f),
                    modifier = Modifier.size(120.dp),
                )
                // Línea de escaneo animada.
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(3.dp)
                        .graphicsLayer { translationY = (progreso - 0.5f) * 360f }
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, if (isDark) Brand400 else Copper600, Color.Transparent),
                            ),
                        ),
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "Buscando código de comanda…",
                style = MaterialTheme.typography.bodyMedium,
                color = brandMutedColor(),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EntradaManual(
    codigo: String,
    onCodigoChange: (String) -> Unit,
    onAbrir: () -> Unit,
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 10.dp) {
        Column {
            Text("¿No escanea?", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(2.dp))
            Text(
                "Ingresa el código de comanda manualmente.",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = codigo,
                onValueChange = onCodigoChange,
                label = { Text("Código de comanda") },
                placeholder = { Text("Ej. CMD-1042") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(CobreIcons.Inventory, contentDescription = null, tint = Brand700, modifier = Modifier.size(20.dp)) },
                keyboardOptions = KeyboardOptions.Default,
                colors = cobreFieldColors(),
            )
            Spacer(Modifier.height(12.dp))
            val interaction = remember { MutableInteractionSource() }
            val isDark = cobreIsDark()
            val accent = if (isDark) Brand400 else Copper700
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressableScale(interaction)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = if (isDark) 0.18f else 0.12f))
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        enabled = codigo.isNotBlank(),
                    ) { onAbrir() }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(CobreIcons.ArrowForward, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Abrir comanda", style = MaterialTheme.typography.labelLarge, color = accent, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Encabezado con botón de volver, reutilizado por las pantallas secundarias. */
@Composable
internal fun EncabezadoSimple(
    titulo: String,
    subtitulo: String,
    onVolver: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val interaction = remember { MutableInteractionSource() }
        val isDark = cobreIsDark()
        Box(
            modifier = Modifier
                .pressableScale(interaction)
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) DarkSurface2 else Color.White.copy(alpha = 0.6f))
                .clickable(interactionSource = interaction, indication = null) { onVolver() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                CobreIcons.ArrowBack,
                contentDescription = "Volver",
                tint = if (isDark) DarkOnSurface else Copper700,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
            Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = brandMutedColor())
        }
    }
}
