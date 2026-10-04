package com.elcobre.lavanderiaelcobre.ui.screens.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/** Configuración del operario: cuenta, apariencia (picker Claro/Oscuro/Sistema) y cierre de sesión. */
@Composable
fun ConfiguracionScreen(
    operario: Operario,
    modoTema: ModoTema,
    onCambiarTema: (ModoTema) -> Unit,
    onCerrarSesion: () -> Unit,
    applySystemBarsInsets: Boolean = true,
) {
    var confirmandoSalida by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    CobreBackground(applySystemBarsInsets = applySystemBarsInsets) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text("Configuración", style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
            Spacer(Modifier.height(2.dp))
            Text(
                "Tu cuenta, apariencia y sesión.",
                style = MaterialTheme.typography.bodyMedium,
                color = brandMutedColor(),
            )
            Spacer(Modifier.height(20.dp))

            CuentaCard(operario)
            Spacer(Modifier.height(16.dp))
            AparienciaCard(modoTema = modoTema, onCambiarTema = onCambiarTema)
            Spacer(Modifier.height(16.dp))
            CerrarSesionBoton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    confirmandoSalida = true
                },
            )
        }
    }

    if (confirmandoSalida) {
        AlertDialog(
            onDismissRequest = { confirmandoSalida = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Deberás volver a ingresar tus datos para acceder de nuevo.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmandoSalida = false
                    onCerrarSesion()
                }) {
                    val accent = if (cobreIsDark()) StatusRedDark else StatusRed
                    Text("Cerrar sesión", color = accent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmandoSalida = false }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun CuentaCard(operario: Operario) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 10.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(BrandCtaGradient, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    operario.nombre.trim().take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(operario.nombre, style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
                Text("${operario.rol} · ${operario.turno}", style = MaterialTheme.typography.bodySmall, color = brandMutedColor())
            }
        }
    }
}

@Composable
private fun AparienciaCard(modoTema: ModoTema, onCambiarTema: (ModoTema) -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 8.dp) {
        Column {
            Text("Apariencia", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(4.dp))
            Text(
                "Elige cómo se ve la app en este dispositivo.",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
            Spacer(Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ModoTema.entries.forEach { opcion ->
                    ModoTemaOpcion(
                        modo = opcion,
                        activo = modoTema == opcion,
                        onClick = { onCambiarTema(opcion) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ModoTemaOpcion(modo: ModoTema, activo: Boolean, onClick: () -> Unit) {
    val isDark = cobreIsDark()
    val interaction = remember { MutableInteractionSource() }
    val borde = if (activo) Brand500 else (if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f))
    val fondo = if (activo) Brand500.copy(alpha = 0.10f) else Color.Transparent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressableScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(fondo)
            .border(1.dp, borde, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(if (activo) Brand500 else brandMutedColor().copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (activo) {
                Icon(CobreIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                modo.etiqueta,
                style = MaterialTheme.typography.labelLarge,
                color = if (activo) Brand500 else brandHeadingColor(),
                fontWeight = FontWeight.Bold,
            )
            Text(modo.descripcion, style = MaterialTheme.typography.bodySmall, color = brandMutedColor())
        }
    }
}

@Composable
private fun CerrarSesionBoton(onClick: () -> Unit) {
    val isDark = cobreIsDark()
    val accent = if (isDark) StatusRedDark else StatusRed
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressableScale(interaction)
            .background(accent.copy(alpha = if (isDark) 0.16f else 0.10f), RoundedCornerShape(16.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(CobreIcons.Logout, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge, color = accent, fontWeight = FontWeight.SemiBold)
    }
}
