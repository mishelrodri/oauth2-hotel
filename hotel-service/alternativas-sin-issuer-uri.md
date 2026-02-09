# Alternativas cuando NO tenemos Issuer-URI

## ⚠️ Advertencia

Este documento describe el razonamiento inicial durante la integración entre un **Authorization Server legacy (Spring Boot 2 + `spring-security-oauth2`)** y un **Resource Server moderno (Spring Boot 3 / Spring Security 6)**.

Algunas conclusiones pueden resultar confusas porque Spring ha tenido **dos implementaciones distintas de Resource Server**.
La propiedad `security.oauth2.resource.jwt.key-value` pertenece al stack antiguo y **no es soportada en Spring Boot 3**, por lo que el framework la ignora.

Se mantiene como referencia de aprendizaje, no como configuración final recomendada.

PARA VER LA CONCLUSION FINAL VER: `entendiendo-dudas.md`

Cuando no tienes acceso a `issuer-uri` (porque no tienes un servidor de autenticación centralizado con endpoint `.well-known/openid-configuration`), tienes estas opciones para configurar JWT en Spring Security:

---

## 📚 Contexto Histórico: Evolución de propiedades

### Spring OAuth2 Legacy (Antiguo)

En versiones antiguas **antes de Spring Security 5**, la propiedad era:

```properties
security.oauth2.resource.jwt.key-value=<STRING con la clave pública>
```

Esto permitía poner **directamente el String de la clave** en el archivo de configuración.

### Spring Security 5+ (Moderno)

A partir de **Spring Boot 2.x y Spring Security 5+**, el estándar cambió significativamente:

| Propiedad             | Qué acepta                          | Requiere Bean |
| --------------------- | ----------------------------------- | ------------- |
| `key-value`           | String directo de la clave (legacy) | ✅ **Sí**     |
| `public-key-location` | Ruta a archivo (.pem, .key, etc)    | ❌ No         |
| `issuer-uri`          | URL al servidor OAuth2              | ❌ No         |

**Nota importante**: Si necesitas inyectar la llave pública como un **String** (ej: desde una variable de entorno, Base de Datos, o Vault), **no existe una propiedad nativa** que lo acepte directamente. En ese caso, **debes crear manualmente un Bean `JwtDecoder`** como se muestra en la opción 1.

---

## 1. **Solo Bean `JwtDecoder`** ✅ Sin propiedad en YAML

### Configuración del Bean:

```java
@Configuration
public class JwtDecoderConfig {
    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1uheN01GIYCMQIZCkSWl\n" +
                "...\n" +
                "-----END PUBLIC KEY-----";

        // Parsear PEM → Base64 → PublicKey
        String publicKeyContent = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) publicKey).build();
    }
}
```

### YAML requerido:

**NADA** - No necesitas propiedades en el YAML. El Bean es suficiente.

Si tienes la propiedad `key-value` en el YAML, puedes comentarla o eliminarla sin problemas:

```yaml
# spring:
#   security:
#     oauth2:
#       resourceserver:
#         jwt:
#           key-value: |  ← COMENTADO o ELIMINADO
```

### ¿Necesita Bean?

**SÍ, es lo ÚNICO que necesitas en Spring Boot 3.x**

- ✅ **El Bean `JwtDecoder` es obligatorio**
- ✅ Transforma la llave PEM a un objeto que Spring entiende
- ✅ **NO necesitas propiedades en YAML**
- ✅ **NO necesitas `SecurityConfig` adicional**
- ℹ️ Ideal si traes la llave desde: Env-vars, BD, Vault, etc.

---

## 2. **`public-key-location` (Archivo externo)** ✅ SIN Bean

### Configuración:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          public-key-location: classpath:public.key
```

### Archivo: `src/main/resources/public.key`

```
-----BEGIN PUBLIC KEY-----
sdkjhkjahjdhakjhdkhdkahdkhdk
...
-----END PUBLIC KEY-----
```

### Formatos de archivo permitidos

La extensión del archivo **NO importa**. Puedes usar:

- ✅ `public.key` (extensión .key)
- ✅ `public.pem` (extensión .pem)
- ✅ `rsa-public-key` (sin extensión)
- ✅ Cualquier nombre y extensión

**Lo que SÍ importa es el CONTENIDO:**

El archivo **debe contener obligatoriamente:**

```
-----BEGIN PUBLIC KEY-----
[contenido Base64 de la llave]
-----END PUBLIC KEY-----
```

Si falta el `BEGIN` o `END`, Spring no podrá parsear la llave y fallará.

### Ejemplo alternativo con `.pem`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          public-key-location: classpath:keys/jwt-public.pem
```

Archivo: `src/main/resources/keys/jwt-public.pem`

```
-----BEGIN PUBLIC KEY-----
sdkjhkjahjdhakjhdkhdkahdkhdk
...
-----END PUBLIC KEY-----
```

---

### ¿Necesita Bean?

**NO** - Spring Security reconoce automáticamente esta propiedad y crea el `JwtDecoder` sin código personalizado.

### Ventajas:

- ✅ **SIN Bean necesario** (Spring lo maneja automáticamente)
- ✅ YAML más limpio
- ✅ Separación de concerns (llave en archivo)

### Desventajas:

- ❌ Requiere un archivo externo
- ❌ Un archivo más que gestionar

---

## 3. **`issuer-uri` (Recomendado)** ✅ SIN Bean

### Configuración:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth-server:8080/oauth2
```

### ¿Necesita Bean?

**NO** - Spring obtiene automáticamente la llave pública del endpoint `.well-known/openid-configuration` del servidor de autenticación.

### Ventajas:

- ✅ **SIN Bean necesario** (Spring lo maneja automáticamente)
- ✅ Configuración mínima
- ✅ Dinámico (si cambia la llave en el servidor, se actualiza automáticamente)

### Desventajas:

- ❌ Requiere tener un servidor de autenticación disponible
- ❌ No funciona en local sin el servidor corriendo
