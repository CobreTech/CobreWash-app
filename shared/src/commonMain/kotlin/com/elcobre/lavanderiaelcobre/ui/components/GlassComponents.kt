package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.ui.theme.BackgroundWarm
import com.elcobre.lavanderiaelcobre.ui.theme.Brand100
import com.elcobre.lavanderiaelcobre.ui.theme.Brand200
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand50
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.Copper200
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.DarkBg
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurface
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurfaceMuted
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface1
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.ForegroundStone
import com.elcobre.lavanderiaelcobre.ui.theme.Neutral50
import com.elcobre.lavanderiaelcobre.ui.theme.Sky200
import com.elcobre.lavanderiaelcobre.ui.theme.Sky500
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.TextSecondaryLight
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/** Escala sutil al presionar; usa el mismo InteractionSource que el clickable del elemento. */
@Composable
fun Modifier.pressableScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "pressScale",
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Fondo de pantalla con gradiente de marca y blobs de color desenfocados, replica los
 * valores de `app/intranet/layout.tsx`. Se monta una sola vez en la raíz de la app
 * ([com.elcobre.lavanderiaelcobre.ui.CobreApp]), detrás de la navegación: si se
 * recreara en cada pantalla (como antes), el blur —costoso— se recalcularía en cada
 * cambio de destino y el fondo se deslizaría junto con la transición, dando la
 * sensación de que toda la pantalla se recarga en vez de solo su contenido.
 */
@Composable
fun CobreBackdrop(modifier: Modifier = Modifier) {
    val isDark = cobreIsDark()
    val bgGradient = if (isDark) {
        Brush.verticalGradient(colors = listOf(DarkBg, DarkBg, DarkSurface1))
    } else {
        Brush.verticalGradient(
            colors = listOf(BackgroundWarm, Brand100.copy(alpha = 0.16f), BackgroundWarm),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient),
    ) {
        // Los 3 blobs se mantienen en ambos temas (solo baja su opacidad en oscuro)
        // para que el fondo oscuro no quede plano.
        val brandBlobAlpha = if (isDark) 0.10f else 0.25f
        val copperBlobAlpha = if (isDark) 0.10f else 0.28f
        val skyBlobAlpha = if (isDark) 0.05f else 0.12f
        val brandBlobColor = if (isDark) Brand500 else Brand200
        val copperBlobColor = if (isDark) Copper600 else Copper200
        val skyBlobColor = if (isDark) Sky500 else Sky200

        Box(
            modifier = Modifier
                .size(360.dp)
                .graphicsLayer { translationX = 140f; translationY = -160f }
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(copperBlobColor.copy(alpha = copperBlobAlpha), Color.Transparent),
                    ),
                    shape = RoundedCornerShape(percent = 50),
                ),
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer { translationX = -60f; translationY = 160f }
                .blur(110.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(brandBlobColor.copy(alpha = brandBlobAlpha), Color.Transparent),
                    ),
                    shape = RoundedCornerShape(percent = 50),
                ),
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterStart)
                .graphicsLayer { translationX = -100f; translationY = -120f }
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(skyBlobColor.copy(alpha = skyBlobAlpha), Color.Transparent),
                    ),
                    shape = RoundedCornerShape(percent = 50),
                ),
        )
    }
}

/**
 * Contenedor de contenido de pantalla: solo aplica el inset de `systemBars` para que
 * nada quede tapado por las barras de Android. El fondo visual lo pinta [CobreBackdrop]
 * una sola vez en la raíz; este composable ya no lo dibuja para no recrearlo por pantalla.
 */
@Composable
fun CobreBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        content()
    }
}

