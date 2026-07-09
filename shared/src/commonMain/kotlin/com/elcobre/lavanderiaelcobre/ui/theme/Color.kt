package com.elcobre.lavanderiaelcobre.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de marca, extraída tal cual del CSS de la landing page.

// brand — Copper Orange (primario)
val Brand50 = Color(0xFFFFF7ED)
val Brand100 = Color(0xFFFFEDD5)
val Brand200 = Color(0xFFFED7AA)
val Brand300 = Color(0xFFFDBA74)
val Brand400 = Color(0xFFFB923C)
val Brand500 = Color(0xFFF97316) // primario
val Brand600 = Color(0xFFEA580C)
val Brand700 = Color(0xFFC2410C)
val Brand800 = Color(0xFF9A3412)
val Brand900 = Color(0xFF7C2D12)

// copper — Pure Copper (secundario / acento)
val Copper50 = Color(0xFFFDF8F5)
val Copper100 = Color(0xFFFBEEE6)
val Copper200 = Color(0xFFF6D8C6)
val Copper300 = Color(0xFFEEA883)
val Copper400 = Color(0xFFE37643)
val Copper500 = Color(0xFFDB541A) // acento
val Copper600 = Color(0xFFCC4114)
val Copper700 = Color(0xFFA93011)
val Copper800 = Color(0xFF882713)
val Copper900 = Color(0xFF6E2112)

// neutrales
val Neutral50 = Color(0xFFFCFCFC)
val Neutral100 = Color(0xFFF3F4F6)
val Neutral800 = Color(0xFF1F2937)
val Neutral900 = Color(0xFF111827)

// Escala `stone` de Tailwind, portada con los mismos nombres que la web para
// aplicar el mismo mapeo semántico en el modo oscuro de la intranet.
val Stone50 = Color(0xFFFAFAF9)
val Stone100 = Color(0xFFF5F5F4)
val Stone200 = Color(0xFFE7E5E4)
val Stone300 = Color(0xFFD6D3D1)
val Stone400 = Color(0xFFA8A29E)
val Stone500 = Color(0xFF78716C)
val Stone600 = Color(0xFF57534E)
val Stone700 = Color(0xFF44403C)
val Stone800 = Color(0xFF292524)
val Stone900 = Color(0xFF1C1917)
val Stone950 = Color(0xFF0C0A09)

// Tercer blob decorativo del fondo de la intranet.
val Sky200 = Color(0xFFBAE6FD)
val Sky500 = Color(0xFF0EA5E9)

// base (Sincronizado con Tailwind :root)
val BackgroundWarm = Color(0xFFFDFCFB) // --background: Soft warm white
val ForegroundStone = Color(0xFF1C1917) // --foreground: Soft stone charcoal (texto principal en claro)
val TextSecondaryLight = Color(0xFF57534E) // gris cálido (stone-600) para texto secundario en claro

// Paleta semántica de estado, independiente de la marca: cada etapa/alerta usa un
// matiz distinto y saturado para leerse de un vistazo en planta. Cada uno tiene una
// variante `*Dark` más luminosa para mantener contraste AA en modo oscuro.

// Recepción — pizarra (neutro, "entrando")
val StatusSlate = Color(0xFF475569)
val StatusSlateDark = Color(0xFF94A3B8)
// Lavado — azul (agua)
val StatusBlue = Color(0xFF2563EB)
val StatusBlueDark = Color(0xFF60A5FA)
// Secado — cian (aire, distinto del naranja de marca)
val StatusCyan = Color(0xFF0E7490)
val StatusCyanDark = Color(0xFF22D3EE)
// Planchado y doblado — violeta
val StatusViolet = Color(0xFF7C3AED)
val StatusVioletDark = Color(0xFFA78BFA)
// Listo para entrega — verde (éxito)
val StatusGreen = Color(0xFF15803D)
val StatusGreenDark = Color(0xFF4ADE80)

// Modo oscuro, portado 1:1 de la intranet web (clases `dark:*` de Tailwind sobre
// la escala `stone`), no un gris propio de Android.
val DarkBg = Stone950
val DarkSurface1 = Stone900
val DarkSurface2 = Stone800
val DarkOnSurface = Color.White
val DarkOnSurfaceMuted = Stone400
val DarkOutline = Stone700

// Señales de urgencia
val StatusAmber = Color(0xFFB45309)     // retraso / prioridad media
val StatusAmberDark = Color(0xFFFBBF24)
val StatusRed = Color(0xFFDC2626)       // incidencia / prioridad alta
val StatusRedDark = Color(0xFFF87171)
val StatusRose = Color(0xFFBE123C)      // insumo crítico
val StatusRoseDark = Color(0xFFFB7185)
