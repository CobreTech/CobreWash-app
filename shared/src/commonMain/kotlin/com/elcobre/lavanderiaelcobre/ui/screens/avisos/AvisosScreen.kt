package com.elcobre.lavanderiaelcobre.ui.screens.avisos

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.theme.Brand100
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/** Feed de solo lectura de avisos de administración; destino de navegación propio del operario. */
@Composable
fun AvisosScreen(
    avisos: List<Aviso>,
    applySystemBarsInsets: Boolean = true,
    cargando: Boolean = false,
    error: String? = null,
    hayMas: Boolean = false,
    onActualizar: () -> Unit = {},
    onMas: () -> Unit = {},
) {
    CobreBackground(applySystemBarsInsets = applySystemBarsInsets) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Avisos", style = MaterialTheme.typography.headlineMedium, color = brandHeadingColor())
                Text(
                    "Comunicados de administración para el equipo de planta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandMutedColor(),
                )
            }
            EstadoAvisos(cargando, error, onActualizar)
            if (avisos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        when {
                            cargando -> "Cargando avisos…"
                            error != null -> "Los avisos no están disponibles por ahora."
                            else -> "No hay avisos por ahora."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = brandMutedColor(),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(avisos, key = { it.id }) { aviso -> AvisoCard(aviso) }
                    if (hayMas) item {
                        TextButton(onClick = onMas, enabled = !cargando, modifier = Modifier.fillMaxWidth()) {
                            Text("Cargar más avisos")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoAvisos(cargando: Boolean, error: String?, onActualizar: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        if (cargando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        TextButton(onClick = onActualizar, enabled = !cargando) {
            Text(if (error == null) "Actualizar" else "Reintentar")
        }
    }
}

@Composable
private fun AvisoCard(aviso: Aviso) {
    val isDark = cobreIsDark()
    val accent = if (isDark) Brand400 else Copper700
    // Marca (naranja), como el badge "Fijado" de la web; el ámbar queda reservado
    // para alertas de comanda estancada.
    val destacadoColor = if (isDark) Brand400 else Brand500
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 8.dp, tint = accent) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .background(Brand100.copy(alpha = if (isDark) 0.16f else 1f), RoundedCornerShape(14.dp))
                    .padding(10.dp),
            ) {
                Icon(CobreIcons.Campaign, contentDescription = null, tint = accent)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        aviso.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        color = brandHeadingColor(),
                        modifier = Modifier.weight(1f),
                    )
                    if (aviso.destacado) {
                        Row(
                            modifier = Modifier
                                .background(destacadoColor.copy(alpha = 0.18f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Icon(
                                CobreIcons.PushPin,
                                contentDescription = null,
                                tint = destacadoColor,
                                modifier = Modifier.size(11.dp),
                            )
                            Text(
                                "Fijado",
                                style = MaterialTheme.typography.labelSmall,
                                color = destacadoColor,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    aviso.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                )
                Spacer(Modifier.height(8.dp))
                if (aviso.autor.isNotBlank()) {
                    Text("Publicado por ${aviso.autor}", style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(CobreIcons.Person, contentDescription = null, tint = brandMutedColor(), modifier = Modifier.size(13.dp))
                    Text(aviso.dirigidoA, style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
                    Spacer(Modifier.weight(1f))
                    Text(aviso.hora, style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
                }
            }
        }
    }
}
