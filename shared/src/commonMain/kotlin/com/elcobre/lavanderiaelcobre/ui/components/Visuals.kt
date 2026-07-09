package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.Prioridad
import com.elcobre.lavanderiaelcobre.data.model.SeveridadStock
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand600
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.Copper500
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurface
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurfaceMuted
import com.elcobre.lavanderiaelcobre.ui.theme.ForegroundStone
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusBlue
import com.elcobre.lavanderiaelcobre.ui.theme.StatusBlueDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRose
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRoseDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusSlate
import com.elcobre.lavanderiaelcobre.ui.theme.StatusSlateDark
import com.elcobre.lavanderiaelcobre.ui.theme.TextSecondaryLight
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/** Color de título/encabezado: en claro es carboncillo piedra, no cobre, para que el cobre quede reservado a acentos (CTAs, logo). */
@Composable
fun brandHeadingColor(): Color =
    if (cobreIsDark()) DarkOnSurface else ForegroundStone

/** Color de texto secundario/de soporte (gris cálido en claro), AA en ambos temas. */
@Composable
fun brandMutedColor(): Color =
    if (cobreIsDark()) DarkOnSurfaceMuted else TextSecondaryLight

/** Espeja `.bg-gradient-brand` del CSS de la web. */
val BrandCtaGradient: Brush = Brush.linearGradient(listOf(Brand500, Copper500))

/** Espeja `.text-gradient` del CSS de la web; para títulos destacados en tema claro. */
val BrandTextGradient: Brush = Brush.linearGradient(listOf(Brand600, Copper500, Copper700))

/**
 * Color resuelto de un estado (etapa, prioridad, alerta), ya adaptado al tema
 * actual: [accent] para íconos/indicadores, [container] para el fondo del
 * chip, [content] para texto legible sobre [container].
 */
data class ChipColors(
    val container: Color,
    val content: Color,
    val accent: Color,
)

/** Deriva un [ChipColors] coherente a partir de un matiz base para claro/oscuro. */
@Composable
private fun statusColors(light: Color, dark: Color): ChipColors {
    val isDark = cobreIsDark()
    return if (isDark) {
        ChipColors(
            container = dark.copy(alpha = 0.22f),
            content = dark,
            accent = dark,
        )
    } else {
        ChipColors(
            container = light.copy(alpha = 0.14f),
            content = lerp(light, Color.Black, 0.28f), // más oscuro => texto AA sobre el tint
            accent = light,
        )
    }
}

/**
 * Color por estado, igual que `estadoConfig` de la web (`lib/mock/comandas.ts`):
 * marca mientras está en curso, verde solo en la etapa final ("Listo"). No varía
 * por tipo de etapa, a diferencia de una versión anterior de este archivo.
 */
@Composable
fun etapaColors(etapa: EtapaProceso): ChipColors = when (etapa) {
    EtapaProceso.ENTREGA, EtapaProceso.ENTREGA_DOMICILIO -> statusColors(StatusGreen, StatusGreenDark)
    else -> statusColors(Brand500, Brand400)
}

/** Ícono del tipo de comanda (tienda vs. domicilio). */
fun tipoIcon(tipo: TipoComanda): ImageVector = when (tipo) {
    TipoComanda.EN_TIENDA -> CobreIcons.Inbox
    TipoComanda.DOMICILIO -> CobreIcons.LocalShipping
}

fun etapaIcon(etapa: EtapaProceso): ImageVector = when (etapa) {
    EtapaProceso.RETIRO -> CobreIcons.LocalShipping
    EtapaProceso.RECEPCION -> CobreIcons.Inbox
    EtapaProceso.RECEPCION_PLANTA -> CobreIcons.Inbox
    EtapaProceso.LAVADO -> CobreIcons.LocalLaundry
    EtapaProceso.SECADO -> CobreIcons.Air
    EtapaProceso.PLANCHADO -> CobreIcons.Checkroom
    EtapaProceso.ENTREGA -> CobreIcons.DoneAll
    EtapaProceso.ENTREGA_DOMICILIO -> CobreIcons.LocalShipping
}

@Composable
fun prioridadColors(prioridad: Prioridad): ChipColors = when (prioridad) {
    Prioridad.ALTA -> statusColors(StatusRed, StatusRedDark)
    Prioridad.MEDIA -> statusColors(StatusAmber, StatusAmberDark)
    Prioridad.BAJA -> statusColors(StatusSlate, StatusSlateDark)
}

/** Color de la severidad de una alerta de stock (ámbar = bajo, rojo = agotado). */
@Composable
fun severidadColors(severidad: SeveridadStock): ChipColors = when (severidad) {
    SeveridadStock.BAJO -> statusColors(StatusAmber, StatusAmberDark)
    SeveridadStock.AGOTADO -> statusColors(StatusRed, StatusRedDark)
}

/** Color de acento de una alerta, ya adaptado al tema. */
@Composable
fun alertaColor(tipo: TipoAlerta): Color {
    val isDark = cobreIsDark()
    return when (tipo) {
        TipoAlerta.RETRASO -> if (isDark) StatusAmberDark else StatusAmber
        TipoAlerta.INCIDENCIA -> if (isDark) StatusRedDark else StatusRed
        TipoAlerta.INSUMO_CRITICO -> if (isDark) StatusRoseDark else StatusRose
    }
}

fun alertaIcon(tipo: TipoAlerta): ImageVector = when (tipo) {
    TipoAlerta.RETRASO -> CobreIcons.Schedule
    TipoAlerta.INCIDENCIA -> CobreIcons.Warning
    TipoAlerta.INSUMO_CRITICO -> CobreIcons.Inventory
}

fun eventoIcon(tipo: TipoEvento): ImageVector = when (tipo) {
    TipoEvento.CAMBIO_ETAPA -> CobreIcons.CheckCircle
    TipoEvento.COMENTARIO -> CobreIcons.Comment
    TipoEvento.ALERTA -> CobreIcons.Warning
    TipoEvento.ALERTA_RESUELTA -> CobreIcons.Check
}

/** Color de acento de un evento del historial, adaptado al tema. */
@Composable
fun eventoColor(tipo: TipoEvento): Color {
    val isDark = cobreIsDark()
    return when (tipo) {
        TipoEvento.CAMBIO_ETAPA -> if (isDark) StatusBlueDark else StatusBlue
        TipoEvento.COMENTARIO -> if (isDark) StatusSlateDark else StatusSlate
        TipoEvento.ALERTA -> if (isDark) StatusRedDark else StatusRed
        TipoEvento.ALERTA_RESUELTA -> if (isDark) StatusGreenDark else StatusGreen
    }
}
