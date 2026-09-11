# Migración a v2

V1 crea las tablas heredadas en una base vacía. V2 añade consentimientos a usuarios y las tablas nuevas. `baseline-on-migrate=false` evita aceptar silenciosamente un esquema desconocido.

## Instalación nueva

Usar una base vacía, ejecutar Flyway al arrancar, crear el primer administrador mediante variables temporales y publicar experiencias enriquecidas. La versión de producción parte sin datos ficticios.

## Instalación con datos existentes

1. Detener escrituras de la aplicación anterior y realizar un respaldo verificable.
2. Restaurar ese respaldo en una base de ensayo.
3. Comparar columnas, tipos, índices y claves foráneas con V1. Corregir las diferencias mediante una migración explícita revisada; no asumir que `ddl-auto=update` dejó el mismo esquema.
4. Solo cuando el esquema corresponda a V1, registrar mediante Flyway CLI un baseline de versión 1 en la copia y ejecutar V2. Es una acción administrativa explícita y no la ejecuta esta entrega sobre tu base.
5. Verificar login, reservas heredadas y nuevas, relaciones, consultas y restauración.
6. Migrar el catálogo manualmente o mediante un script supervisado que complete owner_id, tipo, departamento, municipio, WhatsApp, RNT, acceso, punto de encuentro, condiciones y precio. No se infieren propietarios ni números de contacto. Los tours y reservas antiguos se conservan en sus tablas, pero no aparecen automáticamente en el catálogo v2.
7. Repetir el procedimiento aprobado en producción durante una ventana de mantenimiento y validar conteos y consultas.

No hay reversión destructiva automática. Ante un fallo de migración, conservar evidencia y restaurar la copia según el procedimiento ensayado. No borrar tablas ni ejecutar `flyway clean` sobre datos reales.
