# Avisos del equipo

La pantalla Avisos de Android consulta `GetAvisosParaEquipo` del conector
`example`, con el rol `operario` y la política `SERVER_ONLY`. Data Connect valida
el perfil activo y solo devuelve avisos generales o dirigidos a operarios.
La publicación sigue disponible desde la web de administración.

Se muestran título, contenido completo, autor, destinatario y fecha en la zona
`America/Santiago`. No se inventan avisos fijados: el backend no tiene ese campo.

La carga inicial contiene 20 avisos; Cargar más amplía la ventana de consulta
en bloques de 20. Consultar nuevamente desde el inicio mantiene el orden y evita
saltarse registros si se publican avisos nuevos entre dos cargas.

La lista y el contador se actualizan cada 30 segundos mientras la app está
visible y al regresar al primer plano. Actualizar permite consultar manualmente.
Un fallo conserva los resultados anteriores y ofrece Reintentar; el cierre o
cambio de sesión borra los datos y descarta respuestas de la sesión anterior.

Las pruebas `AvisosTest` cubren paginación, refresco, error y reintento, ausencia
de sesión, solicitudes superpuestas y respuestas tardías. El script
`scripts/test-avisos.mjs` del repositorio web comprueba los permisos en el emulador.

iOS conserva una implementación explícitamente no disponible hasta integrar
Firebase para esa plataforma; no muestra avisos simulados.
