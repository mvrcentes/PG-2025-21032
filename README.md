# Kaminaljuyú AR - Experiencia de Realidad Aumentada Geoespacial

## Descripción

Aplicación móvil de **Realidad Aumentada** para Android que orienta a visitantes en el **Parque Arqueológico Kaminaljuyú** (Guatemala) mediante anclajes geoespaciales y una ruta secuencial guiada. El proyecto utiliza **ARCore Geospatial API** para anclar contenidos 3D mediante coordenadas de latitud, longitud y altitud, permitiendo una experiencia de interpretación patrimonial *in situ* sin necesidad de instalar infraestructura física adicional (balizas, marcadores o señalética).

### Características principales

- **Navegación geoespacial**: Ruta guiada con 12 puntos de interés (POIs) enlazados por 70+ waypoints
- **Anclaje geoespacial**: Modelos 3D posicionados mediante coordenadas WGS84 usando ARCore Geospatial API
- **Sistema de orientación**: Flecha HUD direccional con cálculo de bearing geodésico, normalización y suavizado angular
- **Interacción táctil**: Gestos de pinch-to-zoom y rotación para manipular modelos 3D
- **Contenido interpretativo**: Fichas informativas con descripción histórica, hallazgos arqueológicos e imágenes de cada montículo
- **Fase de retorno**: Navegación de regreso al punto de inicio para completar el recorrido

### Demo y Documentación

- **Video Demo**: Ver demostración de la aplicación en [demo/demo.mp4](demo/demo.mp4)
- **Documentación Completa**: Trabajo de graduación en [docs/Trabajo de Graduación - Marco Ramirez.pdf](docs/Trabajo%20de%20Graduación%20-%20Marco%20Ramirez.pdf)

### Montículos y estructuras incluidas

| POI | Nombre | Período |
|-----|--------|---------|
| 1 | Información Inicial | - |
| 2 | Montículo 6 | Preclásico Tardío-Terminal - Clásico Tardío |
| 3 | Acrópolis | Clásico Temprano - Clásico Tardío |
| 4 | Montículo 3 | Preclásico Tardío - Clásico Tardío |
| 5 | Montículo 5 | Preclásico Tardío - Clásico Tardío |
| 6 | Montículo 7 | Preclásico Tardío-Terminal - Clásico Tardío |
| 7 | Montículo 8 | Preclásico Tardío-Terminal - Clásico Tardío |
| 8 | Montículo 12 | Preclásico Tardío-Terminal |
| 9 | La Palangana | - |
| 10 | Montículo 14 | - |
| 11 | Montículo 13 | - |

## Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Plataforma**: Android (minSdk 24, targetSdk 30)
- **ARCore**: Google Play Services for AR v1.31.0
- **ARCore Geospatial API**: Anclaje mediante latitud, longitud y altitud
- **OpenGL ES 2.0**: Renderizado de modelos 3D con shaders personalizados
- **Google Maps SDK**: Visualización de ruta y marcadores de POIs
- **Sceneform**: Renderizado de escenas AR
- **Wavefront OBJ**: Formato de modelos 3D (librería de.javagl:obj)
- **Google Play Services Location**: Servicios de ubicación de alta precisión
- **Material Design**: Componentes de UI

## Requisitos Previos

