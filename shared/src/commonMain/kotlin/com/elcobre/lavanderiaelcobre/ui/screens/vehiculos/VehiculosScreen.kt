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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.elcobre.lavanderiaelcobre.data.model.normalizarPatenteChilena
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTopBar
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.StatusChip
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import kotlinx.coroutines.delay

private val ETIQUETAS_FOTOS = listOf("Frente", "Lateral derecho", "Lateral izquierdo", "Posterior", "Tablero / Odómetro")

/**
 * Vista raíz del rol Administrador conectada a Firebase Data Connect (`RF-VE01` a `RF-VE04`):
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
    var panelAbierto by remember { mutableStateOf<String?>(null) }
    var mostrandoHistorial by remember { mutableStateOf(false) }
    var mostrandoAlta by remember { mutableStateOf(false) }

    LaunchedEffect(mensajeExito) {
        if (mensajeExito != null) {
            delay(3500)
            onLimpiarMensajes()
        }
    }

    fun alternarPanel(vehiculoId: String, historial: Boolean) {
        if (panelAbierto == vehiculoId && mostrandoHistorial == historial) {
            panelAbierto = null
        } else {
            panelAbierto = vehiculoId
            mostrandoHistorial = historial
        }
    }

    CobreBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBarAdmin(administrador = administrador, onCerrarSesion = onCerrarSesion)
            CabeceraFlota(
                kpis = kpis,
                mostrandoAlta = mostrandoAlta,
                cargando = cargando || operacionEnCurso,
                onRefrescar = onRefrescar,
                onToggleAlta = { mostrandoAlta = !mostrandoAlta },
            )
            BannersEstadoFlota(error = error, mensajeExito = mensajeExito, onLimpiar = onLimpiarMensajes)
            AnimatedVisibility(visible = mostrandoAlta) {
                AgregarVehiculoCard(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    patentesExistentes = vehiculos.map { it.patente },
                    onAgregar = { patente, marca, modelo, anio, km ->
                        onRegistrarVehiculoDetallado(patente, marca, modelo, anio, km)
                        mostrandoAlta = false
                    },
                )
            }
            BarraFiltroFlota(
                busqueda = busqueda,
                filtroActual = filtroActual,
                kpis = kpis,
                onCambiarBusqueda = onCambiarBusqueda,
                onSeleccionarFiltro = onSeleccionarFiltro,
            )
            GrillaVehiculosFlota(
                vehiculos = vehiculos,
                conductores = conductores,
                administradorNombre = administrador.nombre,
                cargando = cargando,
                panelAbierto = panelAbierto,
                mostrandoHistorial = mostrandoHistorial,
                chequeoEnRuta = chequeoEnRuta,
                historialVehiculo = historialVehiculo,
                onAlternarPanel = ::alternarPanel,
                onCambiarEstadoVehiculo = onCambiarEstadoVehiculo,
                onConfirmarChequeo = { vehiculo, chequeo, repartidorId, responsable, km, estado, fotos, obs ->
                    if (chequeo != null && chequeo.estaEnRuta) {
                        onRegistrarEntradaDetallada(vehiculo.id, km, estado, fotos, obs)
                    } else {
                        onRegistrarSalidaDetallada(vehiculo.id, repartidorId, responsable, km, estado, fotos, obs)
                    }
                    panelAbierto = null
                },
            )
        }
    }
}

@Composable
private fun CabeceraFlota(
    kpis: KpisFlota,
    mostrandoAlta: Boolean,
    cargando: Boolean,
    onRefrescar: () -> Unit,
    onToggleAlta: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Flota de Reparto", style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
            Spacer(Modifier.height(2.dp))
            Text(
                "${kpis.total} unidades · ${kpis.disponibles} disponibles · ${kpis.enRuta} en ruta",
                style = MaterialTheme.typography.bodyMedium,
                color = brandMutedColor(),
            )
        }
        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Brand400, strokeWidth = 2.5.dp)
            Spacer(Modifier.width(10.dp))
        } else {
            TextButton(onClick = onRefrescar) {
                Text("Actualizar", color = Brand500, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(6.dp))
        }
        AgregarVehiculoBoton(abierto = mostrandoAlta, onClick = onToggleAlta)
    }
}

@Composable
private fun BannersEstadoFlota(error: String?, mensajeExito: String?, onLimpiar: () -> Unit) {
    val isDark = cobreIsDark()
    AnimatedVisibility(visible = error != null) {
        val rojo = if (isDark) StatusRedDark else StatusRed
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp)
                .background(rojo.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = rojo, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(error.orEmpty(), style = MaterialTheme.typography.bodySmall, color = rojo, modifier = Modifier.weight(1f))
            TextButton(onClick = onLimpiar) { Text("Cerrar", color = rojo) }
        }
    }
    AnimatedVisibility(visible = mensajeExito != null) {
        val verde = if (isDark) StatusGreenDark else StatusGreen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp)
                .background(verde.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(CobreIcons.CheckCircle, contentDescription = null, tint = verde, modifier = Modifier.size(18.dp))
            Text(mensajeExito.orEmpty(), style = MaterialTheme.typography.labelMedium, color = verde)
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
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onCambiarBusqueda,
            placeholder = { Text("Buscar por patente, marca, modelo o conductor...") },
            singleLine = true,
            leadingIcon = { Icon(CobreIcons.Search, contentDescription = null, tint = Brand700, modifier = Modifier.size(18.dp)) },
            trailingIcon = if (busqueda.isNotEmpty()) {
                {
                    Icon(
                        CobreIcons.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = brandMutedColor(),
                        modifier = Modifier.size(18.dp).clickable { onCambiarBusqueda("") },
                    )
                }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = cobreFieldColors(),
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FiltroFlota.entries) { opcion ->
                val cantidad = when (opcion) {
                    FiltroFlota.TODOS -> kpis.total
                    FiltroFlota.DISPONIBLES -> kpis.disponibles
                    FiltroFlota.EN_RUTA -> kpis.enRuta
                    FiltroFlota.INACTIVOS -> kpis.inactivos
                }
                val activo = filtroActual == opcion
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activo) Brand500 else Brand500.copy(alpha = 0.10f))
                        .clickable { onSeleccionarFiltro(opcion) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                ) {
                    Text(
                        "${opcion.etiqueta} ($cantidad)",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (activo) Color.White else Brand500,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun GrillaVehiculosFlota(
    vehiculos: List<Vehiculo>,
    conductores: List<ConductorFlota>,
    administradorNombre: String,
    cargando: Boolean,
    panelAbierto: String?,
    mostrandoHistorial: Boolean,
    chequeoEnRuta: (String) -> ChequeoVehiculo?,
    historialVehiculo: (String) -> List<ChequeoVehiculo>,
    onAlternarPanel: (String, Boolean) -> Unit,
    onCambiarEstadoVehiculo: (String, Boolean) -> Unit,
    onConfirmarChequeo: (
        Vehiculo,
        ChequeoVehiculo?,
        String?,
        String,
        Int,
        EstadoCondicionVehiculo,
        List<FotoChequeo>,
        String?,
    ) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (vehiculos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                if (cargando) {
                    CircularProgressIndicator(color = Brand400)
                } else {
                    Text(
                        "No hay vehículos que coincidan con el filtro seleccionado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = brandMutedColor(),
                    )
                }
            }
        } else {
            val columnas = if (maxWidth >= 720.dp) GridCells.Adaptive(minSize = 340.dp) else GridCells.Fixed(1)
            LazyVerticalGrid(
                columns = columnas,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(vehiculos, key = { it.id }) { vehiculo ->
                    val chequeo = chequeoEnRuta(vehiculo.id)
                    VehiculoCard(
                        vehiculo = vehiculo,
                        chequeo = chequeo,
                        historial = historialVehiculo(vehiculo.id),
                        conductores = conductores,
                        administradorNombre = administradorNombre,
                        panelChequeoAbierto = panelAbierto == vehiculo.id && !mostrandoHistorial,
                        panelHistorialAbierto = panelAbierto == vehiculo.id && mostrandoHistorial,
                        onToggleChequeo = { onAlternarPanel(vehiculo.id, false) },
                        onToggleHistorial = { onAlternarPanel(vehiculo.id, true) },
                        onToggleActivo = { onCambiarEstadoVehiculo(vehiculo.id, !vehiculo.activo) },
                        onConfirmar = { repartidorId, responsable, km, estado, fotos, obs ->
                            onConfirmarChequeo(vehiculo, chequeo, repartidorId, responsable, km, estado, fotos, obs)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AgregarVehiculoBoton(abierto: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(46.dp)
            .pressableScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(Brand500.copy(alpha = 0.12f))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (abierto) CobreIcons.Close else CobreIcons.Add,
            contentDescription = if (abierto) "Cerrar formulario" else "Agregar vehículo",
            tint = Brand500,
            modifier = Modifier.size(22.dp),
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

/** RF-VE01: alta de un vehículo nuevo a la flota con validación de patente chilena. */
@Composable
private fun AgregarVehiculoCard(
    modifier: Modifier = Modifier,
    patentesExistentes: List<String>,
    onAgregar: (patente: String, marca: String, modelo: String, anio: Int?, km: Int) -> Unit,
) {
    var patente by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anioTexto by remember { mutableStateOf("") }
    var kmTexto by remember { mutableStateOf("0") }
    val haptic = LocalHapticFeedback.current

    val km = kmTexto.toIntOrNull()
    val anio = anioTexto.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    val anioInvalido = anioTexto.isNotBlank() && (anio == null || anio !in 1980..2100)
    val errorPatente = diagnosticarPatenteAlta(patente, patentesExistentes)
    val habilitado = patente.isNotBlank() && modelo.isNotBlank() &&
        km != null && km >= 0 && errorPatente == null && !anioInvalido

    GlassCard(modifier = modifier.fillMaxWidth(), elevation = 10.dp) {
        Column {
            Text("Registrar vehículo en flota", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(10.dp))
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
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val kmValido = km ?: return@Button
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAgregar(normalizarPatenteChilena(patente), marca, modelo, anio, kmValido)
                    patente = ""
                    marca = ""
                    modelo = ""
                    anioTexto = ""
                    kmTexto = "0"
                },
                enabled = habilitado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .then(
                        if (habilitado) Modifier.background(BrandCtaGradient, RoundedCornerShape(14.dp))
                        else Modifier.background(Brand500.copy(alpha = 0.30f), RoundedCornerShape(14.dp)),
                    ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
            ) {
                Text("Agregar a la flota", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
            }
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
    Spacer(Modifier.height(8.dp))
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
    Spacer(Modifier.height(8.dp))
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

@Composable
private fun TopBarAdmin(administrador: Administrador, onCerrarSesion: () -> Unit) {
    CobreTopBar(
        nombre = administrador.nombre,
        subtitulo = administrador.rol,
        onCerrarSesion = onCerrarSesion,
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(BrandCtaGradient, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CobreIcons.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun VehiculoCard(
    vehiculo: Vehiculo,
    chequeo: ChequeoVehiculo?,
    historial: List<ChequeoVehiculo>,
    conductores: List<ConductorFlota>,
    administradorNombre: String,
    panelChequeoAbierto: Boolean,
    panelHistorialAbierto: Boolean,
    onToggleChequeo: () -> Unit,
    onToggleHistorial: () -> Unit,
    onToggleActivo: () -> Unit,
    onConfirmar: (
        repartidorId: String?,
        responsable: String,
        km: Int,
        estado: EstadoCondicionVehiculo,
        fotos: List<FotoChequeo>,
        observaciones: String?,
    ) -> Unit,
) {
    val enRuta = chequeo != null && chequeo.estaEnRuta
    val isDark = cobreIsDark()
    val accent = when {
        enRuta -> if (isDark) StatusAmberDark else StatusAmber
        vehiculo.activo -> if (isDark) StatusGreenDark else StatusGreen
        else -> brandMutedColor()
    }
    val etiquetaEstado = when {
        enRuta -> "En ruta"
        vehiculo.activo -> "Disponible"
        else -> "Mantención"
    }

    GlassCard(modifier = Modifier.fillMaxWidth(), tint = accent, elevation = 12.dp) {
        Column {
            CabeceraTarjetaVehiculo(vehiculo = vehiculo, accent = accent, etiquetaEstado = etiquetaEstado)
            Spacer(Modifier.height(10.dp))
            MetadatosTarjetaVehiculo(vehiculo = vehiculo, chequeo = chequeo)
            Spacer(Modifier.height(14.dp))
            AccionesTarjetaVehiculo(
                activo = vehiculo.activo,
                enRuta = enRuta,
                accent = accent,
                totalHistorial = historial.size,
                onToggleChequeo = onToggleChequeo,
                onToggleHistorial = onToggleHistorial,
                onToggleActivo = onToggleActivo,
            )
            AnimatedVisibility(visible = panelChequeoAbierto) {
                ChequeoForm(
                    accent = accent,
                    esEntrada = enRuta,
                    kmMinimo = if (enRuta) chequeo.kmSalida ?: vehiculo.kilometrajeActual else vehiculo.kilometrajeActual,
                    conductores = conductores,
                    responsablePorDefecto = chequeo?.responsable ?: administradorNombre,
                    onConfirmar = onConfirmar,
                )
            }
            AnimatedVisibility(visible = panelHistorialAbierto) {
                HistorialVehiculo(historial)
            }
        }
    }
}

@Composable
private fun CabeceraTarjetaVehiculo(vehiculo: Vehiculo, accent: Color, etiquetaEstado: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(accent.copy(alpha = 0.16f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(CobreIcons.LocalShipping, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(vehiculo.patente, style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
            Text(vehiculo.nombreCompleto, style = MaterialTheme.typography.bodyMedium, color = brandMutedColor())
        }
        StatusChip(
            label = etiquetaEstado,
            container = accent.copy(alpha = 0.16f),
            content = accent,
        )
    }
}

@Composable
private fun MetadatosTarjetaVehiculo(vehiculo: Vehiculo, chequeo: ChequeoVehiculo?) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(CobreIcons.Speed, contentDescription = null, tint = brandMutedColor(), modifier = Modifier.size(15.dp))
            Text("${vehiculo.kilometrajeActual} km", style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
        }
        if (chequeo != null && chequeo.estaEnRuta) {
            Text(
                "· Conductor: ${chequeo.responsable} (${chequeo.horaSalida ?: "En curso"})",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor(),
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
    onToggleChequeo: () -> Unit,
    onToggleHistorial: () -> Unit,
    onToggleActivo: () -> Unit,
) {
    val isDark = cobreIsDark()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (activo || enRuta) {
            val interaction = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .pressableScale(interaction)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = if (isDark) 0.18f else 0.12f))
                    .clickable(interactionSource = interaction, indication = null) { onToggleChequeo() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (enRuta) "Registrar entrada" else "Dar salida",
                    style = MaterialTheme.typography.labelLarge,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        val historialInteraction = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .pressableScale(historialInteraction)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .clickable(interactionSource = historialInteraction, indication = null) { onToggleHistorial() }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(CobreIcons.History, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Text(
                if (totalHistorial > 0) "Historial ($totalHistorial)" else "Historial",
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
        }
        if (!enRuta) {
            val mantencionInteraction = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .pressableScale(mantencionInteraction)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, brandMutedColor().copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .clickable(interactionSource = mantencionInteraction, indication = null) { onToggleActivo() }
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (activo) "Mantención" else "Habilitar",
                    style = MaterialTheme.typography.labelMedium,
                    color = brandMutedColor(),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/** RF-VE04: historial de salidas/regresos con kilometraje, fotos y comentarios de cada evento. */
@Composable
private fun HistorialVehiculo(historial: List<ChequeoVehiculo>) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(brandMutedColor().copy(alpha = 0.12f)))
        Spacer(Modifier.height(14.dp))
        if (historial.isEmpty()) {
            Text(
                "Sin registros de salida o regreso todavía.",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
        } else {
            historial.forEachIndexed { index, chequeo ->
                HistorialFila(chequeo)
                if (index < historial.lastIndex) {
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(brandMutedColor().copy(alpha = 0.08f)))
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun HistorialFila(chequeo: ChequeoVehiculo) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(chequeo.fecha, style = MaterialTheme.typography.labelMedium, color = brandHeadingColor(), fontWeight = FontWeight.Bold)
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
        Spacer(Modifier.height(6.dp))
        Text(
            "Salida: ${chequeo.kmSalida ?: "—"} km · ${chequeo.horaSalida ?: "—"} · ${chequeo.estadoSalida.etiqueta}",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
        if (chequeo.estaCerrado) {
            val condicionRetorno = chequeo.estadoEntrada?.etiqueta ?: "Apto"
            Text(
                "Entrada: ${chequeo.kmEntrada} km · ${chequeo.horaEntrada ?: "—"} · " +
                    "${chequeo.kilometrosRecorridos} km recorridos · $condicionRetorno",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
        }
        val totalFotos = chequeo.fotosSalida.size + chequeo.fotosEntrada.size
        if (totalFotos > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(CobreIcons.PhotoCamera, contentDescription = null, tint = brandMutedColor(), modifier = Modifier.size(13.dp))
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
            "· Responsable: ${chequeo.responsable}",
            style = MaterialTheme.typography.labelSmall,
            color = brandMutedColor().copy(alpha = 0.7f),
        )
    }
}

private fun diagnosticarKilometrajeChequeo(kmTexto: String, kmMinimo: Int): String? {
    if (kmTexto.isBlank()) return null
    val km = kmTexto.toIntOrNull() ?: return "Ingresa un kilometraje válido."
    return if (km < kmMinimo) "No puede ser menor al kilometraje anterior registrado ($kmMinimo km)." else null
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
    var kmTexto by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf(EstadoCondicionVehiculo.APTO) }
    var conductorSeleccionado by remember(conductores) { mutableStateOf(conductores.firstOrNull()) }
    val fotos = remember { mutableStateOf(listOf<FotoChequeo>()) }
    val haptic = LocalHapticFeedback.current

    val km = kmTexto.toIntOrNull()
    val errorKm = diagnosticarKilometrajeChequeo(kmTexto, kmMinimo)
    val bloqueadoPorNoApto = !esEntrada && estadoSeleccionado == EstadoCondicionVehiculo.NO_APTO
    val habilitado = km != null && km >= kmMinimo && !bloqueadoPorNoApto

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(brandMutedColor().copy(alpha = 0.12f)))
        Spacer(Modifier.height(14.dp))

        if (!esEntrada && conductores.isNotEmpty()) {
            SelectorConductorFlota(
                conductores = conductores,
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
            label = { Text(if (esEntrada) "Kilometraje de entrada" else "Kilometraje de salida") },
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
            if (esEntrada) "Confirmar entrada" else "Confirmar salida",
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
    Text("Conductor responsable", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
    Spacer(Modifier.height(6.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(conductores, key = { it.id }) { conductor ->
            val activo = seleccionado?.id == conductor.id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activo) Brand500 else Brand500.copy(alpha = 0.10f))
                    .clickable { onSeleccionar(conductor) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Text(
                    conductor.nombreCompleto,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (activo) Color.White else Brand500,
                    fontWeight = FontWeight.SemiBold,
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
                    .padding(horizontal = 12.dp, vertical = 7.dp),
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
    Text(
        "Evidencia fotográfica (${fotos.value.size})",
        style = MaterialTheme.typography.titleSmall,
        color = brandHeadingColor(),
    )
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(fotos.value, key = { it.id }) { foto ->
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.14f))
                    .clickable { fotos.value = fotos.value.filterNot { it.id == foto.id } }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(CobreIcons.PhotoCamera, contentDescription = foto.descripcion, tint = accent, modifier = Modifier.size(22.dp))
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
                    .size(64.dp)
                    .pressableScale(interaction)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brand500.copy(alpha = 0.10f))
                    .clickable(interactionSource = interaction, indication = null) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val orden = fotos.value.size + 1
                        val etiqueta = ETIQUETAS_FOTOS.getOrElse(orden - 1) { "Evidencia $orden" }
                        fotos.value = fotos.value + FotoChequeo(
                            id = "F$orden",
                            uri = "vehiculos/evidencia/inspeccion-foto-$orden.jpg",
                            descripcion = etiqueta,
                            orden = orden,
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(CobreIcons.Add, contentDescription = "Tomar foto", tint = Brand500, modifier = Modifier.size(24.dp))
            }
        }
    }
}
