package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Crea un lanzador multiplataforma que solicita el permiso de cámara (si es requerido)
 * y abre la cámara del dispositivo para capturar una fotografía de evidencia de inspección.
 *
 * @param onFotoCapturada Callback invocado con la ruta local o URI de la foto capturada.
 */
@Composable
expect fun rememberCapturadorFoto(
    onFotoCapturada: (uri: String) -> Unit,
): () -> Unit

/**
 * Renderiza la miniatura de una fotografía de inspección si [uri] corresponde a un archivo
 * local accesible en el dispositivo, o un ícono de cámara en caso contrario.
 */
@Composable
expect fun MiniaturaFotoChequeo(
    uri: String,
    descripcion: String?,
    tint: Color,
    modifier: Modifier = Modifier,
)
