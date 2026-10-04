package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elcobre.lavanderiaelcobre.ui.Pantalla
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOnSurfaceMuted
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface1
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.Stone200
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

private data class SidebarNavItem(
    val icon: ImageVector,
    val label: String,
    val pantalla: Pantalla,
    val badge: Int? = null,
)

/**
 * Sidebar lateral para tablets (ancho >= 600dp), fiel al diseño web de `CobreWash-web/components/intranet/Sidebar.tsx`.
 */
@Composable
fun WebSidebar(
    tabActual: Pantalla,
    onSeleccionarTab: (Pantalla) -> Unit,
    onEscanearQR: () -> Unit,
    comandasActivas: Int,
    avisosCount: Int,
    alertasStockCount: Int,
    operarioNombre: String,
    operarioRol: String,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = cobreIsDark()
    val bgColor = if (isDark) DarkSurface1.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.92f)
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.06f) else Stone200.copy(alpha = 0.8f)

    val items = listOf(
        SidebarNavItem(CobreIcons.LocalLaundry, "Pedidos", Pantalla.Dashboard, if (comandasActivas > 0) comandasActivas else null),
        SidebarNavItem(CobreIcons.Campaign, "Avisos", Pantalla.Avisos, if (avisosCount > 0) avisosCount else null),
        SidebarNavItem(CobreIcons.Inventory, "Inventario", Pantalla.Insumos, if (alertasStockCount > 0) alertasStockCount else null),
        SidebarNavItem(CobreIcons.Person, "Configuración", Pantalla.Configuracion),
    )

    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(bgColor)
            .drawBehind {
                drawLine(dividerColor, start = Offset(size.width, 0f), end = Offset(size.width, size.height))
            }
            .padding(horizontal = 14.dp, vertical = 18.dp),
    ) {
        // Logo de la marca
        SidebarBrandHeader()

        Spacer(Modifier.height(20.dp))

        // Botón CTA para escanear QR en planta
        SidebarQrCta(onClick = onEscanearQR)

        Spacer(Modifier.height(18.dp))

        Text(
            "MENÚ PRINCIPAL",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            ),
            color = if (isDark) DarkOnSurfaceMuted else Color.Gray,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )

        // Navegación principal
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEach { item ->
                SidebarNavButton(
                    item = item,
                    seleccionado = tabActual == item.pantalla,
                    onClick = { onSeleccionarTab(item.pantalla) },
                )
            }
        }

        // Mini-tarjeta de notificaciones/alertas (espeja Sidebar.tsx de la web)
        if (comandasActivas > 0 || alertasStockCount > 0) {
            SidebarAlertPreview(
                totalAlertas = comandasActivas + alertasStockCount,
                onClick = { onSeleccionarTab(Pantalla.Dashboard) },
            )
            Spacer(Modifier.height(12.dp))
        }

        // Perfil y cierre de sesión
        SidebarUserProfile(
            nombre = operarioNombre,
            rol = operarioRol,
            onCerrarSesion = onCerrarSesion,
        )
    }
}

