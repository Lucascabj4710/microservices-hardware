# 🛒 Microservicios - Tienda de Hardware

Plataforma de e-commerce construida con **Java 17**, **Spring Boot 3.5** y **Spring Cloud 2025**, diseñada como proyecto de portfolio. El sistema gestiona un catálogo de productos de hardware y el procesamiento de pedidos a través de servicios independientes y contenerizados, orquestados con Docker Compose.

---

## Arquitectura General

```
Cliente
  │
  └──▶ [API Gateway] (:8080)       ← Filtro JWT, balanceo de carga
          │
          ├──▶ [Servicio Producto] (:8081)  ← Catálogo, gestión de stock
          │              │
          │       [MySQL product_db] (:3306)
          │
          └──▶ [Servicio Orden] (:8082)     ← Pedidos, máquina de estados
                       │
                [MySQL order_db] (:3307)
                       │
                [Servicio Producto] ← vía OpenFeign + Eureka

[Eureka Server] (:8761)        ← Registro de servicios
[Config Server] (:8888)        ← Configuración centralizada
```

Todos los servicios se registran en Eureka para discovery con balanceo de carga. Las llamadas entre servicios pasan por OpenFeign con circuit breaking y reintentos mediante Resilience4j.

---

## Servicios

| Servicio | Puerto | Descripción |
|---|---|---|
| `Microservice-Config-Server` | 8888 | Spring Cloud Config Server — sirve configuración desde un repositorio Git remoto |
| `Microservice-Eureka-Server` | 8761 | Registro de servicios Netflix Eureka |
| `Microservice-Gateway` | 8080 | Spring Cloud Gateway con filtro de autenticación JWT |
| `Microservice-Product` | 8081 | Catálogo de productos — CRUD, control de stock, búsqueda |
| `Microservice-Order` | 8082 | Gestión de pedidos — creación, cancelación, transiciones de estado |

> El Gateway también está incluido en Docker Compose como `api-gateway` y arranca junto a Product y Order, dependiendo de Config Server y Eureka.

---

## Stack Tecnológico

- **Java 17** / **Spring Boot 3.5.14**
- **Spring Cloud 2025.0.2** (Config, Eureka, Gateway, OpenFeign)
- **Resilience4j** — circuit breaker + reintentos
- **MapStruct 1.6.3** — mapeo de DTOs
- **Lombok** — reducción de boilerplate
- **MySQL 8.0** — una base de datos por servicio
- **Docker / Docker Compose** — infraestructura contenerizada

---

## Cómo Ejecutar

### Requisitos previos

- Docker y Docker Compose instalados
- Java 17 + Maven (solo para compilar los JARs antes de Docker)

### 1. Compilar cada servicio

Ejecutar desde el directorio de cada servicio:

```bash
cd Microservice-Config-Server && ./mvnw clean package -DskipTests
cd Microservice-Eureka-Server && ./mvnw clean package -DskipTests
cd Microservice-Product       && ./mvnw clean package -DskipTests
cd Microservice-Order         && ./mvnw clean package -DskipTests
```

### 2. Levantar con Docker Compose

```bash
docker-compose up --build
```

Docker Compose inicia los servicios en el orden correcto de dependencias:

```
MySQL DBs → Config Server → Eureka Server → Product / Order
```

### 3. Verificar

| URL | Qué vas a ver |
|---|---|
| http://localhost:8761 | Dashboard de Eureka — todos los servicios registrados |
| http://localhost:8888/microservice-product/default | Configuración del servicio de productos |
| http://localhost:8081/product | Listado de productos |
| http://localhost:8082/order | Listado de pedidos |

---

## API

### Servicio de Productos — `/product`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/product` | Listar todos los productos |
| `GET` | `/product/id/{id}` | Obtener producto por ID |
| `GET` | `/product/name/{name}` | Obtener producto por nombre |
| `GET` | `/product/search/{value}` | Buscar productos disponibles por nombre o marca |
| `POST` | `/product` | Crear un nuevo producto |
| `PUT` | `/product/{id}` | Actualizar un producto |
| `PUT` | `/product/{id}/status` | Activar/desactivar disponibilidad |
| `PUT` | `/product/addStock` | Agregar stock a uno o más productos |
| `PUT` | `/product/discountStock` | Descontar stock (usado internamente por el Servicio de Órdenes) |

**Body para crear producto:**
```json
{
  "name": "RTX 4090",
  "brand": "NVIDIA",
  "stock": 10,
  "price": 1599.99,
  "available": true
}
```

### Servicio de Órdenes — `/order`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/order` | Crear un nuevo pedido |
| `GET` | `/order` | Listar todos los pedidos |
| `GET` | `/order/status/{status}` | Filtrar pedidos por estado |
| `PUT` | `/order/{id}?status=CANCELED` | Cancelar un pedido (restaura el stock) |

**Estados de pedido:** `IN_PROCESS` → `COMPLETED` / `CANCELED`

**Body para crear pedido:**
```json
[
  { "productId": 1, "quantity": 2 },
  { "productId": 3, "quantity": 1 }
]
```

Al cancelar un pedido, el stock se restaura automáticamente en el Servicio de Productos vía Feign.

---

## Resiliencia

Cada llamada del Servicio de Órdenes al Servicio de Productos está protegida por:

- **Circuit Breaker** (`@CircuitBreaker`) — se abre ante fallos repetidos y ejecuta un método de fallback
- **Retry** (`@Retry`) — reintenta fallas transitorias antes de abrir el circuito
- **Fallbacks** — lanzan `ProductServiceUnavailableException` (503) para que el Gateway devuelva un error claro al cliente

---

## Configuración

El Config Server obtiene la configuración desde:

```
https://github.com/Lucascabj4710/microservices-config.git  (rama: main)
```

Cada servicio arranca con `optional:configserver:http://localhost:8888` y cae al `application.yaml` local si el Config Server no está disponible.

Docker Compose sobreescribe las propiedades clave mediante variables de entorno:

```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-product:3306/product_db
  - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka
  - SPRING_PROFILES_ACTIVE=docker
```

---

## Estructura del Proyecto

```
.
├── Microservice-Config-Server/
├── Microservice-Eureka-Server/
├── Microservice-Gateway/
├── Microservice-Product/
│   └── src/main/java/com/lucas/microservice/product/
│       ├── controller/    # Endpoints REST
│       ├── services/      # Lógica de negocio
│       ├── entities/      # Entidades JPA
│       ├── dto/           # DTOs de request/response
│       ├── mapper/        # Mappers MapStruct
│       ├── repositories/  # Spring Data JPA
│       └── exception/     # Manejo global de errores
├── Microservice-Order/
│   └── src/main/java/com/lucas/microservice/order/
│       ├── client/        # Clientes Feign
│       ├── config/        # Feign error decoder
│       ├── controller/
│       ├── service/
│       ├── entities/
│       ├── dto/
│       ├── mapper/
│       ├── repository/
│       └── exception/
└── docker-compose.yml
```

---

## Autor

**Lucas** — Desarrollador Java / Spring Boot