/** Tarjeta con efecto glassmorphism: fondo translúcido, borde fino y sombra con tinte de marca. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp), // rounded-2xl de la web, radio por defecto de sus tarjetas
    contentPadding: PaddingValues = PaddingValues(20.dp),
    elevation: Dp = 18.dp,
    tint: Color = Brand500,
    stripe: Color? = null,
    content: @Composable () -> Unit,
) {
    val isDark = cobreIsDark()

    // En oscuro, superficie sólida en vez de blanco translúcido: la translucidez
    // hacía ver el modo oscuro lavado. remember evita reconstruir estos Brush en
    // cada recomposición (relevante cuando hay varias GlassCard en una lista).
    val bgColor = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(colors = listOf(Color(0xFF212127), DarkSurface1))
        } else {
            Brush.linearGradient(colors = listOf(Color.White, Neutral50.copy(alpha = 0.94f)))
        }
    }

    val borderBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.10f), DarkOutline.copy(alpha = 0.6f)))
        } else {
            Brush.linearGradient(colors = listOf(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.4f)))
        }
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isDark) (elevation / 2f) else elevation,
                shape = shape,
                ambientColor = if (isDark) Color.Black else Brand700.copy(alpha = 0.08f),
                spotColor = if (isDark) Color.Black else tint.copy(alpha = 0.14f),
            )
            .clip(shape)
            .background(bgColor)
            .then(
                if (stripe != null) {
                    Modifier.drawBehind {
                        drawRect(color = stripe, size = Size(5.dp.toPx(), size.height))
                    }
                } else {
                    Modifier
                },
            )
            .border(width = if (isDark) 1.dp else 0.5.dp, brush = borderBrush, shape = shape)
            .padding(contentPadding),
    ) {
        content()
    }
}

/** Colores unificados para los campos de texto (login, búsqueda, comentario). */
@Composable
fun cobreFieldColors(): TextFieldColors {
    val isDark = cobreIsDark()
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = if (isDark) Brand400 else Brand500,
        unfocusedBorderColor = if (isDark) DarkOutline else Brand500.copy(alpha = 0.35f),
        focusedLabelColor = if (isDark) Brand400 else Brand700,
        unfocusedLabelColor = if (isDark) DarkOnSurfaceMuted else Brand700.copy(alpha = 0.75f),
        cursorColor = if (isDark) Brand400 else Copper600,
        focusedContainerColor = if (isDark) DarkSurface2 else Color.White.copy(alpha = 0.5f),
        unfocusedContainerColor = if (isDark) DarkSurface1 else Color.White.copy(alpha = 0.35f),
        focusedTextColor = if (isDark) DarkOnSurface else ForegroundStone,
        unfocusedTextColor = if (isDark) DarkOnSurface else ForegroundStone,
        focusedPlaceholderColor = if (isDark) DarkOnSurfaceMuted else Brand700.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = if (isDark) DarkOnSurfaceMuted else Brand700.copy(alpha = 0.6f),
    )
}

/** Marca un elemento como compartido entre pantallas para la transición animada; no-op si no hay scopes disponibles. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedTag(
    key: String,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?,
): Modifier = if (sharedScope != null && animatedScope != null) {
    with(sharedScope) {
        this@sharedTag.sharedElement(
            rememberSharedContentState(key),
            animatedVisibilityScope = animatedScope,
        )
    }
} else {
    this
}

/** Barra de progreso segmentada del [flujo]: segmentos hasta la etapa actual en [accent], el resto atenuados. */
@Composable
fun StageProgress(
    flujo: List<EtapaProceso>,
    etapaActual: EtapaProceso,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val isDark = cobreIsDark()
    val vacio = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
    val indiceActual = flujo.indexOf(etapaActual)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        flujo.forEachIndexed { indice, _ ->
            val objetivo = if (indice <= indiceActual) accent else vacio
            val color by animateColorAsState(objetivo, tween(500), label = "segmento")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color),
            )
        }
    }
}

/** Timeline vertical del proceso productivo, compartido entre DetalleScreen y el Dashboard. */
@Composable
fun EtapasTimeline(flujo: List<EtapaProceso>, indiceActual: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        flujo.forEachIndexed { index, etapa ->
            EtapaTimelineRow(
                etapa = etapa,
                completada = index < indiceActual,
                activa = index == indiceActual,
                esUltimo = index == flujo.size - 1,
            )
        }
    }
}

/** Color del nodo por estado (no por tipo de etapa), igual que el timeline de la web: completada = verde, activa = marca. */
@Composable
private fun etapaEstadoAccent(completada: Boolean, activa: Boolean): Color? {
    val isDark = cobreIsDark()
    return when {
        completada -> if (isDark) StatusGreenDark else StatusGreen
        activa -> if (isDark) Brand400 else Brand500
        else -> null
    }
}

private fun etapaEstadoLabel(completada: Boolean, activa: Boolean): String = when {
    completada -> "Completada"
    activa -> "En curso"
    else -> "Pendiente"
}

