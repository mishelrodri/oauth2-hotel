# Authorization Server Simple - Spring Security 5 OAuth2

Authorization Server básico con Spring Security 5 que genera JWT para pruebas y desarrollo.

## 🛠️ Tecnologías

- **JDK 15**
- **Spring Boot 2.2.7.RELEASE**
- **Spring Security 5**
- **Spring Security OAuth2**
- **JWT (JSON Web Token)**

## 📋 Requisitos

- Java JDK 15 o superior
- Maven 3.6+

## 🚀 Inicio Rápido

### 1. Compilar el proyecto

```bash
mvn clean package
```

### 2. Ejecutar el servidor

```bash
# Opción 1: Con Maven
mvn spring-boot:run

# Opción 2: Con el JAR generado
java -jar target/auth-server-simple-1.0.0.jar
```

### 3. Verificar que está corriendo

El servidor inicia en el puerto **9000**:
```
http://localhost:9000
```

Al iniciar, verás en los logs la **CLAVE PÚBLICA** que necesitas copiar para configurar tus Resource Servers.

## 🔑 Clave Pública JWT

Al iniciar, el servidor imprime la clave pública en los logs:

```
=================================================================
                    CLAVE PÚBLICA JWT                            
=================================================================
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
-----END PUBLIC KEY-----
=================================================================
```

**⚠️ IMPORTANTE:** Copia esta clave y guárdala en tu Resource Server en:
```
src/main/resources/public-key.pem
```

## 📡 Endpoints Disponibles

### 1. Obtener Token (Client Credentials)

```bash
curl -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read"
```

### 2. Obtener Token (Password Grant)

```bash
curl -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=password" \
  -d "scope=read write"
```

### 3. Obtener Clave Pública

```bash
curl http://localhost:9000/oauth/token_key
```

### 4. Verificar Token

```bash
curl -X POST http://localhost:9000/oauth/check_token \
  -u agreement-service:secret \
  -d "token=TU_ACCESS_TOKEN_AQUI"
```

## 👤 Clientes Configurados

| Client ID | Client Secret | Grant Types | Scopes | Uso |
|-----------|---------------|-------------|--------|-----|
| `agreement-service` | `secret` | client_credentials, password, refresh_token | read, write | Microservicios |
| `web-app` | `web-secret` | authorization_code, refresh_token | read, write | Aplicaciones web |
| `mobile-app` | `mobile-secret` | password, refresh_token | read, write | Apps móviles |

## 🔐 Usuario de Prueba

| Username | Password | Roles |
|----------|----------|-------|
| `admin` | `password` | USER, ADMIN |

## 🧪 Ejemplos de Uso

### 1. Obtener token para un microservicio

```bash
TOKEN=$(curl -s -X POST http://localhost:9000/oauth/token \
  -u agreement-service:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read" | jq -r .access_token)

echo $TOKEN
```

### 2. Usar el token en una petición

```bash
curl http://localhost:8080/agreements \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Decodificar el JWT

Copia el token y pégalo en [https://jwt.io](https://jwt.io) para ver su contenido.

O usa este comando:
```bash
echo $TOKEN | cut -d. -f2 | base64 -d | jq .
```

## 🔧 Configurar en tu Resource Server (Spring Security 6)

### application.yml

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Opción 1: Archivo de clave pública
          public-key-location: classpath:public-key.pem
          
          # Opción 2: Clave pública inline
          # public-key-location: |
          #   -----BEGIN PUBLIC KEY-----
          #   MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
          #   -----END PUBLIC KEY-----
```

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );
        
        return http.build();
    }
}
```

## 📝 Estructura del Token JWT

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

## ⚠️ Notas Importantes

1. **Este es un servidor de desarrollo/testing**. Para producción considera:
   - Usar base de datos para clientes y usuarios
   - Implementar rate limiting
   - Configurar HTTPS
   - Guardar las claves en un KeyStore seguro
   - Implementar refresh token rotation

2. **La clave RSA se genera al iniciar**. Cada reinicio genera una nueva clave, invalidando los tokens anteriores.

3. **Spring Security 5 OAuth2 está deprecado**. Para nuevos proyectos usa [Spring Authorization Server](https://spring.io/projects/spring-authorization-server).

## 🐛 Troubleshooting

### Error: "Unauthorized" al obtener token

Verifica que estás usando las credenciales correctas:
```bash
-u client-id:client-secret
```

### Error: "Invalid token"

El token puede haber expirado (1 hora por defecto) o la clave pública no coincide.

### No puedo conectar desde otro contenedor

Usa el nombre del servicio o IP del contenedor en lugar de `localhost`.

## 📚 Referencias

- [Spring Security OAuth2](https://projects.spring.io/spring-security-oauth/docs/oauth2.html)
- [JWT.io](https://jwt.io)
- [RFC 6749 - OAuth 2.0](https://tools.ietf.org/html/rfc6749)

## 👨‍💻 Autor

Proyecto creado para pruebas y desarrollo de microservicios con OAuth2 y JWT.
