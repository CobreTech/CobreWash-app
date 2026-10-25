package com.elcobre.lavanderiaelcobre.data

import android.content.Context
import java.io.File
import java.io.IOException

private var carpetaEvidencias: File? = null
private const val RETENCION_TEMPORAL_MS = 48L * 60 * 60 * 1000

fun inicializarArchivosEvidencia(context: Context) {
    carpetaEvidencias = File(context.applicationContext.filesDir, "evidencias_flota")
    limpiarArchivosEvidencia(carpetaEvidencias, System.currentTimeMillis() - RETENCION_TEMPORAL_MS)
}

/** Solo archivos temporales propios: nunca borra directorios ni sigue rutas fuera de la carpeta. */
internal fun limpiarArchivosEvidencia(carpeta: File?, antesDe: Long = Long.MAX_VALUE) {
    carpeta?.listFiles()?.forEach { archivo ->
        if (esCapturaPropia(carpeta, archivo) && archivo.lastModified() < antesDe) archivo.delete()
    }
}

internal fun esCapturaPropia(carpeta: File, archivo: File): Boolean = try {
    archivo.isFile && archivo.name.startsWith("captura-") && archivo.extension == "jpg" &&
        archivo.canonicalFile.parentFile == carpeta.canonicalFile
} catch (_: IOException) { false }

internal fun eliminarCapturasConfirmadas(rutas: List<String>) {
    val carpeta = carpetaEvidencias ?: return
    rutas.forEach { ruta ->
        val archivo = File(ruta.removePrefix("file://"))
        if (esCapturaPropia(carpeta, archivo)) archivo.delete()
    }
}

internal fun eliminarCapturasAlSalir() = limpiarArchivosEvidencia(carpetaEvidencias)