@Composable
private fun EtapaTimelineRow(
    etapa: EtapaProceso,
    completada: Boolean,
    activa: Boolean,
    esUltimo: Boolean,
) {
    val isDark = cobreIsDark()
    val estadoAccent = etapaEstadoAccent(completada, activa)
    val activo = completada || activa
    val inactiveCircle = if (isDark) DarkSurface2 else Color.White.copy(alpha = 0.6f)
    val inactiveContent = if (isDark) DarkOnSurfaceMuted else Brand700.copy(alpha = 0.55f)
    val lineTrack = if (isDark) DarkOutline else Brand700.copy(alpha = 0.15f)

    Row(modifier = Modifier.fillMaxWidth()) {
        EtapaNodo(
            etapa = etapa,
            completada = completada,
            activa = activa,
            esUltimo = esUltimo,
            activo = activo,
            estadoAccent = estadoAccent,
            inactiveCircle = inactiveCircle,
            inactiveContent = inactiveContent,
            lineTrack = lineTrack,
        )
        Spacer(Modifier.width(14.dp))
        EtapaTexto(
            etapa = etapa,
            completada = completada,
            activa = activa,
            esUltimo = esUltimo,
            activo = activo,
            estadoAccent = estadoAccent,
            inactiveContent = inactiveContent,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun EtapaNodo(
    etapa: EtapaProceso,
    completada: Boolean,
    activa: Boolean,
    esUltimo: Boolean,
    activo: Boolean,
    estadoAccent: Color?,
    inactiveCircle: Color,
    inactiveContent: Color,
    lineTrack: Color,
) {
    val isDark = cobreIsDark()
    val circleColor by animateColorAsState(estadoAccent ?: inactiveCircle, tween(500), label = "nodeColor")
    val iconTint by animateColorAsState(if (activo) Color.White else inactiveContent, tween(500), label = "iconTint")
    val lineColor by animateColorAsState(
        targetValue = if (completada) (if (isDark) StatusGreenDark else StatusGreen) else lineTrack,
        animationSpec = tween(500),
        label = "lineColor",
    )
    val scale by animateFloatAsState(if (activa) 1.1f else 1f, tween(400), label = "nodeScale")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            if (activa) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background((estadoAccent ?: inactiveCircle).copy(alpha = 0.22f)),
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(RoundedCornerShape(50))
                    .background(circleColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (completada) CobreIcons.Check else etapaIcon(etapa),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        if (!esUltimo) {
            Box(modifier = Modifier.width(2.dp).height(36.dp).background(lineColor))
        }
    }
}

@Composable
private fun EtapaTexto(
    etapa: EtapaProceso,
    completada: Boolean,
    activa: Boolean,
    esUltimo: Boolean,
    activo: Boolean,
    estadoAccent: Color?,
    inactiveContent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 6.dp, bottom = if (esUltimo) 0.dp else 20.dp),
    ) {
        Text(
            etapa.displayName,
            style = MaterialTheme.typography.titleSmall,
            color = if (activo) brandHeadingColor() else inactiveContent,
            fontWeight = if (activa) FontWeight.Bold else FontWeight.Normal,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            etapaEstadoLabel(completada, activa),
            style = MaterialTheme.typography.labelSmall,
            color = estadoAccent ?: brandMutedColor(),
            fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

/** Chip/badge compacto con icono opcional, usado para etapa y prioridad. */
@Composable
fun StatusChip(
    label: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .border(1.dp, content.copy(alpha = 0.35f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** Chip de filtro/selección (no de estado): esquinas `rounded-xl` de la web, no píldora. Usado en Dashboard e Insumos. */
@Composable
fun SelectableChip(label: String, seleccionado: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val isDark = cobreIsDark()
    val fondo = when {
        seleccionado -> Brand500
        isDark -> DarkSurface2
        else -> Color.White
    }
    val texto = when {
        seleccionado -> Color.White
        isDark -> DarkOnSurface
        else -> TextSecondaryLight
    }
    val borde = when {
        seleccionado -> Color.Transparent
        isDark -> DarkOutline
        else -> Brand200
    }
    Row(
        modifier = Modifier
            .pressableScale(interaction)
            .clip(RoundedCornerShape(12.dp))
            .background(fondo)
            .border(1.dp, borde, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = texto, fontWeight = FontWeight.SemiBold)
    }
}
