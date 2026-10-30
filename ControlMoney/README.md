# ControlMoney Android

Aplicación móvil desarrollada en Kotlin para administrar ingresos y gastos personales. Forma parte del proyecto académico ControlMoney, desarrollado para el curso de Programación 2 de la Universidad Mariano Gálvez de Guatemala.

##Objetivo

Facilitar el control de las finanzas personales mediante el registro de ingresos y gastos, la consulta del historial de movimientos y la generación de reportes financieros.

## ✨ Funcionalidades

###Datos del usuario
- Registro de información personal.
- Acceso a la aplicación después de completar los datos del usuario.

###Registro de movimientos
- Registro de ingresos.
- Registro de gastos.
- Selección de categorías para los gastos.
- Selección de fecha.
- Validación de los datos ingresados.

###Pantalla principal
- Resumen de ingresos del mes actual.
- Resumen de gastos del mes actual.
- Visualización del saldo disponible.
- Acceso a las diferentes secciones de la aplicación.

###Historial de movimientos
- Consulta de ingresos y gastos registrados.
- Visualización de información de cada movimiento.
- Filtros de movimientos.
- Edición de movimientos existentes.

###Reportes
- Resumen financiero.
- Consulta por diferentes períodos.
- Distribución de gastos por categoría.
- Comparación de ingresos y gastos de los últimos seis meses.

###Persistencia de datos
La aplicación utiliza Room para almacenar los movimientos financieros y conservar la información después de cerrar o reiniciar la aplicación.

---

##Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje de programación de Android. |
| Android Studio | Entorno de desarrollo. |
| XML | Diseño de interfaces. |
| View Binding | Acceso a vistas de los layouts. |
| Room | Persistencia de datos. |
| Gradle | Gestión y compilación del proyecto. |
| Maven | Integración de la biblioteca de lógica de negocio. |

##Arquitectura

La aplicación Android se encarga de la interfaz gráfica, navegación, captura de información y persistencia local.

La lógica de negocio se encuentra separada en la biblioteca `gastos-core`, que proporciona las operaciones para administrar los movimientos financieros.

```text
ControlMoney Android
│
├── MainActivity
│   └── Registro de datos del usuario
│
├── InicioActivity
│   └── Resumen financiero
│
├── RegistrarMovimientoActivity
│   └── Registro de ingresos y gastos
│
├── HistorialActivity
│   └── Consulta y edición de movimientos
│
├── ReportesActivity
│   └── Resúmenes y gráficas
│
└── Persistencia
    └── Room
```

##Requisitos

- Android Studio.
- JDK 11.
- Android SDK configurado para el proyecto.
- Gradle.
- Dispositivo Android compatible o emulador.

**Configuración del proyecto:**

- `minSdk`: 24.
- `compileSdk`: 37.
- `targetSdk`: 37.
- Lenguaje: Kotlin.
- View Binding habilitado.

## 🚀 Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/enatarenog2/Control_Money.git
```

### 2. Preparar la dependencia Maven

La aplicación consume la biblioteca `gastos-core`.

Si se trabaja con el código fuente de ambos proyectos, primero se debe instalar la biblioteca en el repositorio local de Maven:

```bash
mvn clean install
```

Ejecutar el comando desde el directorio de `gastos-core`.

### 3. Abrir en Android Studio

1. Abrir la carpeta del proyecto Android.
2. Esperar la sincronización de Gradle.
3. Verificar que no existan errores de dependencias.
4. Seleccionar un dispositivo Android.
5. Ejecutar `MainActivity`.
