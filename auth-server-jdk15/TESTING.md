# 🧪 Testing del Authorization Server

Guía de pruebas con comandos curl listos para usar.

## 🚀 Antes de empezar

Asegúrate de que el servidor está corriendo:
```bash
mvn spring-boot:run
```

## 1️⃣ Client Credentials Flow (para microservicios)

### Obtener token

```bash
curl -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "scope=read"
```

**Respuesta esperada:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "expires_in": 3599,
  "scope": "read",
  "jti": "abc123..."
}
```

### Guardar token en variable

```bash
TOKEN=$(curl -s -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read" | jq -r .access_token)

echo "Token: $TOKEN"
```

## 2️⃣ Password Flow (para aplicaciones con usuario)

### Obtener token con credenciales de usuario

```bash
curl -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=password" \
  -d "scope=read write"
```

**Respuesta esperada:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3599,
  "scope": "read write",
  "jti": "xyz789..."
}
```

## 3️⃣ Refresh Token Flow

### Renovar token usando refresh token

```bash
REFRESH_TOKEN="tu_refresh_token_aqui"

curl -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "refresh_token=$REFRESH_TOKEN"
```

## 4️⃣ Obtener la Clave Pública

```bash
curl http://localhost:9000/oauth/token_key
```

**Respuesta esperada:**
```json
{
  "alg": "SHA256withRSA",
  "value": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...\n-----END PUBLIC KEY-----"
}
```

### Guardar clave pública en archivo

```bash
curl -s http://localhost:9000/oauth/token_key | jq -r .value > public-key.pem
cat public-key.pem
```

## 5️⃣ Verificar Token

```bash
TOKEN="tu_access_token_aqui"

curl -X POST http://localhost:9000/oauth/check_token \
  -u agreement-service:secret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=$TOKEN"
```

**Respuesta esperada:**
```json
{
  "user_name": "admin",
  "scope": ["read", "write"],
  "active": true,
  "exp": 1738792426,
  "authorities": ["ROLE_USER", "ROLE_ADMIN"],
  "jti": "abc123...",
  "client_id": "agreement-service"
}
```

## 6️⃣ Decodificar JWT (sin verificar)

### Ver el header del JWT

```bash
TOKEN="tu_access_token_aqui"
echo $TOKEN | cut -d. -f1 | base64 -d 2>/dev/null | jq .
```

**Salida:**
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```

### Ver el payload del JWT

```bash
echo $TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq .
```

**Salida:**
```json
{
  "user_name": "admin",
  "scope": ["read", "write"],
  "exp": 1738792426,
  "authorities": ["ROLE_USER", "ROLE_ADMIN"],
  "jti": "abc123...",
  "client_id": "agreement-service"
}
```

## 7️⃣ Usar Token en Resource Server (simulado)

```bash
TOKEN=$(curl -s -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read" | jq -r .access_token)

# Usar el token en una petición (ajusta la URL a tu Resource Server)
curl http://localhost:8080/api/agreements \
  -H "Authorization: Bearer $TOKEN"
```

## 8️⃣ Probar con diferentes clientes

### Cliente: web-app

```bash
curl -X POST http://localhost:9000/oauth/token \
  -u web-app:web-secret \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=password" \
  -d "scope=read"
```

### Cliente: mobile-app

```bash
curl -X POST http://localhost:9000/oauth/token \
  -u mobile-app:mobile-secret \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=password" \
  -d "scope=write"
```

## 9️⃣ Errores Comunes

### Error: "Unauthorized"

```bash
# ❌ Credenciales incorrectas
curl -X POST http://localhost:9000/oauth/token \
  -u wrong-client:wrong-secret \
  -d "grant_type=client_credentials"

# Respuesta:
{
  "error": "unauthorized",
  "error_description": "Bad credentials"
}
```

### Error: "Invalid grant"

```bash
# ❌ Grant type no permitido para este cliente
curl -X POST http://localhost:9000/oauth/token \
  -u web-app:web-secret \
  -d "grant_type=client_credentials"

# Respuesta:
{
  "error": "unauthorized_client",
  "error_description": "Unauthorized grant type: client_credentials"
}
```

### Error: "Invalid scope"

```bash
# ❌ Scope no permitido
curl -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -d "grant_type=client_credentials" \
  -d "scope=admin"

# Respuesta:
{
  "error": "invalid_scope",
  "error_description": "Invalid scope: admin"
}
```

## 🔟 Script Completo de Testing

```bash
#!/bin/bash

echo "==================================="
echo "Testing Authorization Server"
echo "==================================="

# 1. Obtener token
echo "
1️⃣ Obteniendo token..."
TOKEN=$(curl -s -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read" | jq -r .access_token)

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
    echo "❌ Error obteniendo token"
    exit 1
fi

echo "✅ Token obtenido: ${TOKEN:0:50}..."

# 2. Decodificar token
echo "
2️⃣ Decodificando token..."
echo $TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq .

# 3. Obtener clave pública
echo "
3️⃣ Obteniendo clave pública..."
curl -s http://localhost:9000/oauth/token_key | jq -r .value

# 4. Verificar token
echo "
4️⃣ Verificando token..."
curl -s -X POST http://localhost:9000/oauth/check_token \
  -u agreement-service:secret \
  -d "token=$TOKEN" | jq .

echo "
✅ Testing completado exitosamente"
```

Guarda este script como `test.sh`, dale permisos de ejecución y ejecútalo:

```bash
chmod +x test.sh
./test.sh
```

## 🌐 Probar con Postman

### Configuración

1. **URL:** `http://localhost:9000/oauth/token`
2. **Method:** `POST`
3. **Authorization:**
   - Type: `Basic Auth`
   - Username: `agreement-service`
   - Password: `secret`
4. **Body:** (x-www-form-urlencoded)
   - `grant_type`: `client_credentials`
   - `scope`: `read`

### Usar el token

1. **Authorization:**
   - Type: `Bearer Token`
   - Token: `<pegar_token_aqui>`

## ✅ Checklist de Verificación

- [ ] El servidor inicia en el puerto 9000
- [ ] La clave pública se imprime en los logs
- [ ] Puedo obtener token con client_credentials
- [ ] Puedo obtener token con password grant
- [ ] Puedo verificar un token con /oauth/check_token
- [ ] Puedo decodificar el JWT y ver los claims
- [ ] La clave pública es accesible en /oauth/token_key

---

¿Necesitas más ejemplos o tienes algún error? Revisa el README.md principal para troubleshooting.
