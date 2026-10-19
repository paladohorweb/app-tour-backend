# Licencia comercial JGMSolutions

El backend valida la suscripción de la empresa contra el ecosistema antes de permitir operaciones nuevas. Las consultas, recuperación de cuenta y rutas de pagos existentes conservan sus permisos actuales.

Configura en el servidor:

```dotenv
JGM_LICENSE_ENABLED=true
JGM_CONTROL_URL=https://URL-DEL-BACKEND-ECOSISTEMA
JGM_INTEGRATION_KEY=CLAVE_GENERADA_EN_PRODUCT_CONTROL
```

En local se admite HTTP únicamente con localhost o 127.0.0.1. Cada backend necesita un puerto distinto del ecosistema. No agregues la clave al frontend, Git o capturas de pantalla.

1. En el ecosistema crea el acuerdo comercial o piloto de la empresa.
2. Abre Cobros e integración. Para acuerdos comerciales programa los cobros y confirma el dinero recibido.
3. Registra una instalación de este producto y guarda la clave una sola vez.
4. Configura las tres variables anteriores y reinicia este backend.
5. En una sesión autenticada consulta GET /api/jgm/license. Vincula el identificador default en el ecosistema; esta instalación representa un solo negocio.
6. Comprueba estado CURRENT o TRIAL. La renovación admite GRACE por tres días; SUSPENDED deja el sistema en modo consulta y bloquea escrituras con HTTP 402.

Las respuestas de consulta exponen X-JGM-License-State y X-JGM-Billing-Url para informar al usuario. Los roles y la propiedad de los datos siguen validándose en los controladores existentes. Un rol administrativo local no evita el control de licencia.

Las decisiones se conservan como máximo 30 segundos y nunca después de validUntil, incluida la medianoche del vencimiento. Si el control no responde, las escrituras fallan con HTTP 503 y las consultas permanecen disponibles. TLS, tiempo máximo de solicitud, rechazo de redirecciones y comprobación de producto/empresa impiden reutilizar una respuesta de otra aplicación.

El modo sin variables conserva instalaciones locales y demos como STANDALONE. La explotación comercial requiere habilitar las tres variables y verificar lastSeenAt en el ecosistema. Rotar la clave exige actualizarla aquí; revocar una instalación afecta a sus empresas en un máximo de 30 segundos. Nunca se eliminan datos por mora.

Pruebas: JgmLicenseIntegrationTest ejecuta HTTP real con un control aislado; verifica suspensión, reactivación, caída del control, producto/tenant incorrectos, rutas de recuperación y credenciales.

