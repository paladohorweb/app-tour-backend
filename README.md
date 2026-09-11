# App Guía API v2

Java 17, Spring Boot 3.5.16, Spring Security, JWT HS256, MySQL 8 y Flyway. API nueva bajo `/api/v2`, compatible con el frontend v2. Conserva tablas y consultas heredadas; no convierte automáticamente tours antiguos con datos faltantes.

## Inicio local (PowerShell)

1. Crea una base MySQL vacía `app_guia_v2` y un usuario limitado a esa base.
2. Configura las variables de entorno en la terminal o en el IDE. Spring no carga `.env` automáticamente fuera de Docker Compose.

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/app_guia_v2?serverTimezone=America/Bogota"
$env:DB_USER="app_guia"
$env:DB_PASSWORD="TU_CLAVE_LOCAL"
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$env:CORS_ALLOWED_ORIGINS="http://localhost:4200"
$env:BOOTSTRAP_ADMIN_EMAIL="TU_CORREO"
$env:BOOTSTRAP_ADMIN_PASSWORD="UNA_CLAVE_UNICA_DE_AL_MENOS_16_CARACTERES"
.\mvnw.cmd spring-boot:run
```

Después de crear la cuenta administrativa, retira BOOTSTRAP_ADMIN_EMAIL/PASSWORD. El inicio público no permite crear ADMIN. Registra cuentas de viajero o guía desde la aplicación; no hay credenciales de demo en producción.

## Docker y producción

Copia `.env.example` a `.env`, configura todos los valores y ejecuta `docker compose up --build -d`. No publiques `.env`. El puerto API queda enlazado a `127.0.0.1:8086` para colocarlo detrás de un proxy HTTPS; MySQL no publica un puerto al host.

El perfil `prod` verifica `LEGAL_NAME`, `LEGAL_TAX_ID`, `LEGAL_ADDRESS`, `LEGAL_EMAIL`, `LEGAL_APPROVED=true` y orígenes HTTPS explícitos. `LEGAL_APPROVED` registra una decisión del operador, no reemplaza revisión legal. Usa un secreto JWT nuevo, aleatorio y exclusivo de este entorno. Retira o rota tokens/secreto previos porque la versión antigua imprimía partes del secreto y cabeceras Authorization en logs.

En RDS utiliza TLS con verificación de certificado según tu conexión. Configura copias automáticas y prueba restauración antes del lanzamiento. No ejecutes MySQL y Java juntos en una t3.micro de 1 GiB como supuesto de capacidad validada: el Compose sirve para instalación local o un servidor dimensionado; para AWS usa RDS u otra base separada.

## Seguridad implementada

- Autorización por rol y propietario para experiencias, reservas, notificaciones y datos.
- Contraseñas BCrypt, validación de longitud, cuentas inactivas rechazadas y sesiones JWT de 30 minutos por defecto.
- Eliminados logs de tokens/secreto y la emisión de refresh tokens que no tenían flujo de rotación.
- Pagos heredados y webhook deshabilitados por la cadena de seguridad. Un usuario no puede autoacreditar un pago.
- Confirmación de cupos con bloqueo transaccional y aislamiento READ_COMMITTED; solicitudes idempotentes por usuario y clave.
- Consentimiento de políticas versionado, marketing opcional y auditable, historial de acciones y solicitudes de datos.
- CORS explícito, Swagger desactivado, esquema validado y migraciones Flyway sin baseline automático.
- Limitación por IP: 10 solicitudes de autenticación/minuto y 240 solicitudes API/minuto; mapa de contadores acotado. Es un control por instancia. Para múltiples réplicas configura limitación compartida o en el borde. Detrás de un proxy, configura sus IP de confianza y tratamiento de IP original; no confíes en X-Forwarded-For enviado directamente por el cliente.

No se garantiza invulnerabilidad ni se ha realizado una auditoría externa. La aplicación todavía requiere configuración real de infraestructura y validación operativa antes de recibir clientes.

## Datos y migración

Lee `docs/MIGRATION.md`. En base vacía Flyway crea V1 y V2. En una base existente debes comparar el esquema, respaldar y ensayar un baseline explícito en copia; no activar `baseline-on-migrate` ni `ddl-auto=update` para forzar que arranque. Las tablas v2 conservan la identidad del dueño y snapshots de precio, condiciones y punto de encuentro.

## Notificaciones y derechos

POST `/api/v2/admin/notifications` permite al administrador enviar a un usuario o a todos los activos. `promotional=true` respeta la autorización vigente. Los cambios de reserva generan avisos transaccionales. La bandeja es interna; no se configuró un servicio externo de mensajería.

Los usuarios consultan preferencias y registran solicitudes de acceso, corrección, supresión, revocatoria o reclamo. El administrador consulta solicitudes en la pantalla Notificaciones. Marcar atendida requiere haber respondido por el canal verificado del titular; no elimina datos automáticamente ni envía una respuesta externa. Define responsables y plazos operativos antes de abrir el servicio.

## Verificación

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Pruebas con H2 en modo MySQL, migraciones Flyway reales y validación del esquema Hibernate. Incluyen concurrencia, capacidad, acceso cruzado, consentimiento, cancelación pagada, noches de hospedaje y rechazo de altas de ADMIN. Debe repetirse la validación contra MySQL 8 y el proxy HTTPS de destino antes de producción.
