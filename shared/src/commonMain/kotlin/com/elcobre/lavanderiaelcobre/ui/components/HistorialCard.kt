package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700

/**
 * Tarjeta con la cronología de eventos del pedido (avances, comentarios, alertas).
 * Compartida entre DetalleScreen y el panel derecho de la tablet en DashboardScreen.
 */
@Composable
fun HistorialCard(pedido: Pedido, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth(), elevation = 8.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(CobreIcons.History, contentDescription = null, tint = brandHeadingColor(), modifier = Modifier.size(20.dp))
                Text("Historial de avances y comentarios", style = MaterialTheme.typography.titleMedium, color = brandHeadingColor())
            }
            Spacer(Modifier.height(14.dp))
            if (pedido.historial.isEmpty()) {
                Text(
                    "Sin eventos registrados todavía.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandMutedColor().copy(alpha = 0.85f),
                )
            }
            pedido.historial.forEachIndexed { index, evento ->
                EventoFila(
                    tipo = evento.tipo,
                    titulo = evento.titulo,
                    detalle = evento.detalle,
                    hora = evento.hora,
                    autor = evento.autor,
                    esUltimo = index == pedido.historial.size - 1,
                )
            }
        }
    }
}

@Composable
fun EventoFila(
    tipo: TipoEvento,
    titulo: String,
    detalle: String,
    hora: String,
    autor: String,
    esUltimo: Boolean,
) {
    val color = eventoColor(tipo)
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(eventoIcon(tipo), contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            if (!esUltimo) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(26.dp)
                        .background(Brand700.copy(alpha = 0.12f)),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f).padding(bottom = if (esUltimo) 0.dp else 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(titulo, style = MaterialTheme.typography.titleSmall, color = brandHeadingColor(), modifier = Modifier.weight(1f))
                Text(hora, style = MaterialTheme.typography.labelSmall, color = brandMutedColor())
            }
            Spacer(Modifier.height(2.dp))
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f))
            Text("· $autor", style = MaterialTheme.typography.labelSmall, color = brandMutedColor().copy(alpha = 0.8f))
        }
    }
}
