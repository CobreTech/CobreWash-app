package com.elcobre.lavanderiaelcobre.ui.screens.vehiculos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FiltroFlota
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.KpisFlota
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.data.model.calcularKpisFlota
import com.elcobre.lavanderiaelcobre.data.model.esPatenteChilenaValida
import com.elcobre.lavanderiaelcobre.data.model.esRolConductorORepartidor
import com.elcobre.lavanderiaelcobre.data.model.normalizarPatenteChilena
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTopBar
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.MiniaturaFotoChequeo
import com.elcobre.lavanderiaelcobre.ui.components.SelectableChip
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.components.rememberCapturadorFoto
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface1
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.Stone200
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import kotlinx.coroutines.delay

private val ETIQUETAS_FOTOS = listOf("Frente", "Lateral derecho", "Lateral izquierdo", "Posterior", "Tablero / Odómetro")

/**
 * Vista del rol Administrador conectada a Firebase Data Connect (`RF-VE01` a `RF-VE04`):
 * administra el catálogo de flota, registra salidas e inspecciones previas, recibe retornos
 * con validación de odómetro y consulta la bitácora histórica con evidencia fotográfica.
 */
@Composable
fun VehiculosScreen(
    administrador: Administrador,
    vehiculos: List<Vehiculo>,
    chequeoEnRuta: (String) -> ChequeoVehiculo?,
    historialVehiculo: (String) -> List<ChequeoVehiculo>,
    onRegistrarSalida: (vehiculoId: String, km: Int, fotos: List<FotoChequeo>, observaciones: String?) -> Unit,
    onRegistrarEntrada: (vehiculoId: String, km: Int, fotos: List<FotoChequeo>, observaciones: String?) -> Unit,
    onAgregarVehiculo: (patente: String, modelo: String, kilometrajeInicial: Int) -> Unit,
    onCerrarSesion: () -> Unit,
    conductores: List<ConductorFlota> = emptyList(),
    kpis: KpisFlota = calcularKpisFlota(vehiculos, emptyList()),
    filtroActual: FiltroFlota = FiltroFlota.TODOS,
    busqueda: String = "",
    cargando: Boolean = false,
    operacionEnCurso: Boolean = false,
    error: String? = null,
    mensajeExito: String? = null,
    mostrarTopBar: Boolean = true,
    onToggleTema: (() -> Unit)? = null,
    onCambiarBusqueda: (String) -> Unit = {},
    onSeleccionarFiltro: (FiltroFlota) -> Unit = {},
    onRefrescar: () -> Unit = {},
    onLimpiarMensajes: () -> Unit = {},
    onCambiarEstadoVehiculo: (vehiculoId: String, activo: Boolean) -> Unit = { _, _ -> },
    onRegistrarVehiculoDetallado: (patente: String, marca: String, modelo: String, anio: Int?, km: Int) -> Unit =
        { patente, _, modelo, _, km -> onAgregarVehiculo(patente, modelo, km) },
    onRegistrarSalidaDetallada: (
        vehiculoId: String,
        repartidorId: String?,
        responsable: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit = { id, _, _, km, _, fotos, obs -> onRegistrarSalida(id, km, fotos, obs) },
    onRegistrarEntradaDetallada: (
        vehiculoId: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit = { id, km, _, fotos, obs -> onRegistrarEntrada(id, km, fotos, obs) },
) {
    var vehiculoChequeoModal by remember { mutableStateOf<Vehiculo?>(null) }
    var vehiculoHistorialModal by remember { mutableStateOf<Vehiculo?>(null) }
    var mostrandoAltaModal by remember { mutableStateOf(false) }

    LaunchedEffect(mensajeExito) {
        if (mensajeExito != null) {
            delay(3500)
            onLimpiarMensajes()
        }
    }

    CobreBackground(applySystemBarsInsets = mostrarTopBar) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (mostrarTopBar) {
                TopBarAdmin(
                    administrador = administrador,
                    onToggleTema = onToggleTema,
                    onCerrarSesion = onCerrarSesion,
                )
            }
            ContenidoFlotaGrid(
                vehiculos = vehiculos,
                kpis = kpis,
                filtroActual = filtroActual,
                busqueda = busqueda,
                cargando = cargando,
                operacionEnCurso = operacionEnCurso,
                error = error,
                mensajeExito = mensajeExito,
                chequeoEnRuta = chequeoEnRuta,
                historialVehiculo = historialVehiculo,
                onRefrescar = onRefrescar,
                onAbrirAlta = { mostrandoAltaModal = true },
                onLimpiarMensajes = onLimpiarMensajes,
                onCambiarBusqueda = onCambiarBusqueda,
                onSeleccionarFiltro = onSeleccionarFiltro,
                onAbrirChequeo = { vehiculoChequeoModal = it },
                onAbrirHistorial = { vehiculoHistorialModal = it },
                onCambiarEstadoVehiculo = onCambiarEstadoVehiculo,
            )
        }
    }

    if (mostrandoAltaModal) {
        ModalAgregarVehiculo(
            patentesExistentes = vehiculos.map { it.patente },
            onDismiss = { mostrandoAltaModal = false },
            onAgregar = { patente, marca, modelo, anio, km ->
                onRegistrarVehiculoDetallado(patente, marca, modelo, anio, km)
                mostrandoAltaModal = false
            },
        )
    }

    vehiculoChequeoModal?.let { vehiculo ->
        val chequeoActivo = chequeoEnRuta(vehiculo.id)
        ModalChequeoVehiculo(
            vehiculo = vehiculo,
            chequeo = chequeoActivo,
            conductores = conductores,
            administradorNombre = administrador.nombre,
            onDismiss = { vehiculoChequeoModal = null },
            onConfirmar = { repartidorId, responsable, km, estado, fotos, obs ->
                if (chequeoActivo != null && chequeoActivo.estaEnRuta) {
                    onRegistrarEntradaDetallada(vehiculo.id, km, estado, fotos, obs)
                } else {
                    onRegistrarSalidaDetallada(vehiculo.id, repartidorId, responsable, km, estado, fotos, obs)
                }
                vehiculoChequeoModal = null
            },
        )
    }

    vehiculoHistorialModal?.let { vehiculo ->
        ModalHistorialVehiculo(
            vehiculo = vehiculo,
            historial = historialVehiculo(vehiculo.id),
            onDismiss = { vehiculoHistorialModal = null },
        )
    }
}

