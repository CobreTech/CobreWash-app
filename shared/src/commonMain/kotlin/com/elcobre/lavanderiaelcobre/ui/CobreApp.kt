package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elcobre.lavanderiaelcobre.data.mock.MockData
import com.elcobre.lavanderiaelcobre.data.model.Insumo
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackHandler
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackdrop
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.WebSidebar
import com.elcobre.lavanderiaelcobre.ui.components.WebTopbar
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
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

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

    CobreBackHandler(enabled = vm.puedeVolver) {
        vm.volver()
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
                        val isTopLevel = fun(p: Pantalla): Boolean = p in listOf(
                            Pantalla.Dashboard, Pantalla.Avisos, Pantalla.Insumos, Pantalla.Configuracion, Pantalla.Vehiculos
                        )
                        
                        if (isTopLevel(initialState) && isTopLevel(targetState)) {
                            // Entre tabs principales: crossfade suave sin slide. 
                            // Esto hace que la Sidebar y la Topbar parezcan fijas y solo el contenido cambie.
                            fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                        } else {
                            // Navegación profunda (ej. a Detalles, Escáner, Login): slide completo.
                            val haciaAdelante = orden(targetState) >= orden(initialState)
                            val dir = if (haciaAdelante) 1 else -1
                            (slideInHorizontally(tween(300)) { w -> dir * w } + fadeIn(tween(300))) togetherWith
                                (slideOutHorizontally(tween(300)) { w -> -dir * w } + fadeOut(tween(300)))
                        }
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
        Pantalla.Avisos -> OperarioScaffold(destino, vm) { isTablet ->
            AvisosScreen(avisos = vm.avisos, applySystemBarsInsets = !isTablet)
        }
        Pantalla.Configuracion -> ConfiguracionDestino(destino, vm)
        Pantalla.Escaner -> EscanerDestino(vm)
        Pantalla.Insumos -> InsumosDestino(destino, vm)
        Pantalla.Vehiculos -> VehiculosDestino(vm)
        is Pantalla.Detalle -> DetalleDestino(destino, vm, sharedScope, animatedScope)
    }
}

@Composable
private fun LoginDestino(vm: CobreViewModel) {
    val isDark = cobreIsDark()
    LoginScreen(
        onLogin = { sesion -> vm.iniciarSesion(sesion) },
        onToggleTema = { vm.alternarTema(isDark) },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DashboardDestino(vm: CobreViewModel, sharedScope: SharedTransitionScope, animatedScope: AnimatedVisibilityScope) {
    OperarioScaffold(Pantalla.Dashboard, vm) { isTablet ->
        DashboardScreen(
            operario = vm.operario ?: MockData.operario,
            pedidos = vm.pedidos,
            onAbrirPedido = { vm.navegar(Pantalla.Detalle(it.id)) },
            onAvanzarEtapa = { vm.avanzarEtapa(it.id, "") },
            onComentar = { id, nota -> vm.agregarComentario(id, nota) },
            onAlerta = { id, tipo, nota -> vm.registrarAlerta(id, tipo, nota) },
            onResolverAlerta = { id -> vm.resolverAlerta(id) },
            onCerrarSesion = { vm.cerrarSesion() },
            mostrarTopBar = !isTablet,
            sharedScope = sharedScope,
            animatedScope = animatedScope,
        )
    }
}

@Composable
private fun ConfiguracionDestino(destino: Pantalla, vm: CobreViewModel) {
    OperarioScaffold(destino, vm) { isTablet ->
        ConfiguracionScreen(
            operario = vm.operario ?: MockData.operario,
            modoTema = vm.modoTema,
            onCambiarTema = { vm.cambiarModoTema(it) },
            onCerrarSesion = { vm.cerrarSesion() },
            applySystemBarsInsets = !isTablet,
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
    OperarioScaffold(destino, vm) { isTablet ->
        InsumosScreen(
            insumos = Insumo.entries,
            alertas = vm.alertasStock,
            onCrear = { insumo, severidad, nota -> vm.crearAlertaStock(insumo, severidad, nota) },
            applySystemBarsInsets = !isTablet,
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
    content: @Composable (isTablet: Boolean) -> Unit,
) {
    val isDark = cobreIsDark()
    val onSeleccionarTab: (Pantalla) -> Unit = { vm.navegar(it) }
    val onEscanearQR: () -> Unit = { vm.navegar(Pantalla.Escaner) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= ANCHO_TABLET
        if (isTablet) {
            var sidebarVisible by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
            val sidebarWidth by androidx.compose.animation.core.animateDpAsState(
                targetValue = if (sidebarVisible) 240.dp else 0.dp,
                animationSpec = tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                label = "mainSidebarWidth",
            )
            
            Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
                Box(
                    modifier = Modifier
                        .width(sidebarWidth)
                        .fillMaxHeight()
                        .clipToBounds() // Recorta el contenido para evitar que los textos se vean al colapsar
                ) {
                    WebSidebar(
                        tabActual = tabActual,
                        onSeleccionarTab = onSeleccionarTab,
                        onEscanearQR = onEscanearQR,
                        comandasActivas = vm.pedidos.count { !it.esFinal },
                        avisosCount = vm.avisos.size,
                        alertasStockCount = vm.alertasStock.size,
                        operarioNombre = vm.operario?.nombre ?: "Operario Planta",
                        operarioRol = vm.operario?.turno ?: "Turno Mañana",
                        onCerrarSesion = { vm.cerrarSesion() },
                        modifier = Modifier.requiredWidth(240.dp) // Fuerza a que no se redimensione, solo se recorte
                    )
                }
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    WebTopbar(
                        usuarioNombre = vm.operario?.nombre ?: "Operario Planta",
                        usuarioRol = "OPERARIO - " + (vm.operario?.turno ?: "PLANTA"),
                        modoTema = vm.modoTema,
                        onToggleTema = { vm.alternarTema(isDark) },
                        showSearch = false,
                        alertasCount = vm.pedidos.count { it.estaBloqueado },
                        onMenuClick = { sidebarVisible = !sidebarVisible },
                    )
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        content(true)
                    }
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
                    content(false)
                }
            }
        }
    }
}

/**
 * Barra inferior: 4 tabs con un botón circular central para escanear el QR de la
 * comanda (RF-SP02/SP09), la acción más frecuente en planta. El tab activo se
 * resalta con la misma superficie cobriza discreta que usa el sidebar de la web.
 */
@Composable
private fun BottomNavRow(
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
            .padding(horizontal = 3.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (seleccionado) Brand500.copy(alpha = 0.11f) else Color.Transparent)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = accent, modifier = Modifier.size(22.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = accent)
    }
}
