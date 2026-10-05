package com.elcobre.lavanderiaelcobre.data

import io.grpc.Status
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class FirebaseTransporteTest {
    @Test
    fun operacionNoPublicadaSeConvierteEnErrorVisible() = runTest {
        val causa = Status.NOT_FOUND.withDescription("operation GetMisComandasAsignadas not found").asException()
        val error = assertFailsWith<OperacionComandaException> { solicitud<Unit> { throw causa } }
        assertSame(causa, error.cause)
        assertTrue(error.message.orEmpty().contains("aún no está actualizado"))
    }

    @Test
    fun falloGrpcRuntimeSeConvierteEnErrorVisible() = runTest {
        val causa = Status.UNAVAILABLE.asRuntimeException()
        val error = assertFailsWith<OperacionComandaException> { solicitud<Unit> { throw causa } }
        assertSame(causa, error.cause)
    }

    @Test
    fun cancelacionDeSesionNoSeConvierteEnError() = runTest {
        val causa = CancellationException("Sesión cerrada")
        val error = assertFailsWith<CancellationException> { solicitud<Unit> { throw causa } }
        assertSame(causa, error)
    }
}
