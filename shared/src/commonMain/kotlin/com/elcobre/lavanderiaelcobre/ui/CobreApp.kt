package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elcobre.lavanderiaelcobre.data.mock.MockData
import com.elcobre.lavanderiaelcobre.data.model.Insumo
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.screens.avisos.AvisosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.configuracion.ConfiguracionScreen
import com.elcobre.lavanderiaelcobre.ui.screens.dashboard.DashboardScreen
import com.elcobre.lavanderiaelcobre.ui.screens.detalle.DetalleScreen
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.EscanerScreen
import com.elcobre.lavanderiaelcobre.ui.screens.insumos.InsumosScreen
import com.elcobre.lavanderiaelcobre.ui.screens.login.LoginScreen
import com.elcobre.lavanderiaelcobre.ui.screens.vehiculos.VehiculosScreen
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema

/**
 * Raíz de la aplicación: aplica el tema de marca y gestiona la navegación con
 * transiciones animadas. El estado vive en [CobreViewModel]; las pantallas son
 * sin estado de dominio, reciben datos y delegan acciones al ViewModel.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CobreApp() {
    val vm: CobreViewModel = viewModel { CobreViewModel() }
    val temaOscuro = when (vm.modoTema) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.SISTEMA -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    CobreTheme(darkTheme = temaOscuro) {
        val pantalla = vm.pantalla

        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo persistente detrás de toda la navegación: no se recrea en cada
            // cambio de pantalla, así el blur no se recalcula por navegación y el
            // fondo no se desliza junto con la transición (evita la sensación de
            // "recarga completa" al cambiar de pantalla).
            CobreBackdrop()

            // Permite que el icono de etapa "vuele" de la tarjeta del Dashboard a la cabecera del Detalle.
            SharedTransitionLayout {
                val sharedScope = this
                AnimatedContent(
                    targetState = pantalla,
                    transitionSpec = {
                        // 300ms: lo que Material recomienda para transiciones de pantalla
                        // completa; 420ms se sentía lento sumado a las animaciones propias
                        // de cada pantalla.
                        val haciaAdelante = orden(targetState) >= orden(initialState)
                        val dir = if (haciaAdelante) 1 else -1
                        (slideInHorizontally(tween(300)) { w -> dir * w } + fadeIn(tween(300))) togetherWith
                            (slideOutHorizontally(tween(300)) { w -> -dir * w } + fadeOut(tween(300)))
                    },
                    label = "navegacion",
                ) { destino ->
                    DestinoContent(
                        destino = destino,
                        vm = vm,
                        sharedScope = sharedScope,
                        animatedScope = this,
                    )
                }
            }
        }
    }
}

/** Contenido de cada [Pantalla], despachado desde [CobreApp]; una función por destino para mantener esto legible. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DestinoContent(
    destino: Pantalla,
    vm: CobreViewModel,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedVisibilityScope,
) {
    when (destino) {
        Pantalla.Login -> LoginDestino(vm)
        Pantalla.Dashboard -> DashboardDestino(vm, sharedScope, animatedScope)
        Pantalla.Avisos -> OperarioScaffold(destino, vm) { AvisosScreen(avisos = vm.avisos) }
        Pantalla.Configuracion -> ConfiguracionDestino(destino, vm)
        Pantalla.Escaner -> EscanerDestino(vm)
        Pantalla.Insumos -> InsumosDestino(destino, vm)
        Pantalla.Vehiculos -> VehiculosDestino(vm)
        is Pantalla.Detalle -> DetalleDestino(destino, vm, sharedScope, animatedScope)
    }
}

@Composable
private fun LoginDestino(vm: CobreViewModel) {
    LoginScreen(onLogin = { sesion -> vm.iniciarSesion(sesion) })
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DashboardDestino(vm: CobreViewModel, sharedScope: SharedTransitionScope, animatedScope: AnimatedVisibilityScope) {
    OperarioScaffold(Pantalla.Dashboard, vm) {
        DashboardScreen(
            operario = vm.operario ?: MockData.operario,
            pedidos = vm.pedidos,
            onAbrirPedido = { vm.navegar(Pantalla.Detalle(it.id)) },
            onAvanzarEtapa = { vm.avanzarEtapa(it.id, "") },
            onCerrarSesion = { vm.cerrarSesion() },
            sharedScope = sharedScope,
            animatedScope = animatedScope,
        )
    }
}

@Composable
private fun ConfiguracionDestino(destino: Pantalla, vm: CobreViewModel) {
    OperarioScaffold(destino, vm) {
        ConfiguracionScreen(
            operario = vm.operario ?: MockData.operario,
            modoTema = vm.modoTema,
            onCambiarTema = { vm.cambiarModoTema(it) },
            onCerrarSesion = { vm.cerrarSesion() },
        )
    }
}

@Composable
private fun EscanerDestino(vm: CobreViewModel) {
    EscanerScreen(
        onVolver = { vm.volver() },
        escaneoSimulado = { vm.siguientePendiente() },
        buscarComanda = { codigo -> vm.pedidoPorComanda(codigo) },
        onAbrir = { pedido -> vm.navegar(Pantalla.Detalle(pedido.id)) },
    )
}

@Composable
private fun InsumosDestino(destino: Pantalla, vm: CobreViewModel) {
    OperarioScaffold(destino, vm) {
        InsumosScreen(
            insumos = Insumo.entries,
            alertas = vm.alertasStock,
            onCrear = { insumo, severidad, nota -> vm.crearAlertaStock(insumo, severidad, nota) },
        )
    }
}

@Composable
private fun VehiculosDestino(vm: CobreViewModel) {
    val vehiculosVm: VehiculosViewModel = viewModel { VehiculosViewModel() }
    val administrador = vm.administrador ?: MockData.administrador
    VehiculosScreen(
        administrador = administrador,
        vehiculos = vehiculosVm.vehiculos,
        chequeoEnRuta = { id -> vehiculosVm.chequeoEnRuta(id) },
        historialVehiculo = { id -> vehiculosVm.historialVehiculo(id) },
        onRegistrarSalida = { id, km, fotos, obs ->
            vehiculosVm.registrarSalidaVehiculo(id, km, fotos, obs, administrador.nombre)
        },
        onRegistrarEntrada = { id, km, fotos, obs -> vehiculosVm.registrarEntradaVehiculo(id, km, fotos, obs) },
        onAgregarVehiculo = { patente, modelo, km -> vehiculosVm.registrarVehiculo(patente, modelo, km) },
        onCerrarSesion = { vm.cerrarSesion() },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetalleDestino(
    destino: Pantalla.Detalle,
    vm: CobreViewModel,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedVisibilityScope,
) {
    val pedido = vm.pedido(destino.pedidoId)
    if (pedido == null) {
        LaunchedEffect(destino.pedidoId) { vm.volver() }
        return
    }
    DetalleScreen(
        pedido = pedido,
        onVolver = { vm.volver() },
        onAvanzar = { comentario -> vm.avanzarEtapa(pedido.id, comentario) },
        onComentar = { texto -> vm.agregarComentario(pedido.id, texto) },
        onAlerta = { tipo, comentario -> vm.registrarAlerta(pedido.id, tipo, comentario) },
        onResolver = { vm.resolverAlerta(pedido.id) },
        sharedScope = sharedScope,
        animatedScope = animatedScope,
    )
}

/** Ítems de navegación del Operario, compartidos entre el bottom nav (teléfono) y el rail (tablet ancha). */
private data class NavItem(val icon: ImageVector, val label: String, val pantalla: Pantalla)