@Composable
private fun SidebarBrandHeader() {
    val isDark = cobreIsDark()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Logo El Cobre",
            modifier = Modifier.size(36.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                "Lavandería",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = brandMutedColor(),
                lineHeight = 12.sp,
            )
            Text(
                "El Cobre",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (isDark) Brand400 else Brand500,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun SidebarQrCta(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressableScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(BrandCtaGradient)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(CobreIcons.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            "Escanear Comanda",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
    }
}

@Composable
private fun SidebarNavButton(
    item: SidebarNavItem,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    val isDark = cobreIsDark()
    val interaction = remember { MutableInteractionSource() }
    val textColor = when {
        seleccionado -> if (isDark) Brand400 else Brand500
        isDark -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> Color(0xFF44403C)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pressableScale(interaction, pressedScale = 0.98f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (seleccionado) Brand500.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Indicador vertical anaranjado en el extremo izquierdo (idéntico al sidebar web)
            if (seleccionado) {
                Box(
                    modifier = Modifier
                        .size(width = 3.dp, height = 20.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Brand500),
                )
                Spacer(Modifier.width(9.dp))
            } else {
                Spacer(Modifier.width(12.dp))
            }

            Icon(
                item.icon,
                contentDescription = item.label,
                tint = textColor,
                modifier = Modifier.size(19.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                item.label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.SemiBold,
                ),
                color = textColor,
                modifier = Modifier.weight(1f),
            )
            if (item.badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Brand500)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${item.badge}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        ),
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarAlertPreview(totalAlertas: Int, onClick: () -> Unit) {
    val isDark = cobreIsDark()
    val interaction = remember { MutableInteractionSource() }
    val cardBg = if (isDark) DarkSurface2 else Color(0xFFFBF8F5)
    val cardBorder = if (isDark) DarkOutline else Stone200.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pressableScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(11.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brand500.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(CobreIcons.Notifications, contentDescription = null, tint = Brand500, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(7.dp))
                Text(
                    "Operaciones",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = brandHeadingColor(),
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brand500)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    "$totalAlertas",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    color = Color.White,
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(
            "Comandas activas en proceso",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = brandMutedColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SidebarUserProfile(
    nombre: String,
    rol: String,
    onCerrarSesion: () -> Unit,
) {
    val isDark = cobreIsDark()
    val borderColor = if (isDark) Color.White.copy(alpha = 0.08f) else Stone200.copy(alpha = 0.7f)
    val logoutColor = if (isDark) StatusRedDark else StatusRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(borderColor, start = Offset(0f, 0f), end = Offset(size.width, 0f))
            }
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(BrandCtaGradient, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                nombre.trim().take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                nombre,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = brandHeadingColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                rol,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = brandMutedColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(
            onClick = onCerrarSesion,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                CobreIcons.Logout,
                contentDescription = "Cerrar sesión",
                tint = logoutColor,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

/**
 * Barra superior persistente para la tablet (idéntica a `CobreWash-web/components/intranet/Topbar.tsx`).
 */
@Composable
fun WebTopbar(
    usuarioNombre: String,
    usuarioRol: String,
    modoTema: ModoTema,
    onToggleTema: () -> Unit,
    searchQuery: String = "",
    onSearchChange: (String) -> Unit = {},
    showSearch: Boolean = true,
    alertasCount: Int = 0,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val isDark = when (modoTema) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.SISTEMA -> cobreIsDark()
    }
    val bgColor = if (isDark) DarkSurface1.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f)
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.06f) else Stone200.copy(alpha = 0.8f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .drawBehind {
                drawLine(dividerColor, start = Offset(0f, size.height), end = Offset(size.width, size.height))
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (onMenuClick != null) {
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .pressableScale(interaction)
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) DarkSurface2 else Stone200.copy(alpha = 0.5f))
                    .clickable(interactionSource = interaction, indication = null, onClick = onMenuClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    CobreIcons.Menu,
                    contentDescription = "Alternar Menú",
                    tint = if (isDark) DarkOnSurfaceMuted else Brand500,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        
        if (showSearch) {
            // Buscador global rápido
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text("Buscar comandas, clientes...", style = MaterialTheme.typography.bodySmall)
                },
                leadingIcon = {
                    Icon(CobreIcons.Search, contentDescription = null, tint = Brand500, modifier = Modifier.size(18.dp))
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(CobreIcons.Close, contentDescription = "Limpiar", modifier = Modifier.size(16.dp))
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = cobreFieldColors(),
                modifier = Modifier
                    .width(320.dp)
                    .height(44.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        // Botón rápido para alternar tema Claro ☀️ / Oscuro 🌙
        TopbarIconButton(
            icon = if (isDark) CobreIcons.Sun else CobreIcons.Moon,
            contentDescription = "Cambiar tema",
            onClick = onToggleTema,
        )

        // Botón de notificaciones con badge de alertas activas
        Box(contentAlignment = Alignment.TopEnd) {
            TopbarIconButton(
                icon = CobreIcons.Notifications,
                contentDescription = "Notificaciones",
                onClick = {},
            )
            if (alertasCount > 0) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Brand500),
                )
            }
        }

        // Chip de identidad del usuario activo (exacto al Navbar web)
        TopbarUserChip(
            usuarioNombre = usuarioNombre,
            usuarioRol = usuarioRol,
            isDark = isDark,
        )
    }
}

@Composable
private fun TopbarUserChip(
    usuarioNombre: String,
    usuarioRol: String,
    isDark: Boolean,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) DarkSurface2 else Color(0xFFF9F7F5))
            .border(1.dp, if (isDark) DarkOutline else Stone200.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                usuarioNombre,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = brandHeadingColor(),
            )
            Text(
                usuarioRol,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = brandMutedColor(),
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BrandCtaGradient, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                usuarioNombre.trim().take(1).uppercase(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun TopbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val isDark = cobreIsDark()
    val interaction = remember { MutableInteractionSource() }
    val btnBg = if (isDark) DarkSurface2 else Color.White
    val borderCol = if (isDark) DarkOutline else Stone200.copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .size(40.dp)
            .pressableScale(interaction)
            .clip(RoundedCornerShape(12.dp))
            .background(btnBg)
            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = if (isDark) Brand400 else Brand500,
            modifier = Modifier.size(18.dp),
        )
    }
}
