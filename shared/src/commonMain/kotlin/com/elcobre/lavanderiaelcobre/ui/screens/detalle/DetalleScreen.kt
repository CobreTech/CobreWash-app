package com.elcobre.lavanderiaelcobre.ui.screens.detalle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.EtapasTimeline
import com.elcobre.lavanderiaelcobre.ui.components.FlujoProduccionCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.HistorialCard
import com.elcobre.lavanderiaelcobre.ui.components.StageProgress
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.alertaColor
import com.elcobre.lavanderiaelcobre.ui.components.alertaIcon
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.etapaColors
import com.elcobre.lavanderiaelcobre.ui.components.etapaIcon
import com.elcobre.lavanderiaelcobre.ui.components.eventoColor
import com.elcobre.lavanderiaelcobre.ui.components.eventoIcon
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.components.prioridadColors
import com.elcobre.lavanderiaelcobre.ui.components.sharedTag
import com.elcobre.lavanderiaelcobre.ui.components.tipoIcon
import com.elcobre.lavanderiaelcobre.ui.theme.Brand100
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurface
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface1
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetalleScreen(
    pedido: Pedido,
    onVolver: () -> Unit,
    onAvanzar: (comentario: String) -> Unit,
    onComentar: (texto: String) -> Unit,
    onAlerta: (tipo: TipoAlerta, comentario: String) -> Unit,
    onResolver: () -> Unit,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    var comentario by remember { mutableStateOf("") }
    var alertaActiva by remember { mutableStateOf<TipoAlerta?>(null) }
    val haptic = LocalHapticFeedback.current

    // El banner de alerta se auto-descarta tras unos segundos.
    LaunchedEffect(alertaActiva) {
        if (alertaActiva != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            kotlinx.coroutines.delay(3800)
            alertaActiva = null
        }
    }

    // Protege avanzar/resolver/generar alerta de un doble-tap (frecuente con
    // guantes en planta): se deshabilita al primer tap y se reactiva recién
    // cuando el pedido recompuesto refleja el cambio real.
    var accionEnCurso by remember { mutableStateOf(false) }
    LaunchedEffect(pedido.etapaActual, pedido.estaBloqueado, pedido.historial.size) {
        accionEnCurso = false
    }

    CobreBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Encabezado(
                    pedido = pedido,
                    onVolver = onVolver,
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                )

                DetalleInfo(pedido = pedido)

                StepperCard(pedido = pedido)

                AccionesCard(
                    pedido = pedido,
                    comentario = comentario,
                    onComentarioChange = { comentario = it },
                    comentarHabilitado = comentario.isNotBlank(),
                    onComentar = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onComentar(comentario)
                        comentario = ""
                    },
                    alertaHabilitada = !pedido.estaBloqueado && !accionEnCurso,
                    onAlerta = { tipo ->
                        accionEnCurso = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onAlerta(tipo, comentario)
                        comentario = ""
                        alertaActiva = tipo
                    },
                )

                HistorialCard(pedido = pedido)

                // Espacio para que el último contenido no quede oculto tras la barra de acción fija.
                Spacer(Modifier.height(96.dp))
            }

            AlertaBanner(
                alerta = alertaActiva,
                onCerrar = { alertaActiva = null },
                modifier = Modifier.align(Alignment.TopCenter).padding(20.dp),
            )

            BottomActionBar(
                pedido = pedido,
                habilitado = !accionEnCurso,
                onAvanzar = {
                    accionEnCurso = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onAvanzar(comentario)
                    comentario = ""
                },
                onResolver = {
                    accionEnCurso = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onResolver()
                    comentario = ""
                    alertaActiva = null
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Encabezado(
    pedido: Pedido,
    onVolver: () -> Unit,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val interaction = remember { MutableInteractionSource() }
        val backDark = cobreIsDark()
        Box(
            modifier = Modifier
                .pressableScale(interaction)
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (backDark) DarkSurface2 else Color.White.copy(alpha = 0.6f))
                .clickable(interactionSource = interaction, indication = null) { onVolver() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                CobreIcons.ArrowBack,
                contentDescription = "Volver",
                tint = if (backDark) DarkOnSurface else Copper700,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        // Elemento compartido que "vuela" desde la tarjeta del Dashboard.
        val etapa = etapaColors(pedido.etapaActual)
        Box(
            modifier = Modifier
                .sharedTag("icono-${pedido.comanda}", sharedScope, animatedScope)
                .size(44.dp)
                .background(etapa.accent.copy(alpha = 0.16f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(etapaIcon(pedido.etapaActual), contentDescription = null, tint = etapa.accent, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(pedido.comanda, style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
            Text(pedido.cliente, style = MaterialTheme.typography.bodyMedium, color = brandMutedColor())
        }
    }
}

@Composable
private fun DetalleInfo(pedido: Pedido) {
    val etapa = etapaColors(pedido.etapaActual)
    val prioridad = prioridadColors(pedido.prioridad)
    val isDark = cobreIsDark()
    val tipoAccent = if (isDark) Brand400 else Copper700
    GlassCard(modifier = Modifier.fillMaxWidth(), tint = etapa.accent, elevation = 22.dp) {
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(pedido.etapaActual.corto, etapa.container, etapa.content, icon = etapaIcon(pedido.etapaActual))
                StatusChip("Prioridad ${pedido.prioridad.displayName}", prioridad.container, prioridad.content)
            }
            Spacer(Modifier.height(10.dp))
            StatusChip(
                pedido.tipo.displayName,
                tipoAccent.copy(alpha = if (isDark) 0.16f else 0.10f),
                tipoAccent,
                icon = tipoIcon(pedido.tipo),
            )
            Spacer(Modifier.height(16.dp))
            Text(pedido.tipoServicio, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                DatoColumna("Piezas", "${pedido.piezas}")
                DatoColumna("Plazo de entrega", pedido.plazoEntrega)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Etapa ${pedido.indiceEtapa + 1} de ${pedido.totalEtapas} · ${pedido.etapaActual.displayName}",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor(),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            StageProgress(
                flujo = pedido.flujo,
                etapaActual = pedido.etapaActual,
                accent = etapa.accent,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DatoColumna(titulo: String, valor: String) {
    Column {
        Text(titulo.uppercase(), style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
        Spacer(Modifier.height(2.dp))
        Text(valor, style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
    }
}

@Composable
private fun StepperCard(pedido: Pedido) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 10.dp) {
        Column {
            Text("Proceso productivo", style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
            Spacer(Modifier.height(16.dp))
            EtapasTimeline(flujo = pedido.flujo, indiceActual = pedido.indiceEtapa)
        }
    }
}

@Composable
private fun AccionesCard(
    pedido: Pedido,
    comentario: String,
    onComentarioChange: (String) -> Unit,
    comentarHabilitado: Boolean,
    onComentar: () -> Unit,
    alertaHabilitada: Boolean,
    onAlerta: (TipoAlerta) -> Unit,
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 10.dp) {
        Column {
            Text("Actualizar estado", style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
            Spacer(Modifier.height(12.dp))
            
            val alerta = pedido.alertaActiva
            if (alerta != null) {
                val color = alertaColor(alerta)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(CobreIcons.Warning, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Proceso detenido por: ${alerta.displayName}. Resuelve para continuar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = comentario,
                onValueChange = onComentarioChange,
                label = { Text("Comentario del avance (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions.Default,
                trailingIcon = {
                    IconButton(onClick = onComentar, enabled = comentarHabilitado) {
                        Icon(CobreIcons.Send, contentDescription = "Agregar comentario", tint = Brand700, modifier = Modifier.size(20.dp))
                    }
                },
                colors = cobreFieldColors(),
            )
            Spacer(Modifier.height(20.dp))
            Text("Generar alerta", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TipoAlerta.entries.forEach { tipo ->
                    BotonAlerta(
                        tipo = tipo,
                        onClick = { onAlerta(tipo) },
                        modifier = Modifier.weight(1f),
                        enabled = alertaHabilitada,
                    )
                }
            }
        }
    }
}

/** Barra fija al fondo con la acción principal: avanzar de etapa, o resolver si el pedido está bloqueado. */
@Composable
private fun BottomActionBar(
    pedido: Pedido,
    habilitado: Boolean,
    onAvanzar: () -> Unit,
    onResolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = cobreIsDark()
    val barColor = if (isDark) DarkSurface1 else Color.White
    val siguiente = pedido.siguienteEtapa
    val interaction = remember { MutableInteractionSource() }

    Column(modifier = modifier.fillMaxWidth().background(barColor)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(if (isDark) DarkOutline else Brand100),
        )
        Box(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            if (pedido.estaBloqueado) {
                Button(
                    onClick = onResolver,
                    enabled = habilitado,
                    interactionSource = interaction,
                    modifier = Modifier.fillMaxWidth().height(56.dp).pressableScale(interaction),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Copper600),
                ) {
                    Icon(CobreIcons.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Resolver Incidencia",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else {
                val avanzarBrush = if (siguiente != null) BrandCtaGradient
                    else androidx.compose.ui.graphics.SolidColor(Brand500.copy(alpha = 0.35f))
                Button(
                    onClick = onAvanzar,
                    enabled = siguiente != null && habilitado,
                    interactionSource = interaction,
                    modifier = Modifier.fillMaxWidth().height(56.dp).pressableScale(interaction)
                        .background(avanzarBrush, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
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
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (siguiente != null) "Avanzar a ${siguiente.corto}" else "Proceso completado",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonAlerta(tipo: TipoAlerta, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val color = if (enabled) alertaColor(tipo) else Color.Gray.copy(alpha = 0.5f)
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .pressableScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .then(if (enabled) Modifier.clickable(interactionSource = interaction, indication = null) { onClick() } else Modifier)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(alertaIcon(tipo), contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(6.dp))
        Text(
            tipo.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        )
    }
}



@Composable
private fun AlertaBanner(
    alerta: TipoAlerta?,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = alerta != null,
        enter = slideInVertically(tween(400)) { -it } + fadeIn(tween(400)),
        exit = slideOutVertically(tween(300)) { -it } + fadeOut(tween(300)),
        modifier = modifier,
    ) {
        val tipo = alerta ?: TipoAlerta.RETRASO
        val color = alertaColor(tipo)
        GlassCard(
            tint = color,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(alertaIcon(tipo), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.width(220.dp)) {
                    Text("Alerta generada: ${tipo.displayName}", style = MaterialTheme.typography.titleSmall, color = color)
                    Text(tipo.descripcionCorta, style = MaterialTheme.typography.bodySmall, color = brandMutedColor())
                }
                IconButton(onClick = onCerrar) {
                    Icon(CobreIcons.Close, contentDescription = "Cerrar", tint = color, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
