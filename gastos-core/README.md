#gastos-core

Biblioteca desarrollada con Maven que contiene la lógica de negocio del sistema ControlMoney.

Este módulo forma parte del proyecto académico ControlMoney, desarrollado para el curso de Programación 2 de la Universidad Mariano Gálvez de Guatemala.

##Objetivo

Separar la lógica de negocio de la interfaz gráfica de Android, proporcionando una biblioteca reutilizable para administrar ingresos, gastos y operaciones financieras.

La aplicación Android consume esta biblioteca como una dependencia Maven.

## ✨ Funcionalidades principales

- Registrar ingresos.
- Registrar gastos.
- Validar que las descripciones no estén vacías.
- Validar que los montos sean mayores que cero.
- Consultar todos los movimientos.
- Buscar movimientos por identificador.
- Actualizar movimientos.
- Eliminar movimientos.
- Calcular el total de ingresos.
- Calcular el total de gastos.
- Calcular el saldo disponible.

##Modelo de datos

### Gasto

Representa un movimiento financiero y contiene información como:

- Identificador.
- Descripción.
- Monto.
- Categoría.
- Fecha.
- Tipo de movimiento.

###Categoria

Enumera las categorías disponibles para clasificar los gastos:

```java
ALIMENTOS,
TRANSPORTE,
SERVICIOS,
ENTRETENIMIENTO,
OTROS
```

###TipoMovimiento

Identifica la naturaleza del movimiento:

```java
INGRESO,
GASTO
```

##Arquitectura

El módulo está organizado en componentes que separan los modelos, el acceso a los datos y las operaciones del negocio.

```text
gastos-core
│
└── src
    └── main
        └── java
            └── umg.edu.gt.progra2.controlgastos
                │
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

### Componentes

**GastoRepository**

Define las operaciones para registrar, consultar, buscar, actualizar y eliminar movimientos.

**GastoRepositoryEnMemoria**

Implementa el repositorio utilizando una colección en memoria para administrar los movimientos.

**ControlGastos**

Contiene las reglas de negocio, validaciones y operaciones para calcular los totales financieros.

## Tecnologías utilizadas

- Java 11.
- Maven.
- Programación orientada a objetos.
- Enumeraciones (`enum`).
- Interfaces.
- Excepciones y validaciones.

## 📦 Información Maven

| Propiedad | Valor |
|---|---|
| Group ID | `umg.edu.gt.progra2.controlgastos` |
| Artifact ID | `gastos-core` |
| Version | `1.1.0` |
| Packaging | `jar` |
| Java | 11 |

## 🚀 Compilación e instalación

### 1. Abrir el proyecto

Abrir el directorio `gastos-core` en IntelliJ IDEA.

### 2. Compilar e instalar

Desde la terminal, ejecutar:

```bash
mvn clean install
```

Este comando:

1. Limpia los archivos de compilaciones anteriores.
2. Compila las clases Java.
3. Ejecuta las pruebas configuradas en el proyecto.
4. Empaqueta la biblioteca en formato JAR.
5. Instala el artefacto en el repositorio local de Maven cuando la compilación finaliza correctamente.

### 3. Integración con Android

La aplicación ControlMoney Android utiliza esta biblioteca mediante una dependencia Maven.

Coordenadas:

```xml
<dependency>
    <groupId>umg.edu.gt.progra2.controlgastos</groupId>
    <artifactId>gastos-core</artifactId>
    <version>1.1.0</version>
</dependency>
```

La configuración de la dependencia debe coincidir con las coordenadas declaradas en el `pom.xml` del módulo.

##Relación con ControlMoney Android

La biblioteca `gastos-core` proporciona las operaciones de negocio y la aplicación Android las utiliza para gestionar los movimientos desde su interfaz.

La persistencia Room de la aplicación Android permite conservar los datos localmente; el repositorio en memoria incluido en esta biblioteca es una implementación de repositorio independiente.

