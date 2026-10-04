package com.elcobre.lavanderiaelcobre.ui.screens.insumos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.elcobre.lavanderiaelcobre.data.model.AlertaStock
import com.elcobre.lavanderiaelcobre.data.model.Insumo
import com.elcobre.lavanderiaelcobre.data.model.SeveridadStock
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.SelectableChip
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.components.severidadColors
import com.elcobre.lavanderiaelcobre.ui.theme.Brand200
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurface
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.TextSecondaryLight
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/**
 * Reporte de alertas de stock por el operario (RF-AN05), independiente de un pedido; en
 * memoria (mock). Es un destino de tab (como Avisos/Configuración): sin botón de volver,
 * porque no hay una pantalla "anterior" real a la que regresar desde un tab.
 */
@Composable
fun InsumosScreen(
    insumos: List<Insumo>,
    alertas: List<AlertaStock>,
    onCrear: (Insumo, SeveridadStock, String) -> Unit,
    applySystemBarsInsets: Boolean = true,
) {
    var insumoSel by remember { mutableStateOf<Insumo?>(null) }
    var severidad by remember { mutableStateOf(SeveridadStock.BAJO) }
    var nota by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    // La confirmación de envío se auto-descarta tras unos segundos.
    LaunchedEffect(enviado) {
        if (enviado) {
            kotlinx.coroutines.delay(2600)
            enviado = false
        }
    }

    CobreBackground(applySystemBarsInsets = applySystemBarsInsets) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column {
                Text("Reportar insumo", style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
                Text(
                    "Avisa a administración cuando algo escasea",
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandMutedColor(),
                )
            }

            AnimatedVisibility(
                visible = enviado,
                enter = fadeIn(tween(250)),
                exit = fadeOut(tween(250)),
            ) {
                val verde = if (cobreIsDark()) StatusGreenDark else StatusGreen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(verde.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(CobreIcons.CheckCircle, contentDescription = null, tint = verde, modifier = Modifier.size(20.dp))
                    Text(
                        "Alerta enviada a administración.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = verde,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            FormularioReporte(
                insumos = insumos,
                insumoSel = insumoSel,
                onInsumoSel = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    insumoSel = it
                },
                severidad = severidad,
                onSeveridad = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    severidad = it
                },
                nota = nota,
                onNota = { nota = it },
                onEnviar = {
                    val sel = insumoSel ?: return@FormularioReporte
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCrear(sel, severidad, nota)
                    insumoSel = null
                    nota = ""
                    severidad = SeveridadStock.BAJO
                    enviado = true
                },
            )

            ReportesRecientes(alertas = alertas)

            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FormularioReporte(
    insumos: List<Insumo>,
    insumoSel: Insumo?,
    onInsumoSel: (Insumo) -> Unit,
    severidad: SeveridadStock,
    onSeveridad: (SeveridadStock) -> Unit,
    nota: String,
    onNota: (String) -> Unit,
    onEnviar: () -> Unit,
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 12.dp) {
        Column {
            Text("Insumo", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                insumos.forEach { insumo ->
                    SelectableChip(
                        label = insumo.displayName,
                        seleccionado = insumo == insumoSel,
                        onClick = { onInsumoSel(insumo) },
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Text("Nivel", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SeveridadStock.entries.forEach { opcion ->
                    SeveridadSelector(opcion, severidad, onSeveridad, Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = nota,
                onValueChange = onNota,
                label = { Text("Nota (opcional)") },
                placeholder = { Text("Ej. Queda medio bidón para el turno tarde") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(14.dp),
                colors = cobreFieldColors(),
            )

            Spacer(Modifier.height(18.dp))
            val habilitado = insumoSel != null
            val interaction = remember { MutableInteractionSource() }
            Button(
                onClick = onEnviar,
                enabled = habilitado,
                interactionSource = interaction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .pressableScale(interaction)
                    .then(
                        if (habilitado) Modifier.background(BrandCtaGradient, RoundedCornerShape(16.dp))
                        else Modifier.background(Brand500.copy(alpha = 0.30f), RoundedCornerShape(16.dp)),
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
            ) {
                Icon(CobreIcons.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Reportar a administración",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SeveridadSelector(
    opcion: SeveridadStock,
    seleccionada: SeveridadStock,
    onSeleccion: (SeveridadStock) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = severidadColors(opcion)
    val activa = opcion == seleccionada
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .pressableScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(if (activa) colores.container else Color.Transparent)
            .border(
                1.dp,
                if (activa) colores.content.copy(alpha = 0.5f) else colores.content.copy(alpha = 0.22f),
                RoundedCornerShape(14.dp),
            )
            .clickable(interactionSource = interaction, indication = null) { onSeleccion(opcion) }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (opcion == SeveridadStock.AGOTADO) CobreIcons.Warning else CobreIcons.Inventory,
            contentDescription = null,
            tint = colores.content,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            opcion.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = colores.content,
            fontWeight = if (activa) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ReportesRecientes(alertas: List<AlertaStock>) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 8.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(CobreIcons.Inventory, contentDescription = null, tint = brandHeadingColor(), modifier = Modifier.size(22.dp))
                Text("Reportes recientes", style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
            }
            Spacer(Modifier.height(16.dp))
            if (alertas.isEmpty()) {
                Text(
                    "Aún no has reportado insumos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandMutedColor().copy(alpha = 0.85f),
                )
            }
            alertas.forEachIndexed { index, alerta ->
                ReporteFila(alerta)
                if (index < alertas.size - 1) Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun ReporteFila(alerta: AlertaStock) {
    val colores = severidadColors(alerta.severidad)
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colores.container),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CobreIcons.Inventory, contentDescription = null, tint = colores.content, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    alerta.insumo.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = brandHeadingColor(),
                    modifier = Modifier.weight(1f),
                )
                Text(alerta.hora, style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
            }
            Spacer(Modifier.height(6.dp))
            StatusChip(alerta.severidad.displayName, colores.container, colores.content)
            if (alerta.nota.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    alerta.nota,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                )
            }
            Text("· ${alerta.autor}", style = MaterialTheme.typography.labelSmall, color = brandMutedColor().copy(alpha = 0.85f))
        }
    }
}
