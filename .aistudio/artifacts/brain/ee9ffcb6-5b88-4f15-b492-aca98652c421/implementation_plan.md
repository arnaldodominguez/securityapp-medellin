# Plan de Integración: GPS en Tiempo Real, Mapa OSM y Telemetría Bluetooth BLE para ESP32-C3 Mini

Plan técnico para incorporar geolocalización satelital en vivo, cartografía real con OpenStreetMap y comunicación Bluetooth Low Energy (BLE) con el brazalete inteligente ESP32-C3 mini, manteniendo intacto el diseño Figma de las demás pantallas.

## User Review & Critical Decisions

> [!IMPORTANT]
> El usuario especificó que **no se debe modificar el resto de la aplicación** (Comunidad, Registro, Nosotros y el diseño visual Figma deben permanecer intactos). Los cambios se concentran exclusivamente en el mapa real, el motor de GPS y el servicio Bluetooth para el ESP32-C3.

- **Codificación hacia el ESP32-C3**:
  - `1`: Zona ROJA (Peligro / Alto riesgo)
  - `2`: Zona AMARILLA (Precaución / Riesgo moderado)
  - `3`: Zona VERDE (Seguro / Bajo riesgo)
  - `4`: Sin señal GPS o GPS desactivado
- **Modo Bluetooth**: Escaneo y auto-conexión por nombre de dispositivo BLE (`ESP32_BRAZALETE`, `ESP32-C3`, o configurable), utilizando servicio GATT estándar con característica de escritura UART/datos.
- **Cartografía**: Mosaicos reales y gratuitos de **OpenStreetMap** integrados dentro del contenedor redondeado Figma de 280dp, mostrando nombres de calles, avenidas y polígonos semafóricos translúcidos (Rojo, Amarillo, Verde).
- **Notificaciones de Estado**: Notificación oficial de Android (`NotificationManager`) y banner de estado visual en la cabecera del mapa en caso de desconexión del brazalete.

---

## 1. Overview & Core Concept

- **Qué hace**:
  1. Activa el chip GPS del teléfono en tiempo real (`FusedLocationProviderClient`) y localiza al usuario en el mapa de Medellín.
  2. Determina mediante algoritmo de contención geográfica (*Point-in-Polygon*) en qué comuna se encuentra el usuario y clasifica el riesgo en **Rojo**, **Amarillo** o **Verde**.
  3. Establece enlace Bluetooth BLE con el ESP32-C3 mini y transmite continuamente el código correspondiente (`1`, `2`, `3` o `4`).
  4. Si la señal Bluetooth con el brazalete se interrumpe, alerta inmediatamente al usuario mediante una notificación del sistema y un indicador en el mapa.
- **Hardware Objetivo**: Microcontrolador ESP32-C3 mini con antena BLE (Brazalete táctico).

---

## 2. User Experience & Visual Design (MapScreen)

- **Contenedor del Mapa Figma**:
  - Mantiene la tarjeta redondeada de 20dp con borde `#E2ECEB`.
  - Renderiza el mapa real con mosaicos de **OpenStreetMap**, con zoom, paneo interactivo, calles reales de Medellín y marcador de ubicación del usuario (punto azul con halo de precisión GPS).
  - Superpone los polígonos de las 16 comunas coloreados según la regla semafórica:
    - **Rojo** (`#EF4444`): Comunas de atención prioritaria.
    - **Amarillo** (`#F59E0B`): Comunas de precaución media.
    - **Verde** (`#10B981`): Comunas seguras / baja incidencia.
- **Barra de Telemetría ESP32 & GPS**:
  - Encima del mapa, un indicador compacto de estado:
    - `📡 GPS: Conectado (Comuna 10 • ROJO)` o `⚠️ Sin señal GPS`
    - `📶 ESP32: Conectado [Transmitiendo: 1]` o `🔴 Brazalete Desconectado`
  - Botón táctil para reconectar o escanear el brazalete manualmente si se pierde el enlace.
- **Notificaciones Android**:
  - Canal de notificación `esp32_channel` con prioridad alta para alertar: *"Brazalete ESP32 desconectado: revisa la proximidad del dispositivo"*.

---

## 3. Key Product Decisions & Trade-Offs

- **Decisión 1: BLE (Bluetooth Low Energy) Nativo para ESP32-C3**
  - *Enfoque*: Utilizar `BluetoothLeScanner` y `BluetoothGatt` con reconexión automática en segundo plano.
  - *Por qué*: El ESP32-C3 mini solo cuenta con radio BLE 5.0 (no soporta Bluetooth Classic SPP). Requiere bajo consumo de batería en el celular y en el brazalete.
- **Decisión 2: Clasificación de Comunas a 3 Niveles (1, 2, 3)**
  - *Enfoque*: Mapear el índice de riesgo actual (1 a 7) en los 3 estados requeridos por el usuario:
    - Riesgo > 60% -> `1` (ROJO)
    - Riesgo entre 35% y 60% -> `2` (AMARILLO)
    - Riesgo < 35% -> `3` (VERDE)
- **Decisión 3: Arquitectura sin tocar pantallas secundarias**
  - *Enfoque*: Encapsular el servicio en `BluetoothBleManager` y `LocationTrackerService`, consumidos por `MapViewModel` y `MapScreen`. Ningún archivo de `Community`, `Auth` o `About` será alterado.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                   Sensores del Dispositivo             │
│   FusedLocationProviderClient   ·   BluetoothLeScanner │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│                 Location & BLE Engine                  │
│       • GpsLocationTracker (Lat/Lng en vivo)           │
│       • ComunaClassifier (1=Rojo, 2=Amar, 3=Verde, 4)  │
│       • Esp32BleManager (Escaneo, Conexión, GATT TX)   │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│                    MapViewModel & UI                   │
│   • MapScreen (Buscador + Mapa OSM Real + Estado BLE)  │
│   • OpenStreetMap Viewer con Tiles Reales de Medellín  │
│   • NotificationManager (Alerta de desconexión ESP32)  │
└────────────────────────────────────────────────────────┘
```

- **Permisos requeridos en `AndroidManifest.xml`**:
  - `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADMIN`, `BLUETOOTH`.
  - `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`.
  - `POST_NOTIFICATIONS`.
- **Mapeo de Estados**:
  - `gpsState`: `LOCATING`, `LOCKED(lat, lng)`, `NO_SIGNAL` -> Emite `4` al BLE.
  - `esp32ConnectionState`: `DISCONNECTED`, `SCANNING`, `CONNECTED(rssi)`.
  - `activeSignal`: `1`, `2`, `3` o `4`.
