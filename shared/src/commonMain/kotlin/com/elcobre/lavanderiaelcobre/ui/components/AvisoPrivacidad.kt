package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler

@Composable
fun AvisoPrivacidad() {
    var abierto by remember { mutableStateOf(false) }
    var errorEnlace by remember { mutableStateOf(false) }
    val enlaces = LocalUriHandler.current
    TextButton(onClick = { abierto = true }) { Text("Privacidad y mis datos") }
    if (abierto) AlertDialog(
        onDismissRequest = { abierto = false },
        title = { Text("Datos personales en esta app") },
        text = {
            Text(
                "Lavandería El Cobre utiliza tu cuenta, rol y operaciones para gestionar el trabajo y proteger el acceso. " +
                    "La cámara se usa al leer QR o capturar inspecciones; no se solicita ubicación. " +
                    "Fotografía solo el vehículo: evita rostros, documentos y objetos personales. " +
                    "Las capturas se conservan mientras se confirma el envío y se limpian al terminar, al cerrar sesión " +
                    "o al iniciar la app si llevan más de 48 horas. Las copias del servidor tienen una conservación independiente. " +
                    "Puedes pedir acceso, rectificación, supresión, oposición o bloqueo en el canal de derechos de la web. " +
                    if (errorEnlace) "No se pudo abrir el navegador. Visita lavanderia-elcobre.vercel.app/privacidad." else "",
            )
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    enlaces.openUri("https://lavanderia-elcobre.vercel.app/privacidad")
                } catch (_: IllegalArgumentException) { errorEnlace = true }
            }) { Text("Aviso y canal de derechos") }
        },
        dismissButton = { TextButton(onClick = { abierto = false }) { Text("Cerrar") } },
    )
}
