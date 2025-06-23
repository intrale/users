# Cambio de contraseña

Endpoint: `/v1/users/change-password`

Ejemplo de solicitud:
```json
{
  "previousPassword": "MiContraActual123",
  "proposedPassword": "MiNuevaContra456"
}
```

Respuesta exitosa:
```
HTTP 200 OK
```

Este endpoint devuelve errores de autenticación si el token proporcionado no es válido.
