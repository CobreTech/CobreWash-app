package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.EventoHistorial
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.Prioridad
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests de las reglas de negocio del dominio [Pedido]: invariantes de avance, historial e inmutabilidad. */
class PedidoTest {

    private fun pedidoBase(
        etapa: EtapaProceso = EtapaProceso.RECEPCION,
        tipo: TipoComanda = TipoComanda.EN_TIENDA,
        alerta: TipoAlerta? = null,
        historial: List<EventoHistorial> = emptyList(),
    ) = Pedido(
        id = "T1",
        comanda = "CMD-T1",
        cliente = "Cliente Test",
        tipo = tipo,
        tipoServicio = "Servicio Test",
        prioridad = Prioridad.MEDIA,
        piezas = 10,
        plazoEntrega = "Hoy",
        etapaActual = etapa,
        alertaActiva = alerta,
        historial = historial,
    )

    @Test
    fun avanzarEtapa_pasaALaSiguienteYRegistraEvento() {
        // Flujo en tienda: Recepción → Lavado.
        val p = pedidoBase(EtapaProceso.RECEPCION).avanzarEtapa("10:00", "Ana", null)
        assertEquals(EtapaProceso.LAVADO, p.etapaActual)
        assertEquals(TipoEvento.CAMBIO_ETAPA, p.historial.first().tipo)
    }

    @Test
    fun avanzarEtapa_respetaElFlujoDeDomicilio() {
        // Flujo a domicilio: Retiro → Recepción en planta.
        val p = pedidoBase(EtapaProceso.RETIRO, tipo = TipoComanda.DOMICILIO).avanzarEtapa("10:00", "Ana", null)
        assertEquals(EtapaProceso.RECEPCION_PLANTA, p.etapaActual)
    }

    @Test
    fun avanzarEtapa_bloqueadoPorAlerta_noAvanza() {
        val original = pedidoBase(EtapaProceso.LAVADO, alerta = TipoAlerta.INCIDENCIA)
        val p = original.avanzarEtapa("10:00", "Ana", null)
        assertEquals(EtapaProceso.LAVADO, p.etapaActual)
        assertTrue(p.estaBloqueado)
        assertEquals(original.historial.size, p.historial.size) // sin evento nuevo
    }

    @Test
    fun avanzarEtapa_enEtapaFinal_noAvanza() {
        val p = pedidoBase(EtapaProceso.ENTREGA).avanzarEtapa("10:00", "Ana", null)
        assertEquals(EtapaProceso.ENTREGA, p.etapaActual)
    }

    @Test
    fun avanzarEtapa_comentarioEnBlanco_usaDetallePorDefecto() {
        val p = pedidoBase().avanzarEtapa("10:00", "Ana", "   ")
        assertEquals("Avance de etapa registrado.", p.historial.first().detalle)
    }

    @Test
    fun avanzarEtapa_conComentario_loRegistra() {
        val p = pedidoBase().avanzarEtapa("10:00", "Ana", "Carga pesada")
        assertEquals("Carga pesada", p.historial.first().detalle)
    }

    @Test
    fun registrarAlerta_bloqueaYRegistra() {
        val p = pedidoBase().registrarAlerta(TipoAlerta.INSUMO_CRITICO, "Falta agua", "10:00", "Sistema")
        assertEquals(TipoAlerta.INSUMO_CRITICO, p.alertaActiva)
        assertTrue(p.estaBloqueado)
        assertEquals(TipoEvento.ALERTA, p.historial.first().tipo)
    }

    @Test
    fun registrarAlerta_textoVacio_usaDescripcionCorta() {
        val p = pedidoBase().registrarAlerta(TipoAlerta.RETRASO, "", "10:00", "Sistema")
        assertEquals(TipoAlerta.RETRASO.descripcionCorta, p.historial.first().detalle)
    }

    @Test
    fun resolverAlerta_desbloquea() {
        val p = pedidoBase(alerta = TipoAlerta.INCIDENCIA).resolverAlerta("10:00", "Ana")
        assertNull(p.alertaActiva)
        assertFalse(p.estaBloqueado)
        assertEquals(TipoEvento.ALERTA_RESUELTA, p.historial.first().tipo)
    }

    @Test
    fun resolverAlerta_sinAlerta_noHaceNada() {
        val original = pedidoBase()
        assertEquals(original, original.resolverAlerta("10:00", "Ana"))
    }

    @Test
    fun agregarComentario_agregaEventoSinCambiarEtapa() {
        val p = pedidoBase(EtapaProceso.LAVADO).agregarComentario("Nota", "10:00", "Ana")
        assertEquals(EtapaProceso.LAVADO, p.etapaActual)
        assertEquals(TipoEvento.COMENTARIO, p.historial.first().tipo)
    }

    @Test
    fun operaciones_noMutanElOriginal() {
        val original = pedidoBase(EtapaProceso.RECEPCION)
        original.avanzarEtapa("10:00", "Ana", null)
        original.registrarAlerta(TipoAlerta.RETRASO, "x", "10:00", "Ana")
        assertEquals(EtapaProceso.RECEPCION, original.etapaActual)
        assertNull(original.alertaActiva)
        assertTrue(original.historial.isEmpty())
    }

    @Test
    fun historial_nuevoEventoVaPrimero() {
        val previo = EventoHistorial(TipoEvento.COMENTARIO, "Viejo", "d", "09:00", "X")
        val p = pedidoBase(historial = listOf(previo)).agregarComentario("Nuevo", "10:00", "Ana")
        assertEquals("Nuevo", p.historial[0].detalle)
        assertEquals(2, p.historial.size)
    }

    @Test
    fun flujo_siguienteEtapaYEsFinalSegunTipo() {
        // En tienda: primera etapa no es final; su siguiente es Lavado.
        val enTienda = pedidoBase(EtapaProceso.RECEPCION, tipo = TipoComanda.EN_TIENDA)
        assertEquals(EtapaProceso.LAVADO, enTienda.siguienteEtapa)
        assertFalse(enTienda.esFinal)

        // En tienda: planchado NO es final, le sigue "Entrega".
        val enTiendaPlanchado = pedidoBase(EtapaProceso.PLANCHADO, tipo = TipoComanda.EN_TIENDA)
        assertEquals(EtapaProceso.ENTREGA, enTiendaPlanchado.siguienteEtapa)
        assertFalse(enTiendaPlanchado.esFinal)

        // En tienda: "Entrega" es la etapa final, igual que en la web.
        val enTiendaFinal = pedidoBase(EtapaProceso.ENTREGA, tipo = TipoComanda.EN_TIENDA)
        assertNull(enTiendaFinal.siguienteEtapa)
        assertTrue(enTiendaFinal.esFinal)

        // A domicilio: la etapa final es la entrega a domicilio; el flujo tiene 6 etapas
        // (las 5 de la web más el retiro/entrega propios del reparto a domicilio).
        val domicilioFinal = pedidoBase(EtapaProceso.ENTREGA_DOMICILIO, tipo = TipoComanda.DOMICILIO)
        assertTrue(domicilioFinal.esFinal)
        assertEquals(6, domicilioFinal.totalEtapas)
    }
}
