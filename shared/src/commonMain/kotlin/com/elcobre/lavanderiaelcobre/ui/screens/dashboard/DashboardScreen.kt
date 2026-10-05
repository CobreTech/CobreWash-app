package com.elcobre.lavanderiaelcobre.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.ChipColors
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTopBar
import com.elcobre.lavanderiaelcobre.ui.components.EtapasTimeline
import com.elcobre.lavanderiaelcobre.ui.components.FlujoProduccionCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.HistorialCard
import com.elcobre.lavanderiaelcobre.ui.components.SelectableChip
import com.elcobre.lavanderiaelcobre.ui.components.StageProgress
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.alertaColor
import com.elcobre.lavanderiaelcobre.ui.components.alertaIcon
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
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurface
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.Stone200
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

private val ANCHO_TABLET_SPLIT = 720.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DashboardScreen(
    operario: Operario,
    pedidos: List<Pedido>,
    onAbrirPedido: (Pedido) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onComentar: (String, String) -> Unit = { _, _ -> },
    onAlerta: (String, TipoAlerta, String) -> Unit = { _, _, _ -> },
    onResolverAlerta: (String) -> Unit = {},
    onCerrarSesion: () -> Unit,
    mostrarTopBar: Boolean = true,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    var filtro by remember { mutableStateOf<TipoComanda?>(null) }
    var busqueda by remember { mutableStateOf("") }
    var expandidoId by remember { mutableStateOf<String?>(null) }
    var pedidoSeleccionadoId by remember { mutableStateOf<String?>(pedidos.firstOrNull()?.id) }

    var sidebarVisible by remember { mutableStateOf(true) }

    val visibles = remember(filtro, busqueda, pedidos) {
        val consulta = busqueda.trim()
        pedidos.filter { p ->
            (filtro == null || p.tipo == filtro) &&
            (consulta.isEmpty() || p.comanda.contains(consulta, ignoreCase = true) || p.cliente.contains(consulta, ignoreCase = true))
        }
    }
    CobreBackground(applySystemBarsInsets = mostrarTopBar) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            if (maxWidth >= ANCHO_TABLET_SPLIT) {
                TabletMasterDetailLayout(
                    pedidos = pedidos,
                    visibles = visibles,
                    filtro = filtro,
                    onFiltroChange = { filtro = it },
                    busqueda = busqueda,
                    onBusquedaChange = { busqueda = it },
                    pedidoSeleccionadoId = pedidoSeleccionadoId,
                    onSeleccionarPedido = { pedidoSeleccionadoId = it },
                    onSeleccionarPedidoDobleClick = {
                        pedidoSeleccionadoId = it
                        sidebarVisible = false
                    },
                    sidebarVisible = sidebarVisible,
                    onToggleSidebar = { sidebarVisible = !sidebarVisible },
                    onAvanzarEtapa = onAvanzarEtapa,
                    onComentar = onComentar,
                    onAlerta = onAlerta,
                    onResolverAlerta = onResolverAlerta,
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                )
            } else {
                MobileDashboardLayout(
                    operario = operario,
                    pedidos = pedidos,
                    visibles = visibles,
                    filtro = filtro,
                    onFiltroChange = { filtro = it },
                    busqueda = busqueda,
                    onBusquedaChange = { busqueda = it },
                    expandidoId = expandidoId,
                    onToggleExpand = { expandidoId = if (expandidoId == it) null else it },
                    onAvanzarEtapa = onAvanzarEtapa,
                    onAbrirPedido = onAbrirPedido,
                    onCerrarSesion = onCerrarSesion,
                    mostrarTopBar = mostrarTopBar,
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TabletMasterDetailLayout(
    pedidos: List<Pedido>,
    visibles: List<Pedido>,
    filtro: TipoComanda?,
    onFiltroChange: (TipoComanda?) -> Unit,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    pedidoSeleccionadoId: String?,
    onSeleccionarPedido: (String) -> Unit,
    onSeleccionarPedidoDobleClick: (String) -> Unit,
    sidebarVisible: Boolean,
    onToggleSidebar: () -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onComentar: (String, String) -> Unit,
    onAlerta: (String, TipoAlerta, String) -> Unit,
    onResolverAlerta: (String) -> Unit,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val haptic = LocalHapticFeedback.current
    Row(modifier = Modifier.fillMaxSize()) {
        val sidebarWidth by androidx.compose.animation.core.animateDpAsState(
            targetValue = if (sidebarVisible) 380.dp else 0.dp,
            animationSpec = tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            label = "sidebarWidth",
        )

        Box(
            modifier = Modifier
                .width(sidebarWidth)
                .fillMaxHeight(),
        ) {
            // Contenido fijo para que no se deforme durante la animación
            Column(
                modifier = Modifier
                    .width(380.dp)
                    .fillMaxHeight(),
            ) {
                MisTareasResumen(
                    pedidos = pedidos,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp),
                )
                Spacer(Modifier.height(12.dp))
                SearchBar(
                    query = busqueda,
                    onQueryChange = onBusquedaChange,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
                FiltroTipos(
                    seleccion = filtro,
                    onSeleccion = {
                        onFiltroChange(it)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp, top = 4.dp),
                )
                PedidosListColumn(
                    visibles = visibles,
                    expandidoId = null,
                    seleccionadoId = pedidoSeleccionadoId,
                    onToggleExpand = {},
                    onAvanzarEtapa = onAvanzarEtapa,
                    onSeleccionar = { p ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSeleccionarPedido(p.id)
                    },
                    onDoubleClick = { p ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSeleccionarPedidoDobleClick(p.id)
                    },
                    onLimpiar = {
                        onFiltroChange(null)
                        onBusquedaChange("")
                    },
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        val pedidoActivo = pedidos.firstOrNull { it.id == pedidoSeleccionadoId }
        TabletDetailPane(
            pedido = pedidoActivo,
            onAvanzar = { if (pedidoActivo != null) onAvanzarEtapa(pedidoActivo) },
            onComentar = { texto -> if (pedidoActivo != null) onComentar(pedidoActivo.id, texto) },
            onAlerta = { tipo, nota -> if (pedidoActivo != null) onAlerta(pedidoActivo.id, tipo, nota) },
            onResolver = { if (pedidoActivo != null) onResolverAlerta(pedidoActivo.id) },
            sidebarVisible = sidebarVisible,
            onToggleSidebar = onToggleSidebar,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MobileDashboardLayout(
    operario: Operario,
    pedidos: List<Pedido>,
    visibles: List<Pedido>,
    filtro: TipoComanda?,
    onFiltroChange: (TipoComanda?) -> Unit,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    expandidoId: String?,
    onToggleExpand: (String) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onAbrirPedido: (Pedido) -> Unit,
    onCerrarSesion: () -> Unit,
    mostrarTopBar: Boolean,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
) {
    val haptic = LocalHapticFeedback.current
    Column(modifier = Modifier.fillMaxSize()) {
        if (mostrarTopBar) {
            TopBar(operario = operario, onCerrarSesion = onCerrarSesion)
        }

        MisTareasResumen(
            pedidos = pedidos,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp),
        )

        Spacer(Modifier.height(14.dp))

        SearchBar(
            query = busqueda,
            onQueryChange = onBusquedaChange,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        )

        FiltroTipos(
            seleccion = filtro,
            onSeleccion = {
                onFiltroChange(it)
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp, top = 4.dp),
        )

        PedidosListColumn(
            visibles = visibles,
            expandidoId = expandidoId,
            seleccionadoId = null,
            onToggleExpand = { id ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggleExpand(id)
            },
            onAvanzarEtapa = onAvanzarEtapa,
            onSeleccionar = onAbrirPedido,
            onDoubleClick = { }, // En móvil no usamos doble clic para ocultar sidebar
            onLimpiar = {
                onFiltroChange(null)
                onBusquedaChange("")
            },
            sharedScope = sharedScope,
            animatedScope = animatedScope,
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PedidosListColumn(
    visibles: List<Pedido>,
    expandidoId: String?,
    seleccionadoId: String?,
    onToggleExpand: (String) -> Unit,
    onAvanzarEtapa: (Pedido) -> Unit,
    onSeleccionar: (Pedido) -> Unit,
    onDoubleClick: (Pedido) -> Unit,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp, top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (visibles.isEmpty()) {
            item {
                EmptyState(
                    onLimpiar = onLimpiar,
                    modifier = Modifier.padding(top = 40.dp),
                )
            }
        } else {
            item {
                Text(
                    "Comandas (${visibles.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = brandHeadingColor(),
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            items(visibles, key = { p -> p.id }) { pedido ->
                val isDark = cobreIsDark()
                val seleccionado = seleccionadoId == pedido.id
                PedidoCard(
                    pedido = pedido,
                    expandido = expandidoId == pedido.id,
                    seleccionado = seleccionado,
                    onToggleExpand = { onToggleExpand(pedido.id) },
                    onAvanzar = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onAvanzarEtapa(pedido)
                    },
                    onVerDetalle = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSeleccionar(pedido)
                    },
                    onDoubleClick = {
                        onDoubleClick(pedido)
                    },
                    elevation = if (isDark) 3.dp else 8.dp,
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(250),
                        placementSpec = tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                        fadeOutSpec = tween(250)
                    ),
                    sharedScope = sharedScope,
                    animatedScope = animatedScope,
                )
            }
        }
    }
}

/**
 * Panel derecho en tablets: inspección profunda de la comanda con Flujo de Producción web.
 */
@Composable
private fun TabletDetailPane(
    pedido: Pedido?,
    onAvanzar: () -> Unit,
    onComentar: (String) -> Unit,
    onAlerta: (TipoAlerta, String) -> Unit,
    onResolver: () -> Unit,
    sidebarVisible: Boolean,
    onToggleSidebar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var comentarioTexto by remember(pedido?.id) { mutableStateOf("") }
    val isDark = cobreIsDark()
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.06f) else Stone200.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .drawBehind {
                drawLine(dividerColor, start = Offset(0f, 0f), end = Offset(0f, size.height))
            }
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        if (pedido == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Brand500.copy(alpha = 0.12f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(CobreIcons.LocalLaundry, contentDescription = null, tint = Brand500, modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Selecciona una comanda",
                        style = MaterialTheme.typography.titleMedium,
                        color = brandHeadingColor(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Toca cualquier comanda de la lista para ver su flujo de avance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = brandMutedColor(),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header comanda
                TabletDetailHeader(pedido, sidebarVisible, onToggleSidebar)

                // Componente idéntico a FlujoProduccion.tsx de la web
                FlujoProduccionCard(
                    pedido = pedido,
                    onAvanzar = onAvanzar,
                    onResolverAlerta = onResolver,
                    onReportarIncidencia = { onAlerta(TipoAlerta.INCIDENCIA, "Reporte rápido desde tablet") },
                )

                // Agregar comentario rápido
                TabletComentarioCard(
                    comentario = comentarioTexto,
                    onComentarioChange = { comentarioTexto = it },
                    onEnviar = {
                        onComentar(comentarioTexto.trim())
                        comentarioTexto = ""
                    },
                )

                // Historial de eventos
                HistorialCard(pedido = pedido)
            }
        }
    }
}

@Composable
private fun TabletDetailHeader(
    pedido: Pedido,
    sidebarVisible: Boolean,
    onToggleSidebar: () -> Unit
) {
    val etapa = etapaColors(pedido.etapaActual)
    val prioridad = prioridadColors(pedido.prioridad)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
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
                    .clickable(interactionSource = interaction, indication = null, onClick = onToggleSidebar),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (sidebarVisible) CobreIcons.Close else CobreIcons.Menu,
                    contentDescription = if (sidebarVisible) "Ocultar panel" else "Mostrar panel",
                    tint = if (backDark) DarkOnSurface else Copper700,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    pedido.comanda,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = brandHeadingColor(),
                )
                Text(
                    "${pedido.cliente} · ${pedido.piezas} prendas · ${pedido.tipoServicio}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandMutedColor(),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(
                label = pedido.etapaActual.corto,
                container = etapa.container,
                content = etapa.content,
                icon = etapaIcon(pedido.etapaActual),
            )
            StatusChip(
                label = if (pedido.estadoComandaDb == null) "Prioridad ${pedido.prioridad.displayName}" else "Prioridad no definida",
                container = prioridad.container,
                content = prioridad.content,
            )
        }
    }
}

@Composable
private fun TabletComentarioCard(
    comentario: String,
    onComentarioChange: (String) -> Unit,
    onEnviar: () -> Unit,
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 6.dp) {
        Column {
            Text("Registrar nota de proceso", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = comentario,
                onValueChange = onComentarioChange,
                placeholder = { Text("Escribe un comentario sobre esta carga o prenda...", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = cobreFieldColors(),
                trailingIcon = {
                    IconButton(onClick = onEnviar, enabled = comentario.isNotBlank()) {
                        Icon(CobreIcons.Send, contentDescription = "Enviar", tint = Brand500, modifier = Modifier.size(18.dp))
                    }
                },
            )
        }
    }
}

@Composable
private fun MisTareasResumen(pedidos: List<Pedido>, modifier: Modifier = Modifier) {
    val pendientes = pedidos.count { !it.esFinal }
    // Corrección crítica: Se contabilizan únicamente las alertas que están actualmente bloqueadas/activas.
    val alertas = pedidos.count { it.estaBloqueado }
    val isDark = cobreIsDark()

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Seguimiento de Producción", style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                if (pendientes == 1) "1 comanda en proceso" else "$pendientes comandas en proceso",
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
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar comanda o cliente...", style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = { Icon(CobreIcons.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(CobreIcons.Close, contentDescription = "Limpiar búsqueda", modifier = Modifier.size(18.dp))
                }
            }
        } else null,
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
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Brand200.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                CobreIcons.Inventory,
                contentDescription = null,
                tint = Brand500.copy(alpha = 0.45f),
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "No hay comandas aquí",
            style = MaterialTheme.typography.titleMedium,
            color = brandHeadingColor(),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Ninguna comanda coincide con el filtro o la búsqueda actual.",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor().copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
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
                "Ver todas las comandas",
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
            modifier = Modifier.size(40.dp).background(BrandCtaGradient, RoundedCornerShape(12.dp)),
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
        Icon(CobreIcons.FilterList, contentDescription = null, tint = Brand700, modifier = Modifier.size(18.dp))
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

@OptIn(ExperimentalSharedTransitionApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun PedidoCard(
    pedido: Pedido,
    expandido: Boolean,
    seleccionado: Boolean = false,
    onToggleExpand: () -> Unit,
    onAvanzar: () -> Unit,
    onVerDetalle: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
    elevation: androidx.compose.ui.unit.Dp = 18.dp,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val etapa = etapaColors(pedido.etapaActual)
    val prioridad = prioridadColors(pedido.prioridad)
    val chevronRotacion by animateFloatAsState(
        targetValue = if (expandido) 90f else 0f,
        animationSpec = tween(300),
        label = "chevron",
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (seleccionado) Modifier.border(2.dp, Brand500, RoundedCornerShape(18.dp))
                else Modifier,
            )
            .pressableScale(interaction)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = {
                    if (seleccionado) onToggleExpand() else onVerDetalle()
                },
                onDoubleClick = {
                    onDoubleClick?.invoke()
                }
            ),
        tint = etapa.accent,
        elevation = if (seleccionado) 14.dp else elevation,
        stripe = prioridad.accent,
    ) {
        Column {
            PedidoCardHeader(pedido, etapa, expandido, chevronRotacion, sharedScope, animatedScope)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoPill(pedido.tipo)
                Text(
                    pedido.tipoServicio,
                    style = MaterialTheme.typography.bodySmall,
                    color = brandMutedColor(),
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            StageProgress(
                flujo = pedido.flujo,
                etapaActual = pedido.etapaActual,
                accent = etapa.accent,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
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
                .size(42.dp)
                .background(etapa.accent.copy(alpha = 0.16f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(etapaIcon(pedido.etapaActual), contentDescription = null, tint = etapa.accent, modifier = Modifier.size(22.dp))
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
            modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = chevronRotacion },
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
            label = if (pedido.estadoComandaDb == null) "Prioridad ${pedido.prioridad.displayName}" else "Prioridad no definida",
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
            Icon(CobreIcons.Schedule, contentDescription = null, tint = plazoColor, modifier = Modifier.size(14.dp))
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
    Column(modifier = Modifier.padding(top = 14.dp)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(brandMutedColor().copy(alpha = 0.12f)))
        Spacer(Modifier.height(14.dp))
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
