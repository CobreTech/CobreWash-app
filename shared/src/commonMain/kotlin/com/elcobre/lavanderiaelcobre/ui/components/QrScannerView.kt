package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Componente multiplataforma que renderiza la vista de cámara y escanea códigos QR.
 * En Android utiliza CameraX y ML Kit (Barcode Scanning).
 * 
 * @param onQrCodeScanned Callback que se invoca con el contenido (rawValue) del QR detectado.
 * @param isActive Controla si el escáner debe estar activo procesando frames o pausado.
 */
@Composable
expect fun QrScannerView(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    onQrCodeScanned: (String) -> Unit,
)
