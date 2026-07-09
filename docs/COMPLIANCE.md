# Cumplimiento y estándares — Lavandería El Cobre

Mapeo del proyecto a estándares internacionales de **calidad** y **seguridad**.

> **Aviso honesto:** este documento es un **mapeo y hoja de ruta**, no un certificado.
> El cumplimiento formal (ISO/IEC 27001, certificación) es un *proceso* auditado, no un
> resultado de programar. Aquí se documenta qué prácticas ya se adoptaron y qué queda para la
> fase de conexión a Firebase, de modo que el código quede **audit-ready**.

**Fase actual:** prototipo no funcional (mock en memoria, sin backend/red/auth/PII real).
**Siguiente fase:** Firebase (Auth + base de datos compartida con la página web).

---

## 1. ISO/IEC 25010 — Calidad del producto de software

| Característica | Estado actual | Notas / plan |
|---|---|---|
| **Adecuación funcional** | ✅ Flujo completo demostrable | Login → Dashboard → Detalle con reglas de negocio reales |
| **Eficiencia de desempeño** | ✅ Adecuada | Compose + estado inmutable; listas con `LazyColumn` |
| **Compatibilidad** | ✅ KMP Android/iOS | Lógica compartida en `commonMain` |
| **Usabilidad** | ✅ Alta | Alto contraste, color semántico, targets grandes, modo claro/oscuro, accesibilidad básica |
| **Fiabilidad** | 🟡 En progreso | Dominio puro + **tests unitarios** (`PedidoTest`); falta cobertura de UI/VM |
| **Seguridad** | 🟡 N/A hoy | Sin superficie real; ver OWASP MASVS §2 y fase Firebase |
| **Mantenibilidad** | ✅ Buena | MVVM, dominio inmutable, tema centralizado, análisis estático (detekt) |
| **Portabilidad** | ✅ Buena | Sin dependencias específicas de plataforma en la lógica |

## 2. OWASP MASVS v2 — Seguridad de app móvil

El estándar correcto para una app móvil (no el OWASP Top 10, que es web). La mayoría aplica
en la **fase Firebase**.

| Grupo MASVS | Estado (prototipo) | Acción en fase Firebase |
|---|---|---|
| **MASVS-STORAGE** (almacenamiento) | N/A — nada persiste | No guardar datos sensibles; usar almacenamiento cifrado para tokens |
| **MASVS-CRYPTO** | N/A | Delegar en TLS/Firebase; no criptografía casera |
| **MASVS-AUTH** | ❌ Login mock (entra cualquiera) | **Firebase Auth**; roles/claims; sesión y cierre seguros |
| **MASVS-NETWORK** | N/A — sin red | Solo TLS; considerar *certificate pinning* |
| **MASVS-PLATFORM** | 🟡 `allowBackup=false` | Revisar exports, deep links, `WebView`, permisos mínimos |
| **MASVS-CODE** | ✅ Deps fijadas; sin secretos | Validación de entradas; ofuscación (R8) en release |
| **MASVS-RESILIENCE** | N/A | Según sensibilidad: anti-tamper/root detection si aplica |

## 3. IEEE (referencia de proceso)

- **IEEE 730** (Aseguramiento de calidad): tests + análisis estático como controles.
- **IEEE 1016** (Descripción de diseño): arquitectura documentada (MVVM, dominio inmutable).
- **ISO/IEC/IEEE 12207** (Ciclo de vida): fases prototipo → integración → despliegue.

## 4. Plan de reglas de seguridad de Firestore (CRÍTICO)

Como la app **comparte base de datos con la web**, la seguridad *no puede* vivir en el cliente:
debe hacerse cumplir del lado servidor con reglas.

Principios a aplicar:
- **Autenticación obligatoria:** `request.auth != null` para toda lectura/escritura.
- **Autorización por rol:** operario de planta solo modifica campos permitidos (p. ej. `etapaActual`,
  agregar al `historial`); no puede borrar pedidos ni alterar datos de cliente.
- **Validación de esquema en las reglas:** tipos y valores permitidos (etapas, prioridades) validados
  server-side, no confiando en el cliente.
- **Contrato de datos estable app↔web:** persistir *códigos* (`"LAVADO"`), separados del texto visible
  en español, para que app y web coincidan.
- **Transiciones válidas:** idealmente validar que el avance de etapa respete el orden y el bloqueo por
  alerta también server-side (defensa en profundidad; la regla de UI no basta).
- **Mínimo privilegio y auditoría:** reglas restrictivas por defecto; registrar cambios de estado.

## 5. Checklist de la fase Firebase

- [ ] Firebase Auth reemplaza el login mock
- [ ] Reglas de Firestore (auth + rol + validación de esquema + transiciones)
- [ ] Contrato de datos compartido con la web (códigos estables por enum)
- [ ] Reemplazar `RelojMock` por hora confiable (servidor)
- [ ] `google-services.json` fuera del repo, por entorno
- [ ] R8/ofuscación en release; sin logs sensibles
- [ ] Solo TLS; revisar permisos del manifiesto
- [ ] Pipeline CI con detekt + tests como *quality gates*

---

*Última actualización: fase prototipo. Revisar al iniciar la integración con Firebase.*
