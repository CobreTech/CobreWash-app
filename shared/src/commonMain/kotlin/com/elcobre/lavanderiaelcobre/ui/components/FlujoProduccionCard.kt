package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand600
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface1
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.Stone200
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/**
 * Componente interactivo que reproduce `FlujoProduccion.tsx` de la versión web:
 * porcentaje global de avance, etapas segmentadas con iconos/checks y acciones directas.
 */
@Composable
fun FlujoProduccionCard(
    pedido: Pedido,
    onAvanzar: () -> Unit,
    onResolverAlerta: () -> Unit,
    onReportarIncidencia: () -> Unit,
    habilitado: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val isDark = cobreIsDark()
    val porcentaje = ((pedido.indiceEtapa + (if (pedido.esFinal) 1 else 0)).toFloat() / pedido.totalEtapas * 100).toInt()
        .coerceIn(0, 100)
    val cardBg = if (isDark) Color.Black.copy(alpha = 0.2f) else Color(0xFFFAFAF9).copy(alpha = 0.8f) // stone-50/80 y black/20
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.1f) else Stone200.copy(alpha = 0.8f) // white/10 y stone-200/80

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        // Cabecera con porcentaje global
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    "ESTADO GLOBAL DE PRODUCCIÓN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    ),
                    color = brandMutedColor(),
                )
                Text(
                    "Flujo de lavado y acondicionamiento",
                    style = MaterialTheme.typography.bodySmall,
                    color = brandMutedColor(),
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brand500.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    "$porcentaje%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = if (isDark) Brand400 else Brand500,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Bloque de alerta activa si el pedido está bloqueado
        if (pedido.estaBloqueado) {
            val rojo = if (isDark) StatusRedDark else StatusRed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(rojo.copy(alpha = 0.12f))
                    .border(1.dp, rojo.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(CobreIcons.Warning, contentDescription = null, tint = rojo, modifier = Modifier.size(22.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Incidencia activa: ${pedido.alertaActiva?.displayName}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = rojo,
                    )
                    Text(
                        pedido.alertaActiva?.descripcionCorta ?: "El avance está detenido hasta resolver.",
                        style = MaterialTheme.typography.bodySmall,
                        color = rojo,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Stepper horizontal (cadena de etapas, adaptativo)
        EtapasCadenaRow(pedido = pedido)

        Spacer(Modifier.height(20.dp))

        // Botones de acción principales (Avanzar / Resolver / Incidencia)
        FlujoAccionesRow(
            pedido = pedido,
            habilitado = habilitado,
            onAvanzar = onAvanzar,
            onResolverAlerta = onResolverAlerta,
            onReportarIncidencia = onReportarIncidencia,
        )
    }
}

private enum class EstadoChipEtapa { COMPLETADA, ACTIVA, PENDIENTE }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EtapasCadenaRow(pedido: Pedido) {
    val flujo = pedido.flujo
    val indiceActual = pedido.indiceEtapa

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        flujo.forEachIndexed { index, etapa ->
            val estado = when {
                index < indiceActual || (pedido.esFinal && index == indiceActual) -> EstadoChipEtapa.COMPLETADA
                index == indiceActual && !pedido.esFinal -> EstadoChipEtapa.ACTIVA
                else -> EstadoChipEtapa.PENDIENTE
            }
            EtapaCadenaChip(
                etapaNombre = etapa.corto,
                numero = index + 1,
                estado = estado,
            )
        }
    }
}

private data class ChipColorsHolder(val border: Color, val bg: Color, val textColor: Color, val subtitulo: String)

private fun resolverChipColors(estado: EstadoChipEtapa, isDark: Boolean, muted: Color): ChipColorsHolder {
    val green = if (isDark) StatusGreenDark else StatusGreen
    return when (estado) {
        EstadoChipEtapa.COMPLETADA -> ChipColorsHolder(green.copy(alpha = 0.20f), green.copy(alpha = 0.10f), green, "Completada")
        EstadoChipEtapa.ACTIVA -> ChipColorsHolder(
            Brand500.copy(alpha = 0.40f),
            Brand500.copy(alpha = 0.10f),
            if (isDark) Brand400 else Brand600,
            "En curso",
        )
        EstadoChipEtapa.PENDIENTE -> ChipColorsHolder(
            Color.Transparent,
            if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
            muted,
            "Pendiente",
        )
    }
}

@Composable
private fun EtapaCadenaChip(
    etapaNombre: String,
    numero: Int,
    estado: EstadoChipEtapa,
) {
    val isDark = cobreIsDark()
    val colors = resolverChipColors(estado, isDark, brandMutedColor())

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.bg)
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EtapaBadgeIcon(numero = numero, estado = estado, isDark = isDark)
            Column {
                Text(
                    etapaNombre,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textColor,
                )
                Text(
                    colors.subtitulo,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = brandMutedColor(),
                )
            }
        }
    }
}

