#ControlMoney

### Aplicación móvil para el control de ingresos y gastos personales

## Información académica

**Universidad:** Universidad Mariano Gálvez de Guatemala (UMG)  
**Curso:** Programación 2  
**Proyecto:** ControlMoney  
**Estudiante:** Edward Meyer Natareno Garcia

---

ControlMoney es un proyecto desarrollado para el curso de **Programación 2 de la Universidad Mariano Gálvez de Guatemala (UMG)**. Su propósito es facilitar el registro, organización y seguimiento de los movimientos financieros personales mediante una aplicación Android.

El proyecto está dividido en dos componentes principales: una aplicación móvil Android y una biblioteca de lógica de negocio desarrollada con kotlin y Maven.

---

## Componentes del proyecto

### 1. ControlMoney Android

Aplicación móvil que permite al usuario administrar sus finanzas personales desde su dispositivo Android.

**Características principales:**

- Registro de datos personales del usuario.
- Registro de ingresos y gastos.
- Clasificación de gastos por categorías.
- Cálculo automático del saldo disponible.
- Historial de movimientos financieros.
- Consulta y edición de movimientos.
- Filtros para consultar movimientos.
- Reportes financieros.
- Comparación de ingresos y gastos de los últimos seis meses.
- Persistencia de información mediante Room.

**Tecnologías utilizadas:**

- Kotlin
- Android Studio
- Android SDK
- XML para interfaces gráficas
- View Binding
- Room
- Gradle

### 2. gastos-core

Biblioteca desarrollada en Java con Maven que contiene la lógica de negocio del sistema de control de gastos.

Su objetivo es separar las operaciones financieras de la interfaz gráfica de Android, permitiendo mantener una arquitectura modular y reutilizable.

**Responsabilidades principales:**

- Validación de ingresos y gastos.
- Registro de movimientos.
- Consulta de movimientos.
- Búsqueda por identificador.
- Actualización y eliminación de movimientos.
- Cálculo de ingresos, gastos y saldo disponible.
- Administración de categorías y tipos de movimiento.

**Tecnologías utilizadas:**

- Java 11
- Maven
- Programación orientada a objetos

---

## Arquitectura del proyecto

El proyecto utiliza una separación entre la aplicación móvil y la lógica de negocio.

```text
ControlMoney
│
├── ControlMoney Android
│   ├── Interfaz gráfica
│   ├── Actividades
│   ├── Navegación
│   ├── Persistencia Room
│   └── Consumo de gastos-core
│
└── gastos-core
    ├── modelo
    │   ├── Gasto
    │   ├── Categoria
    │   └── TipoMovimiento
    │
    ├── datos
    │   ├── GastoRepository
    │   └── GastoRepositoryEnMemoria
    │
    └── servicio
        └── ControlGastos
```

La aplicación Android se encarga de la interacción con el usuario, mientras que `gastos-core` proporciona las operaciones y reglas de negocio.

La aplicación utiliza Room para conservar los movimientos registrados.

---

## Tipos de movimiento

El sistema contempla dos tipos de movimientos:

| Tipo | Descripción |
|---|---|
| INGRESO | Dinero que recibe el usuario. |
| GASTO | Dinero que utiliza el usuario. |

Los gastos se pueden clasificar en las siguientes categorías:

- Alimentos
- Transporte
- Servicios
- Entretenimiento
- Otros

El saldo disponible se calcula restando los gastos de los ingresos.

**Saldo disponible = Total de ingresos - Total de gastos**

---

## Requisitos de desarrollo

Para trabajar con el proyecto se utilizan las siguientes herramientas:

- JDK 11
- Maven 3.9 o superior
- Android Studio
- Gradle
- Dispositivo Android o emulador compatible

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/enatarenog2/Control_Money.git
```

### 2. Instalar la biblioteca Maven

Ubicarse en el directorio de `gastos-core` y ejecutar:

```bash
mvn clean install
```

Este comando compila la biblioteca y la instala en el repositorio local de Maven.

### 3. Abrir la aplicación Android

- Abrir el proyecto Android en Android Studio.
- Sincronizar el proyecto con Gradle.
- Comprobar que la dependencia `gastos-core` esté disponible.
- Ejecutar la aplicación en un dispositivo Android o emulador.

> Nota: La ruta de los módulos y la configuración de la dependencia deben corresponder con la estructura actual del repositorio.

---

