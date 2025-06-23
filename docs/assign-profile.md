# Asignar perfil a usuario

Este endpoint permite asignar un perfil a un usuario dentro de un negocio. Se debe invocar mediante un token JWT válido correspondiente a un administrador de plataforma.

## Solicitud

`POST /{business}/assignProfile`

Cuerpo:

```json
{
  "email": "usuario@ejemplo.com",
  "profile": "BUSINESS_ADMIN"
}
```

## Respuesta

- **200**: asignación registrada correctamente.
- **400**: errores de validación en la solicitud.
- **401**: el usuario no posee permisos suficientes.
