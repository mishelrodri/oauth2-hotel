# Sistema de Microservicios con OAuth2 - Gestión de Hoteles

## 📋 Descripción General

Este proyecto es un ejercicio de aprendizaje sobre **arquitectura de microservicios** en Spring Boot con **autenticación y autorización mediante OAuth2**. El sistema simula una plataforma de gestión hotelera tipo Airbnb, donde diferentes servicios se comunican de manera segura.

### 🎯 Objetivo del Ejercicio

Aprender a implementar:

- ✅ Autenticación y autorización con OAuth2
- ✅ Servidores de autorización y recursos
- ✅ Comunicación segura entre microservicios
- ✅ Gestión de usuarios y permisos

---

## 📦 Estructura de Proyectos

### 1. **auth-server** 🔐

**Servidor de Autorización OAuth2**

**¿Qué es?**

- Servidor central de autenticación y autorización
- Implementa `spring-boot-starter-oauth2-authorization-server`
- Gestiona usuarios, roles y tokens JWT/OAuth2

**Responsabilidades:**

- Autenticar usuarios (login)
- Emitir tokens de acceso (access tokens)
- Validar credenciales contra la base de datos
- Gestionar permisos y scopes
- Proporcionar endpoints de autorización

**Tecnologías:**

- Spring Boot 3.5.8
- Spring Security
- OAuth2 Authorization Server
- Java 17

**Puerto:** `9000`

---

### 2. **hotel-service** 🏨

**Microservicio de Hoteles - Resource Server**

**¿Qué es?**

- Servicio que expone la API de hoteles
- Servidor de recursos protegido con OAuth2
- Solo acepta peticiones con tokens válidos

**Responsabilidades:**

- Proporcionar endpoints para gestionar hoteles
- Validar tokens contra el servidor de autorización
- Retornar información de hoteles autenticados
- Proteger recursos con scopes específicos

**Tecnologías:**

- Spring Boot 3.5.8
- Spring Boot OAuth2 Resource Server
- Spring Web (REST API)
- Dependencia del `oauth-server`
- Java 17

**Puerto:** `8080`

---

### 3. **habitaciones-service** 🛏️

**Microservicio de Habitaciones - Resource Server**

**¿Qué es?**

- Servicio que gestiona las habitaciones de los hoteles
- Servidor de recursos protegido con OAuth2
- Complemento del servicio de hoteles

**Responsabilidades:**

- Proporcionar endpoints para habitaciones
- Validar tokens OAuth2
- Gestionar inventario y disponibilidad
- Proteger acceso con autenticación

**Tecnologías:**

- Spring Boot 3.5.9
- Spring Boot OAuth2 Resource Server
- Spring Web
- Dependencia del `oauth-server`
- Java 17

**Puerto:** `8081`

---

### 4. **airbnb-service** 🏠

**Microservicio de Airbnb - Resource Server**

**¿Qué es?**

- Servicio para gestionar propiedades tipo Airbnb
- Servidor de recursos con autenticación OAuth2
- Usa arquitectura reactiva con WebFlux

**Responsabilidades:**

- Exponer endpoints reactivos para propiedades
- Validar tokens contra servidor de autorización
- Gestionar listados de propiedades
- Soportar múltiples usuarios/anfitriones

**Tecnologías:**

- Spring Boot 3.5.9
- Spring WebFlux (programación reactiva)
- Spring Boot OAuth2 Resource Server
- Lombok
- Java 17

**Puerto:** `8082`

---

### 5. **oauth-server** (Librería) 📚

**Librería Compartida OAuth2**

**¿Qué es?**

- Librería auxiliar que contiene configuraciones comunes
- No es un servidor ejecutable, es un JAR compartido
- Define clases comunes para validación de OAuth2

**Responsabilidades:**

- Proporcionar configuraciones OAuth2 reutilizables
- Clases y utilidades compartidas entre microservicios
- Validadores de tokens
- Configuraciones de seguridad comunes

**Tecnologías:**

- Maven (empaquetada como librería)
- Spring Security (scope `provided`)
- OAuth2 Resource Server (scope `provided`)
- Java 17

**Nota:** Se publica como dependencia Maven con versión `1.0-SNAPSHOT` que es consumida por otros servicios.

---

## 🔄 Flujo de Autenticación

```
Usuario
   ↓
[auth-server] → Autentica y emite JWT/Token
   ↓
Cliente obtiene token
   ↓
Cliente realiza petición a [hotel-service / habitaciones-service / airbnb-service]
   ↓
Resource Server valida token contra [auth-server] o [oauth-server]
   ↓
✅ Acceso concedido / ❌ Acceso denegado
```

---

## 🚀 Cómo Ejecutar

### Orden recomendado:

1. **Compilar la librería compartida:**

   ```bash
   cd oauth-server
   mvn clean install
   ```

2. **Iniciar el servidor de autorización:**

   ```bash
   cd auth-server
   mvn spring-boot:run
   ```

3. **Iniciar los microservicios de recursos (en terminales diferentes):**
   ```bash
   cd hotel-service
   mvn spring-boot:run
   ```
   ```bash
   cd habitaciones-service
   mvn spring-boot:run
   ```
   ```bash
   cd airbnb-service
   mvn spring-boot:run
   ```

---

## 📝 Endpoints Típicos

### Auth Server (Puerto 8080)

```
POST   /login              → Autenticación de usuarios
POST   /oauth/token        → Obtener token
GET    /oauth/authorize    → Autorización
```

---

## 🔑 Conceptos Clave

| Concepto                 | Descripción                                  |
| ------------------------ | -------------------------------------------- |
| **OAuth2**               | Protocolo de autorización estándar           |
| **Access Token**         | Token que permite acceder a recursos         |
| **Scope**                | Permisos específicos del token               |
| **Resource Server**      | Servicio que protege recursos con OAuth2     |
| **Authorization Server** | Servicio que emite tokens                    |
| **JWT**                  | Formato común para tokens (JSON Web Token)   |
| **Microservicios**       | Servicios independientes que trabajan juntos |

---

## 🛠️ Requisitos

- **Java 17** o superior
- **Maven** 3.6+
- **IDE:** IntelliJ IDEA, VS Code o similar
- **Git** (para control de versiones)

---

## 📚 Tecnologías Utilizadas

- **Spring Boot 3.5.8/3.5.9**
- **Spring Security**
- **Spring OAuth2 (Authorization & Resource Server)**
- **Spring WebFlux** (en airbnb-service)
- **Lombok** (reducción de boilerplate)
- **Maven** (gestión de dependencias)
- **Java 17**

## 📞 Notas

- Los puertos por defecto pueden variar según la configuración en `application.yaml`
- Es importante que el `oauth-server` esté compilado antes de los otros servicios
- Cada servicio tiene su propia configuración de seguridad en `application.yaml`
- Los tokens deben incluirse en el header `Authorization: Bearer <token>`