private val navItems = listOf(
    NavItem(CobreIcons.LocalLaundry, "Pedidos", Pantalla.Dashboard),
    NavItem(CobreIcons.Campaign, "Avisos", Pantalla.Avisos),
    NavItem(CobreIcons.Inventory, "Inventario", Pantalla.Insumos),
    NavItem(CobreIcons.Person, "Ajustes", Pantalla.Configuracion),
)

/** Ancho a partir del cual se usa el rail lateral en vez del bottom nav (breakpoint "medium" de Material 3). */
private val ANCHO_TABLET = 600.dp

/**
 * Navegación del rol Operario, adaptativa según el ancho disponible (NFR05: la app
 * debe operarse en tablets de 10"): bottom nav en teléfono, rail lateral en pantallas
 * anchas — el equivalente adaptativo del sidebar colapsado de la web, en vez de
 * forzar un patrón de teléfono en un ancho donde sobra espacio horizontal.
 */
@Composable
private fun OperarioScaffold(
    tabActual: Pantalla,
    vm: CobreViewModel,
    content: @Composable () -> Unit,
) {
    val onSeleccionarTab: (Pantalla) -> Unit = { vm.navegar(it) }
    val onEscanearQR: () -> Unit = { vm.navegar(Pantalla.Escaner) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth >= ANCHO_TABLET) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRailColumn(tabActual, onSeleccionarTab, onEscanearQR)
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    content()
                }
            }
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                // Los insets de status/navigation bar los maneja CobreBackground dentro de
                // content() (igual que en las pantallas sin Scaffold); si Scaffold también
                // los reservara aquí, se aplicarían dos veces y el layout queda mal calculado.
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                        BottomNavRow(tabActual, onSeleccionarTab, onEscanearQR)
                    }
                },
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    content()
                }
            }
        }
    }
}

