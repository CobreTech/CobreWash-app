# Seguridad — Lavandería El Cobre (app KMP)

Este documento define las prácticas de **secure coding**, la **gestión de secretos** y el
**endurecimiento** de la app. Complementa el mapeo a estándares en
[`docs/COMPLIANCE.md`](docs/COMPLIANCE.md).

> **Estado actual:** prototipo **no funcional** con datos mock en memoria. Sin backend, sin
> red, sin autenticación real y sin datos personales reales. La superficie de ataque es
> mínima *hoy*; la mayoría de los controles se activan en la **fase Firebase** (ver COMPLIANCE).

---

## 1. Gestión de secretos

**Regla de oro: ningún secreto en el repositorio.** El `.gitignore` ya excluye:

- `local.properties`, `keystore.properties`, `secrets.properties`
- `google-services.json`, `GoogleService-Info.plist` (config Firebase, por entorno)
- `*.keystore`, `*.jks`, `*.p12` (firma)

**Cómo aportar secretos sin commitearlos:**
- **Local:** en `local.properties` / `keystore.properties` (ya ignorados). Leerlos en Gradle con
  `Properties()` y exponerlos vía `BuildConfig`/`buildConfigField`.
- **CI/CD:** como *secrets* del runner (variables de entorno), nunca en el YAML.
- **Firma release:** keystore fuera del repo; sus credenciales en `keystore.properties`.

## 2. Convenciones de secure coding

- **Sin secretos hardcodeados** (API keys, contraseñas, tokens) en el código fuente.
- **Sin logging de datos sensibles** (credenciales, PII, tokens). Evitar `println` en release.
- **Validación en los límites:** validar/normalizar toda entrada de usuario y toda respuesta de
  red *antes* de usarla. La autorización real va **del lado servidor** (reglas de Firestore), no
  solo en la UI.
- **Dominio inmutable:** `Pedido` es una `data class` sin estado mutable compartido → menos
  errores de concurrencia y estado. Mantener las reglas de negocio como funciones puras.
- **Principio de mínimo privilegio:** declarar solo los permisos necesarios (hoy: ninguno de red).
- **Solo HTTPS/TLS** cuando se agregue red; nada de tráfico en claro.
- **Higiene de dependencias:** versiones fijadas en `gradle/libs.versions.toml`; revisar
  vulnerabilidades conocidas antes de subir versiones.

## 3. Endurecimiento aplicado

- `android:allowBackup="false"` en el manifiesto — evita respaldo automático de datos de la app.
- Sin permiso de INTERNET declarado (no hay red todavía).

## 4. Pendiente para la fase Firebase (resumen)

- **Firebase Auth** en lugar del login mock (hoy entra cualquier par no vacío).
- **Reglas de seguridad de Firestore** — crítico, porque se comparte BD con la web (ver COMPLIANCE §4).
- Almacenamiento seguro de tokens/credenciales, TLS, validación de entradas contra el esquema
  compartido, y reemplazo de `RelojMock` por hora del servidor/dispositivo confiable.

## 5. Reporte de vulnerabilidades

Reportar cualquier hallazgo de seguridad de forma privada al responsable del proyecto
(no abrir issues públicos con detalles explotables). Definir un contacto antes del despliegue.
