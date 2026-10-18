package com.elcobre.lavanderiaelcobre.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val TAG_CAMARA = "CapturadorFoto"
private const val CALIDAD_JPEG = 90

@Composable
actual fun rememberCapturadorFoto(
    onFotoCapturada: (uri: String) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val callbackActual by rememberUpdatedState(onFotoCapturada)

    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val ruta = guardarBitmapEvidencia(context, bitmap)
            callbackActual(ruta)
        }
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { concedido ->
        if (concedido) {
            lanzarCamaraSegura(lanzadorCamara, callbackActual)
        } else {
            Log.w(TAG_CAMARA, "Permiso de cámara denegado por el usuario.")
        }
    }

    return remember(context, lanzadorCamara, lanzadorPermiso) {
        {
            val tienePermiso = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
            if (tienePermiso) {
                lanzarCamaraSegura(lanzadorCamara, callbackActual)
            } else {
                lanzadorPermiso.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

private fun lanzarCamaraSegura(
    lanzador: ActivityResultLauncher<Void?>,
    onFallback: (String) -> Unit,
) {
    try {
        lanzador.launch()
    } catch (causa: ActivityNotFoundException) {
        Log.w(TAG_CAMARA, "No se encontró aplicación de cámara; usando evidencia simulada.", causa)
        onFallback("vehiculos/evidencia/inspeccion-${System.currentTimeMillis()}.jpg")
    } catch (causa: SecurityException) {
        Log.e(TAG_CAMARA, "Seguridad impidió abrir la cámara.", causa)
    }
}

private fun guardarBitmapEvidencia(context: Context, bitmap: Bitmap): String {
    val marcaTiempo = System.currentTimeMillis()
    return try {
        val carpeta = File(context.cacheDir, "evidencias_flota").apply { mkdirs() }
        val archivo = File(carpeta, "inspeccion-$marcaTiempo.jpg")
        FileOutputStream(archivo).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
        }
        archivo.absolutePath
    } catch (causa: IOException) {
        Log.w(TAG_CAMARA, "No se pudo guardar la foto en caché; usando ruta lógica.", causa)
        "vehiculos/evidencia/inspeccion-$marcaTiempo.jpg"
    }
}

@Composable
actual fun MiniaturaFotoChequeo(
    uri: String,
    descripcion: String?,
    tint: Color,
    modifier: Modifier,
) {
    val bitmap = remember(uri) { decodificarArchivoImagen(uri) }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = descripcion,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Icon(
            imageVector = CobreIcons.PhotoCamera,
            contentDescription = descripcion,
            tint = tint,
            modifier = modifier,
        )
    }
}

private fun decodificarArchivoImagen(uri: String): ImageBitmap? {
    if (uri.isBlank()) return null
    val rutaLimpia = uri.removePrefix("file://")
    val archivo = File(rutaLimpia)
    if (!archivo.exists() || !archivo.isFile) return null
    return BitmapFactory.decodeFile(archivo.absolutePath)?.asImageBitmap()
}
