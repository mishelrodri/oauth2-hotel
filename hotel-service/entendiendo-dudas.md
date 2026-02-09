# Spring Boot 3 Resource Server + Authorization Server Legacy (sin JWKS)

Este documento corrige conceptos comunes cuando un **microservicio Spring Boot 3 / Spring Security 6** debe validar JWT emitidos por un **Authorization Server viejo (Spring Boot 2.x / spring-security-oauth2 legacy)** que **no expone** `.well-known/openid-configuration` ni `jwks_uri`.

---

## ❗ Punto clave

En **Spring Boot 3** la propiedad:

```
spring.security.oauth2.resourceserver.jwt.key-value
```

**YA NO EXISTE.**

Era parte del stack antiguo `spring-security-oauth2` (ResourceServerConfigurerAdapter / @EnableResourceServer).

Por eso, si la defines en `application.yml`, **Spring Boot 3 la ignora** y el contexto falla con:

```
required a bean of type 'org.springframework.security.oauth2.jwt.JwtDecoder' that could not be found
```

No es que tu configuración esté mal: es que **Boot 3 nunca crea el JwtDecoder** porque no reconoce esa propiedad.

---

## Cómo Spring Boot 3 crea automáticamente el `JwtDecoder`

Spring Security 6 solo auto-configura el `JwtDecoder` si encuentra **una** de estas configuraciones válidas:

### 1) `issuer-uri` (OIDC moderno)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth-server
```

Spring descarga dinámicamente el `jwks_uri` desde `.well-known/openid-configuration`.

---

### 2) `jwk-set-uri`

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: https://auth-server/oauth/token_key
```

Útil cuando el auth-server viejo expone las llaves pero no OIDC.

---

### 3) `public-key-location`  ← ✔ **Caso típico con auth legacy**

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          public-key-location: classpath:public.key
```

Archivo: `src/main/resources/public.key`

```
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQE...
-----END PUBLIC KEY-----
```

**Importante:**

* Debe iniciar en la primera columna (sin espacios antes de BEGIN)
* Formato PEM X.509 (RSA pública)

Spring Boot internamente hace:

```
PEM -> RSAPublicKey -> NimbusJwtDecoder
```

Sin necesidad de escribir código.

---

## Alternativa válida: crear el Bean manualmente

Si necesitas cargar la llave desde variables de entorno, BD o Vault:

```java
@Bean
public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
    return NimbusJwtDecoder.withPublicKey(publicKey).build();
}
```

Esto es equivalente a `public-key-location`, solo que manual.

---

## Por qué `key-value` sí parecía funcionar antes

En proyectos viejos (Boot 2 + spring-security-oauth2):

```
key-value -> ResourceServerTokenServices -> JwtAccessTokenConverter
```

En Boot 3 ese código **fue eliminado completamente**. El Resource Server moderno usa `NimbusJwtDecoder`, no `JwtAccessTokenConverter`.

---

## Resumen rápido

| Configuración         | ¿Funciona en Boot 3?             |
| --------------------- | -------------------------------- |
| `key-value`           | ❌ No existe                      |
| `issuer-uri`          | ✔                                |
| `jwk-set-uri`         | ✔                                |
| `public-key-location` | ✔ (recomendado para auth legacy) |
| Bean `JwtDecoder`     | ✔                                |

---

## Checklist para un auth-server viejo

1. El token debe ser **RS256 (RSA)**, no HS256.
2. Exportar la llave pública del auth-server.
3. Guardarla en `public.key`.
4. Configurar `public-key-location`.
5. Tener `spring-boot-starter-oauth2-resource-server` en dependencias.
6. Usar:

```java
http.oauth2ResourceServer(oauth -> oauth.jwt());
```

Con eso, el microservicio Boot 3 valida tokens de un Authorization Server antiguo sin JWKS ni OIDC.