### Hardware
- Dispositivo Android compatible con [ARCore](https://developers.google.com/ar/devices)
- GPS y sensores de movimiento funcionales
- Cámara trasera

### Software
- Android 7.0 (API 24) o superior
- Google Play Services for AR instalado
- Android Studio Arctic Fox o superior (para desarrollo)
- JDK 8+

### Permisos requeridos
- `CAMERA`: Captura de video para AR
- `ACCESS_FINE_LOCATION`: Geolocalización precisa
- `INTERNET`: Acceso a servicios geoespaciales (VPS)

## Instalación

### Para usuarios finales

1. Descargar el APK desde la sección de releases
2. Habilitar "Instalar apps de fuentes desconocidas" en configuración
3. Instalar el APK
4. Conceder permisos de cámara y ubicación

### Para desarrolladores

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/mvrcentes/PG-2025-21032.git
   cd PG-2025-21032/src/android-app
   ```

2. Abrir el proyecto en Android Studio

3. Configurar API Key de Google Cloud:
   - Crear proyecto en [Google Cloud Console](https://console.cloud.google.com/)
   - Habilitar ARCore API y Maps SDK for Android
   - Generar API Key y agregarla en `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.ar.API_KEY"
       android:value="TU_API_KEY"/>
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="TU_API_KEY"/>
   ```

4. Sincronizar Gradle y compilar:
   ```bash
   ./gradlew assembleDebug
   ```

5. Instalar en dispositivo conectado:
   ```bash
   ./gradlew installDebug
   ```

## Estructura del Proyecto

```
src/
├── android-app/
│   ├── app/
│   │   └── src/main/
│   │       ├── assets/
│   │       │   ├── models/          # Modelos 3D (.obj) y texturas
│   │       │   └── shaders/         # Shaders GLSL (vertex/fragment)
│   │       ├── java/com/google/ar/core/codelabs/hellogeospatial/
│   │       │   ├── config/          # Configuración (umbrales, parámetros)
│   │       │   ├── engine/          # Gestión de objetos Earth/Anchors
│   │       │   ├── helpers/         # Utilidades (permisos, diálogos, vistas)
│   │       │   ├── models/          # Modelos de datos (POILocation, ModelPart)
│   │       │   ├── navigation/      # Sistema de navegación (RouteNavigator)
│   │       │   ├── renderers/       # Renderizado de objetos
│   │       │   ├── ui/              # Componentes UI (ArrowIndicator)
│   │       │   └── utilities/       # Cálculos geodésicos, configuración de ruta
│   │       └── res/                 # Recursos Android (layouts, drawables, strings)
│   └── gradle/
└── models/                          # Modelos 3D fuente (antes de optimización)
```

## Uso

### Iniciar el recorrido guiado

1. Abrir la aplicación y seleccionar **"Recorrido Guiado"**
2. Aceptar los permisos de cámara y ubicación
3. Ubicarse dentro del **radio de inicio** (20m del punto de inicio)
4. Presionar **"Iniciar Recorrido"**
5. Seguir la flecha direccional hacia el primer POI
6. Al llegar a cada POI:
   - Visualizar el modelo 3D superpuesto
   - Usar gestos para rotar/escalar el modelo
   - Presionar el botón de información para ver detalles
   - Presionar **"Siguiente"** para continuar
7. En el último POI, presionar **"Finalizar"** para iniciar la fase de retorno
8. Regresar al punto de inicio para completar el recorrido

### Parámetros configurables

Los umbrales de navegación se pueden ajustar en `HelloGeoConfig.kt`:

```kotlin
const val GUIDED_ARRIVAL_METERS = 10.0    // Radio de llegada a POIs
const val CONNECTOR_ARRIVAL_METERS = 2.0   // Radio para waypoints intermedios
const val TOUR_START_RADIUS_METERS = 20.0  // Radio para iniciar recorrido
const val VISIBILITY_RADIUS_METERS = 100.0 // Distancia de renderizado de modelos
```

## Resultados de Validación

En un estudio de usabilidad con **N=17 participantes**:

| Tarea | Éxito |
|-------|-------|
| Iniciar recorrido | 100% |
| Avanzar al siguiente montículo | 100% |
| Finalizar con retorno | 100% |
| Navegar al primer montículo | 94.1% |
| Interactuar con modelo 3D | 94.1% |
| Abrir información del montículo | 82.4% |

## Licencia

Este proyecto está bajo la Licencia Apache 2.0. Ver el archivo [LICENSE](src/android-app/LICENSE) para más detalles.

## Autor

**Marco Vinicio Ramirez Centes** - 21032 - Proyecto de Graduación 2025  
Universidad del Valle de Guatemala

## Agradecimientos

- Parque Arqueológico Kaminaljuyú - Ministerio de Cultura y Deportes de Guatemala
- ARCore Geospatial API - Google
- Universidad del Valle de Guatemala