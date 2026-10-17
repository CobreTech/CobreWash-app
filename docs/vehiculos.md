# Control de Flota y Vehículos (Sprint 08)

La vista `VehiculosScreen` del rol **Administrador** conecta la aplicación móvil con el conector `example` de Firebase Data Connect (`GetFlotaVehiculos`, `RegistrarVehiculoFlota`, `EditarVehiculoFlota`, `RegistrarSalidaFlota`, `RegistrarRetornoFlota` y `AdjuntarFotoInspeccionFlota`) usando `QueryRef.FetchPolicy.SERVER_ONLY`.

## Reglas de negocio sincronizadas con la web

- **Catálogo de Flota (`RF-VE01`):**
  - Normaliza las patentes chilenas al formato en mayúsculas con guion (`HLRT-24` o `AB-1234`) mediante `normalizarPatenteChilena` y valida unicidad tanto en cliente como en PostgreSQL.
  - Permite habilitar o enviar unidades a mantención (`activo`), bloqueando la desactivación cuando el vehículo mantiene una salida abierta (`EN_SERVICIO` o `PROGRAMADA`).
- **Registro de Salida (`RF-VE02`):**
  - Registra la salida en ruta junto con la inspección inicial (`momento: ANTES`), conductor responsable, estado técnico (`APTO` o `CON_OBSERVACIONES`) y kilometraje de salida.
  - Bloquea la salida si el vehículo está inactivo, si ya tiene una salida abierta, si el estado técnico es `NO_APTO` o si el odómetro es menor al último kilometraje registrado.
- **Registro de Retorno (`RF-VE03`):**
  - Registra la inspección posterior (`momento: DESPUES`), exige que el odómetro de entrada sea mayor o igual al odómetro de salida (`kmEntrada >= kmSalida`), adjunta evidencia fotográfica y cierra el viaje (`FINALIZADA`).
- **Historial y Resiliencia (`RF-VE04`):**
  - Consulta y filtra la flota por estado operativo (`Todos`, `Disponibles`, `En ruta`, `Inactivos`) y término de búsqueda (patente, marca/modelo o conductor).
  - Refresca automáticamente cada 30 segundos mientras la sesión de Administrador está activa en primer plano (`Lifecycle.State.STARTED`), conserva el último estado válido ante fallos de red y descarta respuestas tardías al cerrar sesión.

## Verificación

```powershell
.\gradlew.bat detekt :shared:testAndroidHostTest
```
