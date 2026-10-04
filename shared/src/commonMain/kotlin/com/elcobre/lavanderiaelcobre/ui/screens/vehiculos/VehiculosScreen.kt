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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
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
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import kotlinx.coroutines.delay

/**
 * Vista raíz del rol Administrador: mantiene la flota (RF-VE01), da la salida y
 * recibe la entrada de los vehículos de reparto con kilometraje y evidencia
 * fotográfica (RF-VE02/03), y permite consultar el historial de cada uno
 * (RF-VE04). El botón "Tomar foto" de [ChequeoForm] genera una [FotoChequeo]
 * mock; la integración con la cámara real del tablet no está implementada aún.
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
) {
    var panelAbierto by remember { mutableStateOf<String?>(null) }
    var mostrandoHistorial by remember { mutableStateOf(false) }
    var mostrandoAlta by remember { mutableStateOf(false) }
    var vehiculoAgregado by remember { mutableStateOf(false) }

    LaunchedEffect(vehiculoAgregado) {
        if (vehiculoAgregado) {
            delay(2000)
            vehiculoAgregado = false
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Flota de Reparto", style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${vehiculos.size} vehículos registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = brandMutedColor(),
                    )
                }
                AgregarVehiculoBoton(abierto = mostrandoAlta, onClick = { mostrandoAlta = !mostrandoAlta })
            }

            AnimatedVisibility(visible = vehiculoAgregado) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val verde = if (cobreIsDark()) StatusGreenDark else StatusGreen
                    Icon(CobreIcons.CheckCircle, contentDescription = null, tint = verde, modifier = Modifier.size(16.dp))
                    Text("Vehículo agregado a la flota.", style = MaterialTheme.typography.labelMedium, color = verde)
                }
            }

            AnimatedVisibility(visible = mostrandoAlta) {
                AgregarVehiculoCard(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    patentesExistentes = vehiculos.map { it.patente },
                    onAgregar = { patente, modelo, km ->
                        onAgregarVehiculo(patente, modelo, km)
                        vehiculoAgregado = true
                        mostrandoAlta = false
                    },
                )
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val columnas = if (maxWidth >= 720.dp) GridCells.Adaptive(minSize = 340.dp) else GridCells.Fixed(1)
                LazyVerticalGrid(
                    columns = columnas,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(vehiculos, key = { it.id }) { vehiculo ->
                        val chequeo = chequeoEnRuta(vehiculo.id)
                        VehiculoCard(
                            vehiculo = vehiculo,
                            chequeo = chequeo,
                            historial = historialVehiculo(vehiculo.id),
                            panelChequeoAbierto = panelAbierto == vehiculo.id && !mostrandoHistorial,
                            panelHistorialAbierto = panelAbierto == vehiculo.id && mostrandoHistorial,
                            onToggleChequeo = { alternarPanel(vehiculo.id, historial = false) },
                            onToggleHistorial = { alternarPanel(vehiculo.id, historial = true) },
                            onConfirmar = { km, fotos, observaciones ->
                                if (chequeo != null && chequeo.estaEnRuta) {
                                    onRegistrarEntrada(vehiculo.id, km, fotos, observaciones)
                                } else {
                                    onRegistrarSalida(vehiculo.id, km, fotos, observaciones)
                                }
                                panelAbierto = null
                            },
                        )
                    }
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

/** RF-VE01: alta de un vehículo nuevo a la flota. */
@Composable
private fun AgregarVehiculoCard(
    modifier: Modifier = Modifier,
    patentesExistentes: List<String>,
    onAgregar: (patente: String, modelo: String, km: Int) -> Unit,
) {
    var patente by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var kmTexto by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current
    val km = kmTexto.toIntOrNull()

    val patenteNormalizada = patente.trim().uppercase()
    val patenteDuplicada = patenteNormalizada.isNotBlank() &&
        patentesExistentes.any { it.equals(patenteNormalizada, ignoreCase = true) }
    val patenteCorta = patente.isNotBlank() && patenteNormalizada.length < 5
    val patenteInvalida = patenteCorta || patenteDuplicada
    val errorPatente = when {
        patenteDuplicada -> "Ya existe un vehículo con esa patente."
        patenteCorta -> "Ingresa una patente válida (mínimo 5 caracteres)."
        else -> null
    }
    val habilitado = patente.isNotBlank() && modelo.isNotBlank() && km != null && km >= 0 && !patenteInvalida

    GlassCard(modifier = modifier.fillMaxWidth(), elevation = 10.dp) {
        Column {
            Text("Agregar vehículo", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = patente,
                onValueChange = { patente = it },
                label = { Text("Patente") },
                placeholder = { Text("Ej. HLRT-24") },
                singleLine = true,
                isError = patenteInvalida,
                supportingText = errorPatente?.let { mensaje -> { Text(mensaje) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = cobreFieldColors(),
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = { Text("Modelo") },
                placeholder = { Text("Ej. Suzuki APV 2019") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = cobreFieldColors(),
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = kmTexto,
                onValueChange = { kmTexto = it.filter(Char::isDigit) },
                label = { Text("Kilometraje inicial") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(CobreIcons.Speed, contentDescription = null, tint = Brand700, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = cobreFieldColors(),
            )
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = {
                    val kmValido = km ?: return@Button
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAgregar(patente, modelo, kmValido)
                    patente = ""
                    modelo = ""
                    kmTexto = ""
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
    panelChequeoAbierto: Boolean,
    panelHistorialAbierto: Boolean,
    onToggleChequeo: () -> Unit,
    onToggleHistorial: () -> Unit,
    onConfirmar: (km: Int, fotos: List<FotoChequeo>, observaciones: String?) -> Unit,
) {
    val enRuta = chequeo != null && chequeo.estaEnRuta
    val isDark = cobreIsDark()
    val accent = if (enRuta) (if (isDark) StatusAmberDark else StatusAmber) else (if (isDark) StatusGreenDark else StatusGreen)

    GlassCard(modifier = Modifier.fillMaxWidth(), tint = accent, elevation = 12.dp) {
        Column {
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
                    Text(vehiculo.modelo, style = MaterialTheme.typography.bodyMedium, color = brandMutedColor())
                }
                StatusChip(
                    label = if (enRuta) "En ruta" else "Disponible",
                    container = accent.copy(alpha = 0.16f),
                    content = accent,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(CobreIcons.Speed, contentDescription = null, tint = brandMutedColor(), modifier = Modifier.size(15.dp))
                Text(
                    "${vehiculo.kilometrajeActual} km",
                    style = MaterialTheme.typography.labelSmall,
                    color = brandMutedColor(),
                )
            }

            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

                val historialInteraction = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .pressableScale(historialInteraction)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable(interactionSource = historialInteraction, indication = null) { onToggleHistorial() }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(CobreIcons.History, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                    Text("Historial", style = MaterialTheme.typography.labelLarge, color = accent, fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(visible = panelChequeoAbierto) {
                ChequeoForm(
                    accent = accent,
                    esEntrada = enRuta,
                    kmMinimo = if (enRuta) chequeo.kmSalida ?: vehiculo.kilometrajeActual else vehiculo.kilometrajeActual,
                    onConfirmar = onConfirmar,
                )
            }

            AnimatedVisibility(visible = panelHistorialAbierto) {
                HistorialVehiculo(historial)
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
            "Salida: ${chequeo.kmSalida ?: "—"} km · ${chequeo.horaSalida ?: "—"}",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
        if (chequeo.estaCerrado) {
            Text(
                "Entrada: ${chequeo.kmEntrada} km · ${chequeo.horaEntrada} · ${chequeo.kilometrosRecorridos} km recorridos",
                style = MaterialTheme.typography.bodySmall,
                color = brandMutedColor(),
            )
        }
        val totalFotos = chequeo.fotosSalida.size + chequeo.fotosEntrada.size
        if (totalFotos > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(CobreIcons.PhotoCamera, contentDescription = null, tint = brandMutedColor(), modifier = Modifier.size(13.dp))
                Text(
                    "$totalFotos foto${if (totalFotos == 1) "" else "s"} adjunta${if (totalFotos == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = brandMutedColor(),
                )
            }
        }
        chequeo.observacionesSalida?.takeIf { it.isNotBlank() }?.let {
            Text("Salida: $it", style = MaterialTheme.typography.labelSmall, color = brandMutedColor().copy(alpha = 0.85f))
        }
        chequeo.observacionesEntrada?.takeIf { it.isNotBlank() }?.let {
            Text("Entrada: $it", style = MaterialTheme.typography.labelSmall, color = brandMutedColor().copy(alpha = 0.85f))
        }
        Text("· ${chequeo.responsable}", style = MaterialTheme.typography.labelSmall, color = brandMutedColor().copy(alpha = 0.7f))
    }
}

/** Formulario de salida o entrada; `esEntrada` cambia el copy y valida contra el km de salida ya registrado. */
@Composable
private fun ChequeoForm(
    accent: Color,
    esEntrada: Boolean,
    kmMinimo: Int,
    onConfirmar: (km: Int, fotos: List<FotoChequeo>, observaciones: String?) -> Unit,
) {
    var kmTexto by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    val fotos = remember { mutableStateOf(listOf<FotoChequeo>()) }
    val haptic = LocalHapticFeedback.current

    val km = kmTexto.toIntOrNull()
    val tocado = kmTexto.isNotBlank()
    val kmInvalido = tocado && (km == null || km < kmMinimo)
    val habilitado = km != null && km >= kmMinimo
    val errorKm = when {
        !kmInvalido -> null
        km == null -> "Ingresa un kilometraje válido."
        else -> "No puede ser menor al kilometraje anterior registrado ($kmMinimo)."
    }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(brandMutedColor().copy(alpha = 0.12f)))
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = kmTexto,
            onValueChange = { kmTexto = it.filter(Char::isDigit) },
            label = { Text(if (esEntrada) "Kilometraje de entrada" else "Kilometraje de salida") },
            placeholder = { Text("Mínimo $kmMinimo") },
            singleLine = true,
            isError = kmInvalido,
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
        Button(
            onClick = {
                val kmValido = km ?: return@Button
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onConfirmar(kmValido, fotos.value, observaciones.trim().takeIf { it.isNotEmpty() })
            },
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
}

@Composable
private fun SelectorFotos(accent: Color, fotos: MutableState<List<FotoChequeo>>, haptic: HapticFeedback) {
    Text("Fotos del vehículo", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(fotos.value, key = { it.id }) { foto ->
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(CobreIcons.PhotoCamera, contentDescription = foto.descripcion, tint = accent, modifier = Modifier.size(24.dp))
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
                        fotos.value = fotos.value + FotoChequeo(
                            id = "F${fotos.value.size + 1}",
                            uri = "mock://vehiculo/foto-${fotos.value.size + 1}.jpg",
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(CobreIcons.Add, contentDescription = "Tomar foto", tint = Brand500, modifier = Modifier.size(24.dp))
            }
        }
    }
}
