package com.elcobre.lavanderiaelcobre.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.ChipColors
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTopBar
import com.elcobre.lavanderiaelcobre.ui.components.EtapasTimeline
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.SelectableChip
import com.elcobre.lavanderiaelcobre.ui.components.StageProgress
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.etapaColors
import com.elcobre.lavanderiaelcobre.ui.components.etapaIcon
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.components.prioridadColors
import com.elcobre.lavanderiaelcobre.ui.components.sharedTag
import com.elcobre.lavanderiaelcobre.ui.components.tipoIcon
import com.elcobre.lavanderiaelcobre.ui.theme.Brand200
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurface
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.TextSecondaryLight
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DashboardScreen(
    operario: Operario,
    pedidos: List<Pedido>,
    onAbrirPedido: (Pedido) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onCerrarSesion: () -> Unit,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    var filtro by remember { mutableStateOf<TipoComanda?>(null) }
    var busqueda by remember { mutableStateOf("") }
    var expandidoId by remember { mutableStateOf<String?>(null) }

    val visibles = remember(filtro, busqueda, pedidos.map { it.etapaActual }) {
        val consulta = busqueda.trim()
        pedidos.filter { p ->
            (filtro == null || p.tipo == filtro) &&
            (consulta.isEmpty() || p.comanda.contains(consulta, ignoreCase = true) || p.cliente.contains(consulta, ignoreCase = true))
        }
    }
    val haptic = LocalHapticFeedback.current

    CobreBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(operario = operario, onCerrarSesion = onCerrarSesion)

            MisTareasResumen(pedidos = pedidos, modifier = Modifier.padding(horizontal = 24.dp))

            Spacer(Modifier.height(16.dp))

            SearchBar(
                query = busqueda,
                onQueryChange = { busqueda = it },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            FiltroTipos(
                seleccion = filtro,
                onSeleccion = {
                    filtro = it
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp, top = 4.dp),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (visibles.isEmpty()) {
                    item {
                        EmptyState(
                            onLimpiar = {
                                filtro = null
                                busqueda = ""
                            },
                            modifier = Modifier.padding(top = 40.dp),
                        )
                    }
                } else {
                    item {
                        Text(
                            "Pedidos (${visibles.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = brandHeadingColor(),
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    items(visibles, key = { p -> p.comanda }) { pedido ->
                        val isDark = cobreIsDark()
                        PedidoCard(
                            pedido = pedido,
                            expandido = expandidoId == pedido.id,
                            onToggleExpand = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                expandidoId = if (expandidoId == pedido.id) null else pedido.id
                            },
                            onAvanzar = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAvanzarEtapa(pedido)
                            },
                            onVerDetalle = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAbrirPedido(pedido)
                            },
                            elevation = if (isDark) 4.dp else 18.dp,
                            modifier = Modifier.animateItem(),
                            sharedScope = sharedScope,
                            animatedScope = animatedScope,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Título "Mis Tareas" (h1 de la web en `seguimiento/page.tsx`) más el conteo de
 * comandas asignadas y alertas activas como subtítulo; sin panel de KPIs, eso
 * es del dashboard de administrador.
 */
@Composable
private fun MisTareasResumen(pedidos: List<Pedido>, modifier: Modifier = Modifier) {
    val pendientes = pedidos.count { !it.esFinal }
    val alertas = pedidos.sumOf { p -> p.historial.count { it.tipo == TipoEvento.ALERTA } }
    val isDark = cobreIsDark()

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Mis Tareas", style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                if (pendientes == 1) "Tienes 1 comanda asignada" else "Tienes $pendientes comandas asignadas",
                style = MaterialTheme.typography.bodyMedium,
                color = brandMutedColor(),
                modifier = Modifier.weight(1f),
            )
            if (alertas > 0) {
                val accent = if (isDark) StatusRedDark else StatusRed
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accent.copy(alpha = if (isDark) 0.18f else 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = if (alertas == 1) "1 alerta activa" else "$alertas alertas activas"
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(CobreIcons.Warning, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
                    Text(
                        "$alertas",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar comanda o cliente...", style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = { Icon(CobreIcons.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                androidx.compose.material3.IconButton(onClick = { onQueryChange("") }) {
                    Icon(CobreIcons.Close, contentDescription = "Limpiar búsqueda", modifier = Modifier.size(18.dp))
                }
            }
        } else {
            null
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = cobreFieldColors(),
    )
}

@Composable
private fun EmptyState(onLimpiar: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(Brand200.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                CobreIcons.Inventory,
                contentDescription = null,
                tint = Brand500.copy(alpha = 0.45f),
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            "No hay pedidos aquí",
            style = MaterialTheme.typography.titleMedium,
            color = brandHeadingColor()
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Ningún pedido coincide con el filtro o la búsqueda actual.",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor().copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        val interaction = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .pressableScale(interaction)
                .clip(RoundedCornerShape(50))
                .background(Brand500)
                .clickable(interactionSource = interaction, indication = null) { onLimpiar() }
                .padding(horizontal = 20.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(CobreIcons.Inventory, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Text(
                "Ver todos los pedidos",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun TopBar(operario: Operario, onCerrarSesion: () -> Unit) {
    CobreTopBar(
        nombre = operario.nombre,
        subtitulo = "${operario.rol} · ${operario.turno}",
        onCerrarSesion = onCerrarSesion,
    ) {
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
    }
}

@Composable
private fun FiltroTipos(
    seleccion: TipoComanda?,
    onSeleccion: (TipoComanda?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(CobreIcons.FilterList, contentDescription = null, tint = Brand700, modifier = Modifier.size(20.dp))
        LazyRowChips(seleccion = seleccion, onSeleccion = onSeleccion)
    }
}

@Composable
private fun LazyRowChips(seleccion: TipoComanda?, onSeleccion: (TipoComanda?) -> Unit) {
    androidx.compose.foundation.lazy.LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            SelectableChip(label = "Todas", seleccionado = seleccion == null, onClick = { onSeleccion(null) })
        }
        items(TipoComanda.entries) { tipo ->
            SelectableChip(
                label = tipo.displayName,
                seleccionado = seleccion == tipo,
                onClick = { onSeleccion(tipo) },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PedidoCard(
    pedido: Pedido,
    expandido: Boolean,
    onToggleExpand: () -> Unit,
    onAvanzar: () -> Unit,
    onVerDetalle: () -> Unit,
    elevation: androidx.compose.ui.unit.Dp = 18.dp,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    // Sin animación de entrada propia: la pantalla ya trae su transición de
    // AnimatedContent (CobreApp.kt) y sumarle un stagger por tarjeta competía
    // con esa animación cada vez que se volvía a esta pestaña, sintiéndose
    // forzado en vez de fluido.
    val interaction = remember { MutableInteractionSource() }
    val etapa = etapaColors(pedido.etapaActual)
    val prioridad = prioridadColors(pedido.prioridad)
    val chevronRotacion by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (expandido) 90f else 0f,
        animationSpec = tween(300),
        label = "chevron",
    )
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .pressableScale(interaction)
            .clickable(interactionSource = interaction, indication = null) { onToggleExpand() },
        tint = etapa.accent,
        elevation = elevation,
        stripe = prioridad.accent,
    ) {
        Column {
            PedidoCardHeader(pedido, etapa, expandido, chevronRotacion, sharedScope, animatedScope)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoPill(pedido.tipo)
                Text(
                    pedido.tipoServicio,
                    style = MaterialTheme.typography.bodySmall,
                    color = brandMutedColor(),
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(14.dp))
            StageProgress(
                flujo = pedido.flujo,
                etapaActual = pedido.etapaActual,
                accent = etapa.accent,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(14.dp))
            PedidoCardChips(pedido, etapa, prioridad)
            AnimatedVisibility(visible = expandido) {
                PedidoCardExpandido(pedido, etapa, onAvanzar, onVerDetalle)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PedidoCardHeader(
    pedido: Pedido,
    etapa: ChipColors,
    expandido: Boolean,
    chevronRotacion: Float,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
        Column(modifier = Modifier.weight(1f)) {
            Text(pedido.comanda, style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
            Text(pedido.cliente, style = MaterialTheme.typography.bodyMedium, color = brandMutedColor())
        }
        Icon(
            CobreIcons.ArrowForward,
            contentDescription = if (expandido) "Contraer" else "Expandir",
            tint = etapa.accent,
            modifier = Modifier.size(22.dp).graphicsLayer { rotationZ = chevronRotacion },
        )
    }
}

@Composable
private fun PedidoCardChips(pedido: Pedido, etapa: ChipColors, prioridad: ChipColors) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        StatusChip(
            label = pedido.etapaActual.corto,
            container = etapa.container,
            content = etapa.content,
            icon = etapaIcon(pedido.etapaActual),
        )
        StatusChip(
            label = "Prioridad ${pedido.prioridad.displayName}",
            container = prioridad.container,
            content = prioridad.content,
        )
        Spacer(Modifier.weight(1f))
        val urgente = pedido.plazoEntrega.startsWith("Hoy")
        val plazoColor = when {
            !urgente -> brandMutedColor()
            cobreIsDark() -> StatusAmberDark
            else -> StatusAmber
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(CobreIcons.Schedule, contentDescription = null, tint = plazoColor, modifier = Modifier.size(15.dp))
            Text(
                pedido.plazoEntrega,
                style = MaterialTheme.typography.labelSmall,
                color = plazoColor,
                fontWeight = if (urgente) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun PedidoCardExpandido(pedido: Pedido, etapa: ChipColors, onAvanzar: () -> Unit, onVerDetalle: () -> Unit) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(brandMutedColor().copy(alpha = 0.12f)))
        Spacer(Modifier.height(16.dp))
        EtapasTimeline(flujo = pedido.flujo, indiceActual = pedido.indiceEtapa)
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val interactionAvanzar = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .pressableScale(interactionAvanzar)
                    .clip(RoundedCornerShape(50))
                    .background(if (pedido.esFinal || pedido.estaBloqueado) etapa.accent.copy(alpha = 0.35f) else etapa.accent)
                    .then(
                        if (!pedido.esFinal && !pedido.estaBloqueado) {
                            Modifier.clickable(interactionSource = interactionAvanzar, indication = null) { onAvanzar() }
                        } else Modifier,
                    )
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    when {
                        pedido.estaBloqueado -> "Resuelve la alerta para avanzar"
                        pedido.siguienteEtapa != null -> "Avanzar a ${pedido.siguienteEtapa!!.corto}"
                        else -> "Proceso completado"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            val interactionDetalle = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .pressableScale(interactionDetalle)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, etapa.accent.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .clickable(interactionSource = interactionDetalle, indication = null) { onVerDetalle() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Ver detalle",
                    style = MaterialTheme.typography.labelMedium,
                    color = etapa.accent,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/** Distintivo compacto del tipo de comanda (tienda / domicilio) para las tarjetas. */
@Composable
private fun TipoPill(tipo: TipoComanda) {
    val isDark = cobreIsDark()
    val accent = if (isDark) Brand400 else Copper700
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = if (isDark) 0.16f else 0.10f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(tipoIcon(tipo), contentDescription = null, tint = accent, modifier = Modifier.size(13.dp))
        Text(tipo.corto, style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.SemiBold)
    }
}

