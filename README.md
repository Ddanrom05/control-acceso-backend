# Sistema de Control de Acceso RFID

Backend REST para un sistema de control de acceso mediante RFID, desarrollado con **Spring Boot**, **Spring Data JPA** y **MySQL**.

El sistema está diseñado para comunicarse posteriormente con una **ESP32 + RC522**, permitiendo consultar si una tarjeta RFID tiene acceso y registrar el historial de accesos.

---

## Tecnologías

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* MySQL
* Maven Wrapper

---

## Arquitectura

```text
ESP32 + RC522
      │
      │ HTTP
      ▼
Spring Boot
      │
      │ JPA / Hibernate
      ▼
MySQL
```

El backend administra los usuarios autorizados y registra los eventos de acceso.

---

## Requisitos

Antes de ejecutar el proyecto se necesita:

* Java JDK
* MySQL Server
* Git

No es necesario instalar Maven globalmente, ya que el proyecto incluye **Maven Wrapper**.

---

# Configuración de MySQL

## 1. Crear la base de datos

Abrir MySQL y ejecutar:

```sql
CREATE DATABASE control_acceso;
```

No es necesario crear manualmente las tablas.

Hibernate las generará/actualizará automáticamente al iniciar la aplicación.

---

# Configuración de Spring Boot

En:

```text
src/main/resources/
```

existe un archivo:

```text
application.properties.example
```

Copiarlo como:

```text
application.properties
```

y modificar la contraseña de MySQL.

---

# Ejecutar el proyecto

Desde la carpeta raíz:

### macOS / Linux

```bash
./mvnw spring-boot:run
```

### Windows

```cmd
mvnw.cmd spring-boot:run
```

También puede ejecutarse desde el **Spring Boot Dashboard** de VS Code.

El servidor estará disponible en:

```text
http://localhost:8080
```

---

# Compilar el proyecto

Para comprobar que el proyecto compila correctamente:

### macOS / Linux

```bash
./mvnw clean compile
```

### Windows

```cmd
mvnw.cmd clean compile
```

---

# Base de datos

Hibernate genera las siguientes tablas:

```text
usuarios
logs_acceso
```

## Tabla `usuarios`

Contiene los usuarios registrados en el sistema.

Los usuarios tienen:

* `id`
* `uid`
* `nombre`
* `activo`

El campo `activo` permite realizar una baja lógica sin eliminar físicamente el registro.

---

## Tabla `logs_acceso`

Registra las acciones relacionadas con el sistema.

Los resultados utilizados actualmente son:

```text
ALTA
CONCEDIDO
DENEGADO
BAJA
```

---

# API REST

La API utiliza la siguiente ruta base:

```text
http://localhost:8080/api
```

---

## 1. Verificar acceso

### GET

```text
/api/acceso/{uid}
```

Ejemplo:

```text
GET /api/acceso/84368A8E
```

### Usuario autorizado

```json
{
  "autorizado": true,
  "uid": "84368A8E",
  "nombre": "Diana"
}
```

### Usuario no autorizado

```json
{
  "autorizado": false,
  "uid": "11223344"
}
```

Cada consulta registra automáticamente:

```text
CONCEDIDO
```

o:

```text
DENEGADO
```

---

# 2. Registrar usuario

### POST

```text
/api/usuarios
```

### Body

```json
{
  "uid": "84368A8E",
  "nombre": "Diana"
}
```

### Respuesta

```json
{
  "exito": true,
  "uid": "84368A8E",
  "mensaje": "Usuario registrado"
}
```

La operación registra:

```text
ALTA
```

en el historial.

Si el UID ya existía pero estaba dado de baja, el usuario se reactiva.

---

# 3. Dar de baja un usuario

### DELETE

```text
/api/usuarios/{uid}
```

Ejemplo:

```text
DELETE /api/usuarios/84368A8E
```

### Respuesta exitosa

```json
{
  "eliminado": true,
  "uid": "84368A8E"
}
```

La operación realiza una baja lógica y registra:

```text
BAJA
```

---

# 4. Obtener usuarios activos

### GET

```text
/api/usuarios
```

### Ejemplo de respuesta

```json
[
  {
    "uid": "84368A8E",
    "nombre": "Diana"
  }
]
```

Solamente se muestran usuarios activos.

---

# 5. Obtener historial

### GET

```text
/api/logs
```

### Ejemplo

```json
[
  {
    "id": 1,
    "uid": "84368A8E",
    "resultado": "ALTA",
    "fechaHora": "2026-10-07T20:00:00"
  },
  {
    "id": 2,
    "uid": "84368A8E",
    "resultado": "CONCEDIDO",
    "fechaHora": "2026-10-07T20:01:00"
  }
]
```

---

# Pruebas con Insomnia

Los endpoints pueden probarse utilizando **Insomnia**.

Orden recomendado:

```text
1. POST /api/usuarios
2. GET  /api/usuarios
3. GET  /api/acceso/{uid}
4. GET  /api/acceso/{uid-no-autorizado}
5. GET  /api/logs
6. DELETE /api/usuarios/{uid}
7. GET  /api/acceso/{uid}
8. GET  /api/logs
```

---

# Integración con ESP32

La ESP32 utilizará principalmente:

```text
GET /api/acceso/{uid}
```

El flujo será:

```text
RC522
  │
  ▼
UID RFID
  │
  ▼
ESP32
  │
  │ HTTP GET
  ▼
Spring Boot
  │
  ▼
MySQL
  │
  ▼
Respuesta JSON
  │
  ▼
ESP32
  │
  ├── autorizado → abrir acceso
  │
  └── no autorizado → denegar acceso
```

La ESP32 se conectará al servidor mediante la dirección IP local de la computadora donde se encuentre ejecutándose Spring Boot.

---

# Estado actual

Backend funcional y probado mediante Insomnia.

Actualmente están implementados:

* [x] Registro de usuarios
* [x] Baja lógica de usuarios
* [x] Reactivación de usuarios
* [x] Consulta de usuarios activos
* [x] Verificación de acceso RFID
* [x] Registro de accesos concedidos
* [x] Registro de accesos denegados
* [x] Registro de altas
* [x] Registro de bajas
* [x] Consulta de historial