@Composable
private fun EtapaBadgeIcon(
    numero: Int,
    estado: EstadoChipEtapa,
    isDark: Boolean,
) {
    val badgeBg = when (estado) {
        EstadoChipEtapa.COMPLETADA -> if (isDark) StatusGreenDark else StatusGreen
        EstadoChipEtapa.ACTIVA -> Brand500
        EstadoChipEtapa.PENDIENTE -> if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(badgeBg),
        contentAlignment = Alignment.Center,
    ) {
        if (estado == EstadoChipEtapa.COMPLETADA) {
            Icon(CobreIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        } else {
            Text(
                "$numero",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                color = if (estado == EstadoChipEtapa.ACTIVA) Color.White else brandMutedColor(),
            )
        }
    }
}

@Composable
private fun FlujoAccionesRow(
    pedido: Pedido,
    habilitado: Boolean,
    onAvanzar: () -> Unit,
    onResolverAlerta: () -> Unit,
    onReportarIncidencia: () -> Unit,
) {
    val isDark = cobreIsDark()
    val siguiente = pedido.siguienteEtapa
    val interactionAvanzar = remember { MutableInteractionSource() }
    val interactionIncidencia = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (pedido.estaBloqueado) {
            // Botón para resolver alerta
            Button(
                onClick = onResolverAlerta,
                enabled = habilitado,
                interactionSource = interactionAvanzar,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .pressableScale(interactionAvanzar),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Copper600),
            ) {
                Icon(CobreIcons.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Resolver Incidencia", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        } else {
            // Botón principal de avance de etapa
            val puedeAvanzar = siguiente != null && habilitado
            val btnBrush = if (puedeAvanzar) BrandCtaGradient else androidx.compose.ui.graphics.SolidColor(Brand500.copy(alpha = 0.35f))
            Button(
                onClick = onAvanzar,
                enabled = puedeAvanzar,
                interactionSource = interactionAvanzar,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .pressableScale(interactionAvanzar)
                    .background(btnBrush, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
            ) {
                Icon(
                    if (siguiente != null) CobreIcons.ArrowForward else CobreIcons.DoneAll,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (siguiente != null) "Avanzar a ${siguiente.corto}" else "Proceso Finalizado",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
        }

        // Botón secundario: Reportar incidencia (estilo web rojo sutil)
        if (!pedido.estaBloqueado && !pedido.esFinal) {
            val rojo = if (isDark) StatusRedDark else StatusRed
            Row(
                modifier = Modifier
                    .height(48.dp)
                    .pressableScale(interactionIncidencia)
                    .clip(RoundedCornerShape(14.dp))
                    .background(rojo.copy(alpha = if (isDark) 0.16f else 0.10f))
                    .border(1.dp, rojo.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .clickable(interactionSource = interactionIncidencia, indication = null, onClick = onReportarIncidencia)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(CobreIcons.Warning, contentDescription = null, tint = rojo, modifier = Modifier.size(16.dp))
                Text(
                    "Incidencia",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = rojo,
                )
            }
        }
    }
}
