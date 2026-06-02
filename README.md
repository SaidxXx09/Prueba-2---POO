# Sistema Gestor de Licencias

Sistema de escritorio para la gestión de licencias de conducir, desarrollado en Java. Permite registrar conductores, validar sus documentos, gestionar pruebas psicométricas, emitir licencias y generar reportes en PDF, todo conectado a una base de datos en la nube.

---

## ¿Qué hace este sistema?

- Inicio de sesión con roles de usuario (acceso diferenciado según el rol).
- Gestión de conductores: registrar, consultar y administrar datos personales.
- Validación de documentos del conductor antes de proceder.
- Registro y seguimiento de pruebas psicométricas.
- Emisión de licencias de conducir (tipos A, B, C, D, E, F según normativa ecuatoriana).
- Consulta de licencias emitidas.
- Generación de documentos en **PDF** con los datos de la licencia.

---

## Tecnologías utilizadas

| Herramienta | Para qué se usa |
|---|---|
| Java 21 | Lenguaje principal del sistema |
| Swing (Java GUI) | Interfaz gráfica de ventanas |
| PostgreSQL (Railway) | Base de datos en la nube |
| iText PDF 5 | Generación de documentos PDF |
| Gradle | Gestión del proyecto y dependencias |
| IntelliJ IDEA | IDE recomendado para el desarrollo |

---

## Requisitos previos

- [Java JDK 21](https://adoptium.net/) o superior
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recomendado) o cualquier IDE compatible con Java
- Conexión a internet (el sistema se conecta a la base de datos en Railway)

> **Nota:** No es necesario instalar PostgreSQL localmente porque la base de datos ya está configurada en la nube.

---

## Cómo ejecutar el proyecto

**Opción 1 — Desde IntelliJ IDEA:**

1. Abre IntelliJ IDEA y selecciona `Open` → elige la carpeta `SistemaGestorLicencias-main`.
2. Espera a que Gradle descargue las dependencias automáticamente.
3. Ejecuta la clase `LoginView` o usa el botón de Run.

**Opción 2 — Desde la terminal:**

```bash
# Entra a la carpeta del proyecto
cd SistemaGestorLicencias-main

# Ejecuta con Gradle
./gradlew run          # Linux / macOS
gradlew.bat run        # Windows
```

Al iniciar, el sistema mostrará una pantalla de bienvenida y verificará la conexión a la base de datos antes de abrir el login.

---

## Estructura del proyecto

```
src/main/java/ec/edu/sistemalicencias/
├── config/         # Configuración de conexión a la base de datos
├── controller/     # Lógica que comunica la vista con los servicios
├── dao/            # Acceso directo a la base de datos (Conductor, Licencia, Prueba)
├── model/
│   ├── entities/   # Clases principales: Conductor, Licencia, PruebaPsicometrica
│   ├── exceptions/ # Errores personalizados del sistema
│   └── interfaces/ # Contratos de validación y persistencia
├── service/        # Reglas de negocio (validaciones, cálculos)
├── util/           # Generador de PDFs
├── view/           # Ventanas de la aplicación (Login, Menú, Formularios)
└── Main.java       # Punto de entrada del programa
```

---

## Pantallas del sistema

| Ventana | Descripción |
|---|---|
| `LoginView` | Inicio de sesión con usuario y contraseña |
| `MainView` | Menú principal con acceso a todos los módulos |
| `GestionConductoresView` | Registro y administración de conductores |
| `ValidarDocumentosView` | Revisión y validación de documentos |
| `PruebasPsicometricasView` | Registro de resultados de pruebas |
| `EmitirLicenciaView` | Formulario para emitir una nueva licencia |
| `ConsultarLicenciasView` | Búsqueda y consulta de licencias emitidas |

---

## Tipos de licencia soportados

| Tipo | Descripción |
|---|---|
| A | Motocicletas y ciclomotores |
| B | Vehículos livianos hasta 3.500 kg |
| C | Vehículos pesados de carga |
| D | Transporte público de pasajeros |
| E | Vehículos especiales y maquinaria |
| F | Transporte comercial profesional |

---

## Autores

| Nombre |
|---|
| Said Quinto |
| Alejandro Fabara |
