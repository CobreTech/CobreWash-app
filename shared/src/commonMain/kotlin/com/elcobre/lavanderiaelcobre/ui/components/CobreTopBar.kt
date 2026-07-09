package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/**
 * Tarjeta de identidad compartida por los roles Operario y Administrador: avatar
 * (slot, cada rol tiene el suyo), nombre/subtítulo y botón de cerrar sesión.
 */
@Composable
fun CobreTopBar(
    nombre: String,
    subtitulo: String,
    onCerrarSesion: () -> Unit,
    avatar: @Composable () -> Unit,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            avatar()
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, style = MaterialTheme.typography.titleLarge, color = brandHeadingColor())
                Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = brandMutedColor())
            }
            CerrarSesionChip(onCerrarSesion)
        }
    }
}

@Composable
private fun CerrarSesionChip(onCerrarSesion: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val isDark = cobreIsDark()
    val accent = if (isDark) Brand400 else Copper700
    Row(
        modifier = Modifier
            .pressableScale(interaction)
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = if (isDark) 0.18f else 0.12f))
            .clickable(interactionSource = interaction, indication = null) { onCerrarSesion() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(CobreIcons.Logout, contentDescription = "Cerrar sesión", tint = accent, modifier = Modifier.size(18.dp))
        Text("Salir", style = MaterialTheme.typography.labelMedium, color = accent, fontWeight = FontWeight.SemiBold)
    }
}
