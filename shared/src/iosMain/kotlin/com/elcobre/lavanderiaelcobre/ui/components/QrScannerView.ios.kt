package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun QrScannerView(
    modifier: Modifier,
    isActive: Boolean,
    onQrCodeScanned: (String) -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Escáner de QR no disponible en el simulador iOS")
    }
}
