package com.elcobre.lavanderiaelcobre.data

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArchivosEvidenciaTest {
    @Test
    fun limpiaSoloCapturasVencidasDentroDeLaCarpeta() {
        val raiz = createTempDirectory("cobre-privacidad-").toFile()
        try {
            val carpeta = File(raiz, "evidencias").apply { mkdir() }
            val vieja = File(carpeta, "captura-vieja.jpg").apply { writeText("foto"); setLastModified(1000) }
            val reciente = File(carpeta, "captura-reciente.jpg").apply { writeText("foto"); setLastModified(10000) }
            val ajena = File(carpeta, "documento.jpg").apply { writeText("privado"); setLastModified(1000) }
            val fuera = File(raiz, "captura-fuera.jpg").apply { writeText("foto") }
            limpiarArchivosEvidencia(carpeta, 5000)
            assertFalse(vieja.exists())
            assertTrue(reciente.exists())
            assertTrue(ajena.exists())
            assertFalse(esCapturaPropia(carpeta, fuera))
            assertFalse(esCapturaPropia(carpeta, File(carpeta, "../captura-fuera.jpg")))
            limpiarArchivosEvidencia(carpeta)
            assertFalse(reciente.exists())
            assertTrue(ajena.exists())
            assertTrue(fuera.exists())
        } finally { raiz.deleteRecursively() }
    }
}