@Composable
private fun ContenidoFlotaGrid(
    vehiculos: List<Vehiculo>,
    kpis: KpisFlota,
    filtroActual: FiltroFlota,
    busqueda: String,
    cargando: Boolean,
    operacionEnCurso: Boolean,
    error: String?,
    mensajeExito: String?,
    chequeoEnRuta: (String) -> ChequeoVehiculo?,
    historialVehiculo: (String) -> List<ChequeoVehiculo>,
    onRefrescar: () -> Unit,
    onAbrirAlta: () -> Unit,
    onLimpiarMensajes: () -> Unit,
    onCambiarBusqueda: (String) -> Unit,
    onSeleccionarFiltro: (FiltroFlota) -> Unit,
    onAbrirChequeo: (Vehiculo) -> Unit,
    onAbrirHistorial: (Vehiculo) -> Unit,
    onCambiarEstadoVehiculo: (String, Boolean) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val esAncho = maxWidth >= 700.dp
        val columnas = if (esAncho) GridCells.Adaptive(minSize = 340.dp) else GridCells.Fixed(1)
        LazyVerticalGrid(
            columns = columnas,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 36.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CabeceraFlota(
                    cargando = cargando || operacionEnCurso,
                    onRefrescar = onRefrescar,
                    onAbrirAlta = onAbrirAlta,
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                ResumenKpiFlota(
                    kpis = kpis,
                    filtroActual = filtroActual,
                    esAncho = esAncho,
                    onSeleccionarFiltro = onSeleccionarFiltro,
                )
            }
            if (error != null || mensajeExito != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    BannersEstadoFlota(
                        error = error,
                        mensajeExito = mensajeExito,
                        onLimpiar = onLimpiarMensajes,
                    )
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                BarraFiltroFlota(
                    busqueda = busqueda,
                    filtroActual = filtroActual,
                    kpis = kpis,
                    onCambiarBusqueda = onCambiarBusqueda,
                    onSeleccionarFiltro = onSeleccionarFiltro,
                )
            }
            if (vehiculos.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EstadoVacioFlota(
                        cargando = cargando,
                        busqueda = busqueda,
                        filtroActual = filtroActual,
                        onLimpiarFiltros = {
                            onCambiarBusqueda("")
                            onSeleccionarFiltro(FiltroFlota.TODOS)
                        },
                        onRefrescar = onRefrescar,
                    )
                }
            } else {
                items(vehiculos, key = { it.id }) { vehiculo ->
                    val chequeo = chequeoEnRuta(vehiculo.id)
                    VehiculoCard(
                        vehiculo = vehiculo,
                        chequeo = chequeo,
                        totalHistorial = historialVehiculo(vehiculo.id).size,
                        onAbrirChequeo = { onAbrirChequeo(vehiculo) },
                        onAbrirHistorial = { onAbrirHistorial(vehiculo) },
                        onToggleActivo = { onCambiarEstadoVehiculo(vehiculo.id, !vehiculo.activo) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CabeceraFlota(
    cargando: Boolean,
    onRefrescar: () -> Unit,
    onAbrirAlta: () -> Unit,
) {
    val isDark = cobreIsDark()
    val refreshInteraction = remember { MutableInteractionSource() }
    val addInteraction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Flota de Reparto",
                style = MaterialTheme.typography.headlineMedium,
                color = brandHeadingColor(),
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Control de unidades, despacho a ruta e inspección técnica",
                style = MaterialTheme.typography.bodyMedium,
                color = brandMutedColor(),
            )
        }
        Spacer(Modifier.width(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .pressableScale(refreshInteraction)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) DarkSurface2 else Color.White.copy(alpha = 0.7f))
                    .border(
                        1.dp,
                        if (isDark) DarkOutline else Stone200.copy(alpha = 0.8f),
                        RoundedCornerShape(14.dp),
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = "Actualizar flota"
                    }
                    .clickable(
                        interactionSource = refreshInteraction,
                        indication = null,
                        enabled = !cargando,
                        onClick = onRefrescar,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Brand400,
                        strokeWidth = 2.2.dp,
                    )
                } else {
                    Icon(
                        CobreIcons.History,
                        contentDescription = null,
                        tint = if (isDark) Brand400 else Brand700,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .height(48.dp)
                    .pressableScale(addInteraction)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandCtaGradient)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Registrar nuevo vehículo"
                    }
                    .clickable(interactionSource = addInteraction, indication = null, onClick = onAbrirAlta)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(CobreIcons.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(
                    "Nuevo vehículo",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private data class KpiDescriptor(
    val titulo: String,
    val subtitulo: String,
    val valor: String,
    val icono: ImageVector,
    val accent: Color,
    val filtro: FiltroFlota,
)

@Composable
private fun ResumenKpiFlota(
    kpis: KpisFlota,
    filtroActual: FiltroFlota,
    esAncho: Boolean,
    onSeleccionarFiltro: (FiltroFlota) -> Unit,
) {
    val isDark = cobreIsDark()
    val colorTotal = if (isDark) Brand400 else Brand500
    val colorDisponible = if (isDark) StatusGreenDark else StatusGreen
    val colorEnRuta = if (isDark) StatusAmberDark else StatusAmber
    val colorInactivo = if (kpis.inactivos > 0) (if (isDark) StatusRedDark else StatusRed) else brandMutedColor()

    val items = listOf(
        KpiDescriptor(
            "Total Flota", "Unidades registradas", "${kpis.total}",
            CobreIcons.LocalShipping, colorTotal, FiltroFlota.TODOS,
        ),
        KpiDescriptor(
            "Disponibles", "Listos en planta", "${kpis.disponibles}",
            CobreIcons.CheckCircle, colorDisponible, FiltroFlota.DISPONIBLES,
        ),
        KpiDescriptor(
            "En Ruta", "Despachos activos", "${kpis.enRuta}",
            CobreIcons.Speed, colorEnRuta, FiltroFlota.EN_RUTA,
        ),
        KpiDescriptor(
            "Mantención", "Fuera de servicio", "${kpis.inactivos}",
            CobreIcons.Warning, colorInactivo, FiltroFlota.INACTIVOS,
        ),
    )

    if (esAncho) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item ->
                KpiFlotaCard(
                    titulo = item.titulo,
                    subtitulo = item.subtitulo,
                    valor = item.valor,
                    icono = item.icono,
                    accent = item.accent,
                    seleccionado = filtroActual == item.filtro,
                    onClick = { onSeleccionarFiltro(item.filtro) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.chunked(2).forEach { par ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    par.forEach { item ->
                        KpiFlotaCard(
                            titulo = item.titulo,
                            subtitulo = item.subtitulo,
                            valor = item.valor,
                            icono = item.icono,
                            accent = item.accent,
                            seleccionado = filtroActual == item.filtro,
                            onClick = { onSeleccionarFiltro(item.filtro) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiFlotaCard(
    titulo: String,
    subtitulo: String,
    valor: String,
    icono: ImageVector,
    accent: Color,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    GlassCard(
        modifier = modifier
            .pressableScale(interaction)
            .semantics(mergeDescendants = true) { role = Role.Tab }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        elevation = if (seleccionado) 10.dp else 6.dp,
        tint = accent,
        stripe = if (seleccionado) accent else null,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.labelMedium,
                    color = brandMutedColor(),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    valor,
                    style = MaterialTheme.typography.headlineSmall,
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = brandMutedColor().copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accent.copy(alpha = 0.14f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icono, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun BannersEstadoFlota(error: String?, mensajeExito: String?, onLimpiar: () -> Unit) {
    val isDark = cobreIsDark()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AnimatedVisibility(visible = error != null) {
            val rojo = if (isDark) StatusRedDark else StatusRed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(rojo.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
                    .border(1.dp, rojo.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = rojo, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    error.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = rojo,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onLimpiar) {
                    Text("Cerrar", color = rojo, fontWeight = FontWeight.Bold)
                }
            }
        }
        AnimatedVisibility(visible = mensajeExito != null) {
            val verde = if (isDark) StatusGreenDark else StatusGreen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(verde.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
                    .border(1.dp, verde.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(CobreIcons.CheckCircle, contentDescription = null, tint = verde, modifier = Modifier.size(18.dp))
                Text(
                    mensajeExito.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = verde,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun BarraFiltroFlota(
    busqueda: String,
    filtroActual: FiltroFlota,
    kpis: KpisFlota,
    onCambiarBusqueda: (String) -> Unit,
    onSeleccionarFiltro: (FiltroFlota) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onCambiarBusqueda,
            label = { Text("Buscar por patente, marca, modelo o conductor") },
            singleLine = true,
            leadingIcon = {
                Icon(CobreIcons.Search, contentDescription = null, tint = Brand500, modifier = Modifier.size(20.dp))
            },
            trailingIcon = if (busqueda.isNotEmpty()) {
                {
                    IconButton(onClick = { onCambiarBusqueda("") }) {
                        Icon(CobreIcons.Close, contentDescription = "Limpiar búsqueda", modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = cobreFieldColors(),
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FiltroFlota.entries) { opcion ->
                val cantidad = when (opcion) {
                    FiltroFlota.TODOS -> kpis.total
                    FiltroFlota.DISPONIBLES -> kpis.disponibles
                    FiltroFlota.EN_RUTA -> kpis.enRuta
                    FiltroFlota.INACTIVOS -> kpis.inactivos
                }
                SelectableChip(
                    label = "${opcion.etiqueta} ($cantidad)",
                    seleccionado = filtroActual == opcion,
                    onClick = { onSeleccionarFiltro(opcion) },
                )
            }
        }
    }
}

@Composable
private fun EstadoVacioFlota(
    cargando: Boolean,
    busqueda: String,
    filtroActual: FiltroFlota,
    onLimpiarFiltros: () -> Unit,
    onRefrescar: () -> Unit,
) {
    val hayFiltrosActivos = busqueda.isNotBlank() || filtroActual != FiltroFlota.TODOS
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 6.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (cargando) {
                CircularProgressIndicator(color = Brand400)
                Text(
                    "Sincronizando flota de vehículos…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandMutedColor(),
                    textAlign = TextAlign.Center,
                )
            } else {
                Icon(
                    imageVector = CobreIcons.LocalShipping,
                    contentDescription = null,
                    tint = Brand500,
                    modifier = Modifier.size(32.dp),
                )
                Text(
                    text = if (hayFiltrosActivos) {
                        "No hay vehículos que coincidan con el filtro seleccionado."
                    } else {
                        "No hay vehículos registrados en la flota."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandMutedColor(),
                    textAlign = TextAlign.Center,
                )
                TextButton(
                    onClick = if (hayFiltrosActivos) onLimpiarFiltros else onRefrescar,
                ) {
                    Text(
                        if (hayFiltrosActivos) "Limpiar filtros" else "Actualizar flota",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBarAdmin(
    administrador: Administrador,
    onToggleTema: (() -> Unit)?,
    onCerrarSesion: () -> Unit,
) {
    CobreTopBar(
        nombre = administrador.nombre,
        subtitulo = administrador.rol,
        onCerrarSesion = onCerrarSesion,
        onToggleTema = onToggleTema,
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(BrandCtaGradient, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                administrador.nombre.trim().take(1).uppercase().ifEmpty { "A" },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun VehiculoCard(
    vehiculo: Vehiculo,
    chequeo: ChequeoVehiculo?,
    totalHistorial: Int,
    onAbrirChequeo: () -> Unit,
    onAbrirHistorial: () -> Unit,
    onToggleActivo: () -> Unit,
) {
    val enRuta = chequeo != null && chequeo.estaEnRuta
    val isDark = cobreIsDark()
    val accent = when {
        enRuta -> if (isDark) StatusAmberDark else StatusAmber
        vehiculo.activo -> if (isDark) StatusGreenDark else StatusGreen
        else -> if (isDark) StatusRedDark else StatusRed
    }
    val etiquetaEstado = when {
        enRuta -> "En ruta"
        vehiculo.activo -> "Disponible"
        else -> "Mantención"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        tint = accent,
        stripe = accent,
        elevation = 8.dp,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CabeceraTarjetaVehiculo(vehiculo = vehiculo, accent = accent, etiquetaEstado = etiquetaEstado)
            MetadatosTarjetaVehiculo(vehiculo = vehiculo, chequeo = chequeo, accent = accent)
            AccionesTarjetaVehiculo(
                activo = vehiculo.activo,
                enRuta = enRuta,
                accent = accent,
                totalHistorial = totalHistorial,
                onAbrirChequeo = onAbrirChequeo,
                onAbrirHistorial = onAbrirHistorial,
                onToggleActivo = onToggleActivo,
            )
        }
    }
}

@Composable
private fun CabeceraTarjetaVehiculo(vehiculo: Vehiculo, accent: Color, etiquetaEstado: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(accent.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CobreIcons.LocalShipping, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                vehiculo.patente,
                style = MaterialTheme.typography.titleMedium,
                color = brandHeadingColor(),
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                vehiculo.nombreCompleto,
                style = MaterialTheme.typography.bodyMedium,
                color = brandMutedColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        StatusChip(
            label = etiquetaEstado,
            container = accent.copy(alpha = 0.16f),
            content = accent,
        )
    }
}

@Composable
private fun MetadatosTarjetaVehiculo(vehiculo: Vehiculo, chequeo: ChequeoVehiculo?, accent: Color) {
    val isDark = cobreIsDark()
    val infoBg = if (isDark) DarkSurface2.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.55f)
    val infoBorder = if (isDark) DarkOutline else Stone200.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(infoBg)
            .border(1.dp, infoBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(CobreIcons.Speed, contentDescription = null, tint = Brand500, modifier = Modifier.size(16.dp))
                Text(
                    "Odómetro: ${vehiculo.kilometrajeActual} km",
                    style = MaterialTheme.typography.labelMedium,
                    color = brandHeadingColor(),
                    fontWeight = FontWeight.Bold,
                )
            }
            vehiculo.anio?.let { anio ->
                Text(
                    "Año $anio",
                    style = MaterialTheme.typography.labelSmall,
                    color = brandMutedColor(),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        if (chequeo != null && chequeo.estaEnRuta) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(CobreIcons.Person, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
                Text(
                    "En ruta: ${chequeo.responsable} · Salida ${chequeo.horaSalida ?: "En curso"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = accent,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Text(
                text = if (vehiculo.activo) {
                    vehiculo.descripcion?.takeIf { it.isNotBlank() } ?: "Unidad operativa disponible en planta"
                } else {
                    "Unidad fuera de servicio por mantención preventiva"
                },
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AccionesTarjetaVehiculo(
    activo: Boolean,
    enRuta: Boolean,
    accent: Color,
    totalHistorial: Int,
    onAbrirChequeo: () -> Unit,
    onAbrirHistorial: () -> Unit,
    onToggleActivo: () -> Unit,
) {
    val isDark = cobreIsDark()
    val secondaryBg = if (isDark) DarkSurface2 else Color.White.copy(alpha = 0.75f)
    val secondaryBorder = if (isDark) DarkOutline else Stone200

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (activo || enRuta) {
            BotonPrincipalTarjetaVehiculo(
                enRuta = enRuta,
                accent = accent,
                isDark = isDark,
                onClick = onAbrirChequeo,
                modifier = Modifier.weight(1f),
            )
        }
        val historialInteraction = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .height(44.dp)
                .pressableScale(historialInteraction)
                .clip(RoundedCornerShape(12.dp))
                .background(secondaryBg)
                .border(1.dp, secondaryBorder, RoundedCornerShape(12.dp))
                .clickable(interactionSource = historialInteraction, indication = null, onClick = onAbrirHistorial)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(CobreIcons.History, contentDescription = null, tint = Brand500, modifier = Modifier.size(16.dp))
            Text(
                if (totalHistorial > 0) "Bitácora ($totalHistorial)" else "Bitácora",
                style = MaterialTheme.typography.labelMedium,
                color = brandHeadingColor(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
        if (!enRuta) {
            val mantencionInteraction = remember { MutableInteractionSource() }
            val colorTexto = if (activo) brandMutedColor() else (if (isDark) StatusGreenDark else StatusGreen)
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .pressableScale(mantencionInteraction)
                    .clip(RoundedCornerShape(12.dp))
                    .background(secondaryBg)
                    .border(1.dp, secondaryBorder, RoundedCornerShape(12.dp))
                    .clickable(interactionSource = mantencionInteraction, indication = null, onClick = onToggleActivo)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (activo) "Mantención" else "Habilitar",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorTexto,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun BotonPrincipalTarjetaVehiculo(
    enRuta: Boolean,
    accent: Color,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val colorContenido = if (enRuta) accent else Color.White
    Row(
        modifier = modifier
            .height(44.dp)
            .pressableScale(interaction)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (enRuta) {
                    Modifier.background(accent.copy(alpha = if (isDark) 0.22f else 0.15f))
                        .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                } else {
                    Modifier.background(BrandCtaGradient)
                },
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (enRuta) CobreIcons.CheckCircle else CobreIcons.LocalShipping,
            contentDescription = null,
            tint = colorContenido,
            modifier = Modifier.size(17.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            if (enRuta) "Registrar retorno" else "Dar salida",
            style = MaterialTheme.typography.labelLarge,
            color = colorContenido,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

private fun diagnosticarPatenteAlta(patente: String, patentesExistentes: List<String>): String? {
    if (patente.isBlank()) return null
    val normalizada = normalizarPatenteChilena(patente)
    val duplicada = patentesExistentes.any { normalizarPatenteChilena(it) == normalizada }
    return when {
        duplicada -> "Ya existe un vehículo con esa patente ($normalizada)."
        !esPatenteChilenaValida(normalizada) -> "Ingresa una patente chilena válida (ej. HLRT-24 o AB-1234)."
        else -> null
    }
}

/** RF-VE01: modal de alta de un vehículo nuevo a la flota con validación de patente chilena. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalAgregarVehiculo(
    patentesExistentes: List<String>,
    onDismiss: () -> Unit,
    onAgregar: (patente: String, marca: String, modelo: String, anio: Int?, km: Int) -> Unit,
) {
    var patente by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anioTexto by remember { mutableStateOf("") }
    var kmTexto by remember { mutableStateOf("0") }
    val haptic = LocalHapticFeedback.current
    val isDark = cobreIsDark()

    val km = kmTexto.toIntOrNull()
    val anio = anioTexto.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    val anioInvalido = anioTexto.isNotBlank() && (anio == null || anio !in 1980..2100)
    val errorPatente = diagnosticarPatenteAlta(patente, patentesExistentes)
    val habilitado = patente.isNotBlank() && modelo.isNotBlank() &&
        km != null && km >= 0 && errorPatente == null && !anioInvalido

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) DarkSurface1 else Color(0xFFFBF8F5),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Registrar vehículo en flota",
                        style = MaterialTheme.typography.titleLarge,
                        color = brandHeadingColor(),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        "Ingresa los datos técnicos y odómetro inicial de la unidad.",
                        style = MaterialTheme.typography.bodySmall,
                        color = brandMutedColor(),
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(CobreIcons.Close, contentDescription = "Cerrar", tint = brandMutedColor())
                }
            }
            CamposAgregarVehiculo(
                patente = patente,
                marca = marca,
                modelo = modelo,
                anioTexto = anioTexto,
                kmTexto = kmTexto,
                errorPatente = errorPatente,
                anioInvalido = anioInvalido,
                onPatenteChange = { patente = it },
                onMarcaChange = { marca = it },
                onModeloChange = { modelo = it },
                onAnioChange = { anioTexto = it.filter(Char::isDigit).take(4) },
                onKmChange = { kmTexto = it.filter(Char::isDigit) },
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    val kmValido = km ?: return@Button
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAgregar(normalizarPatenteChilena(patente), marca, modelo, anio, kmValido)
                },
                enabled = habilitado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .then(
                        if (habilitado) Modifier.background(BrandCtaGradient, RoundedCornerShape(14.dp))
                        else Modifier.background(Brand500.copy(alpha = 0.30f), RoundedCornerShape(14.dp)),
                    ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
            ) {
                Icon(CobreIcons.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Agregar a la flota",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CamposAgregarVehiculo(
    patente: String,
    marca: String,
    modelo: String,
    anioTexto: String,
    kmTexto: String,
    errorPatente: String?,
    anioInvalido: Boolean,
    onPatenteChange: (String) -> Unit,
    onMarcaChange: (String) -> Unit,
    onModeloChange: (String) -> Unit,
    onAnioChange: (String) -> Unit,
    onKmChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = patente,
        onValueChange = onPatenteChange,
        label = { Text("Patente") },
        placeholder = { Text("Ej. HLRT-24") },
        singleLine = true,
        isError = errorPatente != null,
        supportingText = errorPatente?.let { mensaje -> { Text(mensaje) } },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = cobreFieldColors(),
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = marca,
            onValueChange = onMarcaChange,
            label = { Text("Marca") },
            placeholder = { Text("Ej. Suzuki") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = cobreFieldColors(),
        )
        OutlinedTextField(
            value = modelo,
            onValueChange = onModeloChange,
            label = { Text("Modelo") },
            placeholder = { Text("Ej. APV Cargo") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = cobreFieldColors(),
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = anioTexto,
            onValueChange = onAnioChange,
            label = { Text("Año (opcional)") },
            placeholder = { Text("2022") },
            singleLine = true,
            isError = anioInvalido,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = cobreFieldColors(),
        )
        OutlinedTextField(
            value = kmTexto,
            onValueChange = onKmChange,
            label = { Text("Kilometraje inicial") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(CobreIcons.Speed, contentDescription = null, tint = Brand700, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = cobreFieldColors(),
        )
    }
}

/** RF-VE04: modal con historial de salidas/regresos con kilometraje, fotos y comentarios. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalHistorialVehiculo(
    vehiculo: Vehiculo,
    historial: List<ChequeoVehiculo>,
    onDismiss: () -> Unit,
) {
    val isDark = cobreIsDark()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) DarkSurface1 else Color(0xFFFBF8F5),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Bitácora · ${vehiculo.patente}",
                        style = MaterialTheme.typography.titleLarge,
                        color = brandHeadingColor(),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        "${vehiculo.nombreCompleto} · Odómetro actual: ${vehiculo.kilometrajeActual} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = brandMutedColor(),
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(CobreIcons.Close, contentDescription = "Cerrar", tint = brandMutedColor())
                }
            }
            if (historial.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 4.dp) {
                    Text(
                        "Sin registros de salida o retorno todavía para esta unidad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = brandMutedColor(),
                    )
                }
            } else {
                historial.forEach { chequeo ->
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp,
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        HistorialFila(chequeo)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HistorialFila(chequeo: ChequeoVehiculo) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                chequeo.fecha,
                style = MaterialTheme.typography.labelLarge,
                color = brandHeadingColor(),
                fontWeight = FontWeight.Bold,
            )
            val isDark = cobreIsDark()
            val estadoColor = if (chequeo.estaCerrado) {
                if (isDark) StatusGreenDark else StatusGreen
            } else {
                if (isDark) StatusAmberDark else StatusAmber
            }
            StatusChip(
                label = if (chequeo.estaCerrado) "Cerrado" else "En ruta",
                container = estadoColor.copy(alpha = 0.16f),
                content = estadoColor,
            )
        }
        Text(
            "Salida: ${chequeo.kmSalida ?: "—"} km · ${chequeo.horaSalida ?: "—"} · ${chequeo.estadoSalida.etiqueta}",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
        if (chequeo.estaCerrado) {
            val condicionRetorno = chequeo.estadoEntrada?.etiqueta ?: "Apto"
            Text(
                "Retorno: ${chequeo.kmEntrada} km · ${chequeo.horaEntrada ?: "—"} · " +
                    "${chequeo.kilometrosRecorridos} km recorridos · $condicionRetorno",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
        }
        val totalFotos = chequeo.fotosSalida.size + chequeo.fotosEntrada.size
        if (totalFotos > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(CobreIcons.PhotoCamera, contentDescription = null, tint = Brand500, modifier = Modifier.size(14.dp))
                Text(
                    "$totalFotos foto${if (totalFotos == 1) "" else "s"} de inspección " +
                        "(${chequeo.fotosSalida.size} salida / ${chequeo.fotosEntrada.size} retorno)",
                    style = MaterialTheme.typography.labelSmall,
                    color = brandMutedColor(),
                )
            }
        }
        chequeo.observacionesSalida?.takeIf { it.isNotBlank() }?.let {
            Text(
                "Inspección salida: $it",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor().copy(alpha = 0.85f),
            )
        }
        chequeo.observacionesEntrada?.takeIf { it.isNotBlank() }?.let {
            Text(
                "Inspección retorno: $it",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor().copy(alpha = 0.85f),
            )
        }
        Text(
            "Responsable: ${chequeo.responsable}",
            style = MaterialTheme.typography.labelSmall,
            color = brandHeadingColor().copy(alpha = 0.8f),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun diagnosticarKilometrajeChequeo(kmTexto: String, kmMinimo: Int): String? {
    if (kmTexto.isBlank()) return null
    val km = kmTexto.toIntOrNull() ?: return "Ingresa un kilometraje válido."
    return if (km < kmMinimo) "No puede ser menor al kilometraje anterior registrado ($kmMinimo km)." else null
}

/** RF-VE02 / RF-VE03: modal de registro de salida o retorno con inspección técnica y evidencia fotográfica. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalChequeoVehiculo(
    vehiculo: Vehiculo,
    chequeo: ChequeoVehiculo?,
    conductores: List<ConductorFlota>,
    administradorNombre: String,
    onDismiss: () -> Unit,
    onConfirmar: (
        repartidorId: String?,
        responsable: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit,
) {
    val esEntrada = chequeo != null && chequeo.estaEnRuta
    val isDark = cobreIsDark()
    val accent = if (esEntrada) {
        if (isDark) StatusAmberDark else StatusAmber
    } else {
        if (isDark) StatusGreenDark else StatusGreen
    }
    val kmMinimo = if (esEntrada) chequeo.kmSalida ?: vehiculo.kilometrajeActual else vehiculo.kilometrajeActual

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) DarkSurface1 else Color(0xFFFBF8F5),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (esEntrada) "Registrar Retorno · ${vehiculo.patente}" else "Dar Salida · ${vehiculo.patente}",
                        style = MaterialTheme.typography.titleLarge,
                        color = brandHeadingColor(),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        "${vehiculo.nombreCompleto} · Odómetro mínimo: $kmMinimo km",
                        style = MaterialTheme.typography.bodySmall,
                        color = brandMutedColor(),
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(CobreIcons.Close, contentDescription = "Cerrar", tint = brandMutedColor())
                }
            }
            ChequeoForm(
                accent = accent,
                esEntrada = esEntrada,
                kmMinimo = kmMinimo,
                conductores = conductores,
                responsablePorDefecto = chequeo?.responsable ?: administradorNombre,
                onConfirmar = onConfirmar,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Formulario de salida o entrada con conductor, condición técnica, odómetro y evidencia fotográfica. */
@Composable
private fun ChequeoForm(
    accent: Color,
    esEntrada: Boolean,
    kmMinimo: Int,
    conductores: List<ConductorFlota>,
    responsablePorDefecto: String,
    onConfirmar: (
        repartidorId: String?,
        responsable: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit,
) {
    val conductoresHabilitados = remember(conductores) {
        conductores.filter { esRolConductorORepartidor(it.rol) }
    }
    var kmTexto by remember { mutableStateOf(kmMinimo.toString()) }
    var observaciones by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf(EstadoCondicionVehiculo.APTO) }
    var conductorSeleccionado by remember(conductoresHabilitados) {
        mutableStateOf(conductoresHabilitados.firstOrNull())
    }
    val fotos = remember { mutableStateOf(listOf<FotoChequeo>()) }
    val haptic = LocalHapticFeedback.current

    val km = kmTexto.toIntOrNull()
    val errorKm = diagnosticarKilometrajeChequeo(kmTexto, kmMinimo)
    val bloqueadoPorNoApto = !esEntrada && estadoSeleccionado == EstadoCondicionVehiculo.NO_APTO
    val habilitado = km != null && km >= kmMinimo && !bloqueadoPorNoApto

    Column(modifier = Modifier.padding(top = 12.dp)) {
        if (!esEntrada) {
            SelectorConductorFlota(
                conductores = conductoresHabilitados,
                seleccionado = conductorSeleccionado,
                onSeleccionar = { conductorSeleccionado = it },
            )
            Spacer(Modifier.height(12.dp))
        }

        SelectorCondicionVehiculo(
            esEntrada = esEntrada,
            seleccionado = estadoSeleccionado,
            onSeleccionar = { estadoSeleccionado = it },
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = kmTexto,
            onValueChange = { kmTexto = it.filter(Char::isDigit) },
            label = { Text(if (esEntrada) "Kilometraje de retorno" else "Kilometraje de salida") },
            placeholder = { Text("Mínimo $kmMinimo") },
            singleLine = true,
            isError = errorKm != null,
            supportingText = errorKm?.let { mensaje -> { Text(mensaje) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(CobreIcons.Speed, contentDescription = null, tint = Brand700, modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = cobreFieldColors(),
        )

        Spacer(Modifier.height(12.dp))
        SelectorFotos(accent, fotos, haptic)

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones (opcional)") },
            placeholder = { Text("Ej. Rayón leve en parachoques trasero") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = cobreFieldColors(),
        )

        Spacer(Modifier.height(14.dp))
        BotonConfirmarChequeo(
            accent = accent,
            esEntrada = esEntrada,
            habilitado = habilitado,
            onClick = {
                val kmValido = km ?: return@BotonConfirmarChequeo
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                val nombreResponsable = conductorSeleccionado?.nombreCompleto ?: responsablePorDefecto
                onConfirmar(
                    conductorSeleccionado?.id,
                    nombreResponsable,
                    kmValido,
                    estadoSeleccionado,
                    fotos.value,
                    observaciones.trim().takeIf { it.isNotEmpty() },
                )
            },
        )
    }
}

@Composable
private fun BotonConfirmarChequeo(
    accent: Color,
    esEntrada: Boolean,
    habilitado: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .then(
                if (habilitado) Modifier.background(BrandCtaGradient, RoundedCornerShape(14.dp))
                else Modifier.background(accent.copy(alpha = 0.30f), RoundedCornerShape(14.dp)),
            ),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
    ) {
        Icon(CobreIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            if (esEntrada) "Confirmar retorno" else "Confirmar salida",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SelectorConductorFlota(
    conductores: List<ConductorFlota>,
    seleccionado: ConductorFlota?,
    onSeleccionar: (ConductorFlota) -> Unit,
) {
    Text("Conductor o repartidor responsable", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
    Spacer(Modifier.height(6.dp))
    if (conductores.isEmpty()) {
        Text(
            "No hay usuarios con rol conductor o repartidor disponibles.",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(conductores, key = { it.id }) { conductor ->
                SelectableChip(
                    label = "${conductor.nombreCompleto} · ${conductor.rol.uppercase()}",
                    seleccionado = seleccionado?.id == conductor.id,
                    onClick = { onSeleccionar(conductor) },
                )
            }
        }
    }
}

@Composable
private fun SelectorCondicionVehiculo(
    esEntrada: Boolean,
    seleccionado: EstadoCondicionVehiculo,
    onSeleccionar: (EstadoCondicionVehiculo) -> Unit,
) {
    val isDark = cobreIsDark()
    Text(
        if (esEntrada) "Estado técnico al retorno" else "Estado técnico de salida",
        style = MaterialTheme.typography.titleSmall,
        color = brandHeadingColor(),
    )
    Spacer(Modifier.height(6.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoCondicionVehiculo.entries.forEach { opcion ->
            val activo = seleccionado == opcion
            val colorOpcion = when (opcion) {
                EstadoCondicionVehiculo.APTO -> if (isDark) StatusGreenDark else StatusGreen
                EstadoCondicionVehiculo.CON_OBSERVACIONES -> if (isDark) StatusAmberDark else StatusAmber
                EstadoCondicionVehiculo.NO_APTO -> if (isDark) StatusRedDark else StatusRed
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activo) colorOpcion else colorOpcion.copy(alpha = 0.12f))
                    .clickable { onSeleccionar(opcion) }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
            ) {
                Text(
                    opcion.etiqueta,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (activo) Color.White else colorOpcion,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
    if (!esEntrada && seleccionado == EstadoCondicionVehiculo.NO_APTO) {
        val rojo = if (isDark) StatusRedDark else StatusRed
        Spacer(Modifier.height(6.dp))
        Text(
            "Un vehículo calificado como NO APTO no puede autorizarse para salir a ruta.",
            style = MaterialTheme.typography.labelSmall,
            color = rojo,
        )
    }
}

@Composable
private fun SelectorFotos(accent: Color, fotos: MutableState<List<FotoChequeo>>, haptic: HapticFeedback) {
    val abrirCamara = rememberCapturadorFoto { uriCapturada ->
        val orden = fotos.value.size + 1
        val etiqueta = ETIQUETAS_FOTOS.getOrElse(orden - 1) { "Evidencia $orden" }
        fotos.value = fotos.value + FotoChequeo(
            id = "F-$orden-${uriCapturada.hashCode()}",
            uri = uriCapturada,
            descripcion = etiqueta,
            orden = orden,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Evidencia fotográfica (${fotos.value.size})",
            style = MaterialTheme.typography.titleSmall,
            color = brandHeadingColor(),
        )
        TextButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                abrirCamara()
            },
        ) {
            Icon(CobreIcons.PhotoCamera, contentDescription = null, tint = Brand500, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Tomar foto", color = Brand500, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(Modifier.height(6.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(fotos.value, key = { it.id }) { foto ->
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.14f))
                    .clickable { fotos.value = fotos.value.filterNot { it.id == foto.id } }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MiniaturaFotoChequeo(
                    uri = foto.uri,
                    descripcion = foto.descripcion,
                    tint = accent,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    foto.descripcion ?: "Foto ${foto.orden}",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                )
            }
        }
        item {
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .pressableScale(interaction)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brand500.copy(alpha = 0.10f))
                    .clickable(interactionSource = interaction, indication = null) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        abrirCamara()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(CobreIcons.PhotoCamera, contentDescription = "Tomar foto", tint = Brand500, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text("Cámara", style = MaterialTheme.typography.labelSmall, color = Brand500, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