/** `null` marca el slot central del QR (no seleccionable) entre los ítems de navegación. */
private val slotsNav: List<Pantalla?> =
    listOf(navItems[0].pantalla, navItems[1].pantalla, null, navItems[2].pantalla, navItems[3].pantalla)

/**
 * Barra inferior: 4 tabs con un botón circular central para escanear el QR de la
 * comanda (RF-SP02/SP09), la acción más frecuente en planta. El tab activo se
 * resalta con una "pill" que se desliza con spring detrás del ítem seleccionado.
 */
@Composable
private fun BottomNavRow(
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: () -> Unit,
) {
    var barWidthPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val slotWidthPx = barWidthPx / slotsNav.size
    val indiceSeleccionado = slotsNav.indexOf(tabActual).coerceAtLeast(0)
    val pillOffsetPx by animateFloatAsState(
        targetValue = indiceSeleccionado * slotWidthPx,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
        label = "pillOffset",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { barWidthPx = it.size.width.toFloat() },
    ) {
        if (slotWidthPx > 0f) {
            with(density) {
                Box(
                    modifier = Modifier
                        .offset(x = pillOffsetPx.toDp())
                        .width(slotWidthPx.toDp())
                        .padding(6.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brand500.copy(alpha = 0.12f)),
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NavTabButton(
                icon = navItems[0].icon,
                label = navItems[0].label,
                seleccionado = tabActual == navItems[0].pantalla,
                onClick = { onSeleccionarTab(navItems[0].pantalla) },
                modifier = Modifier.weight(1f),
            )
            NavTabButton(
                icon = navItems[1].icon,
                label = navItems[1].label,
                seleccionado = tabActual == navItems[1].pantalla,
                onClick = { onSeleccionarTab(navItems[1].pantalla) },
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                QrTabButton(onClick = onEscanearQR)
            }
            NavTabButton(
                icon = navItems[2].icon,
                label = navItems[2].label,
                seleccionado = tabActual == navItems[2].pantalla,
                onClick = { onSeleccionarTab(navItems[2].pantalla) },
                modifier = Modifier.weight(1f),
            )
            NavTabButton(
                icon = navItems[3].icon,
                label = navItems[3].label,
                seleccionado = tabActual == navItems[3].pantalla,
                onClick = { onSeleccionarTab(navItems[3].pantalla) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Rail lateral para pantallas anchas: 2 tabs, el QR al medio (misma acción y posición relativa que en el bottom nav), y 2 tabs más. */
@Composable
private fun NavigationRailColumn(
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(88.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        navItems.take(2).forEach { item ->
            RailTabButton(
                icon = item.icon,
                label = item.label,
                seleccionado = tabActual == item.pantalla,
                onClick = { onSeleccionarTab(item.pantalla) },
            )
            Spacer(Modifier.height(8.dp))
        }
        QrTabButton(onClick = onEscanearQR)
        Spacer(Modifier.height(8.dp))
        navItems.drop(2).forEach { item ->
            RailTabButton(
                icon = item.icon,
                label = item.label,
                seleccionado = tabActual == item.pantalla,
                onClick = { onSeleccionarTab(item.pantalla) },
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RailTabButton(
    icon: ImageVector,
    label: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (seleccionado) Brand500 else MaterialTheme.colorScheme.onSurfaceVariant
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (seleccionado) Brand500.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = accent, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = accent)
    }
}

/** Botón circular destacado (marca) para la acción principal del Operario: escanear QR. */
@Composable
private fun QrTabButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(46.dp)
            .pressableScale(interaction)
            .clip(RoundedCornerShape(50))
            .background(Brand500)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            CobreIcons.QrCodeScanner,
            contentDescription = "Escanear QR",
            tint = Color.White,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun NavTabButton(
    icon: ImageVector,
    label: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = if (seleccionado) Brand500 else MaterialTheme.colorScheme.onSurfaceVariant
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = accent, modifier = Modifier.size(22.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = accent)
    }
}
