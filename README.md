# Smart Alert

**A Smart India Hackathon 2018 flood-awareness prototype that combines a solar-deployable water-level sensor with an Android safety map.**

Smart Alert is designed for flood-prone areas where people need a quick, local view of hazardous water levels. A field unit measures the clearance to the water surface with an ultrasonic sensor, calculates the water level from a calibrated baseline, and uploads the reading over a network connection. The companion Android app displays the reading on a map, supports authenticated users and administrators, and includes help/feedback flows for people who need attention during an emergency.

> This repository preserves a hackathon prototype. Treat it as a learning and demonstration project, not as an emergency-management system or a substitute for official evacuation guidance.

## What is included

| Area | Location | Purpose |
| --- | --- | --- |
| Water-level firmware | `Smart Alert/Smart Alert.ino` | Arduino sketch for ultrasonic measurement, calibration, local alarm, Wi-Fi/ESP8266 connectivity, and ThingSpeak upload. |
| Sensor helper | `Smart Alert/ultra.h` | HC-SR04-compatible distance measurement with an echo timeout. |
| MQTT experiment | `Smart Alert/mqtt_esp8266/` | Separate ESP8266/MQTT proof of concept; it is not used by the primary sketch. |
| Android client | `Android App/` | Java Android application with Firebase authentication, Maps, help requests, and administrator alert UI. |
| Project material | `smart alert.pdf` | Original project documentation. |

## System flow

```text
Ultrasonic sensor
       ↓
Arduino + ESP8266 → ThingSpeak water-level channel → Android map
       ↓                                                ↓
Local LED alarm                                  Users / rescue coordination
```

The device is calibrated to record the sensor-to-ground distance. At each reading it derives the water level as:

```text
water level = calibrated sensor-to-ground distance - current sensor-to-water distance
```

The current Arduino sketch activates the local alarm at 30 cm and uploads no more often than once every 20 seconds, respecting ThingSpeak's public-channel update interval.

## Hardware

- Arduino-compatible board
- HC-SR04 or compatible ultrasonic sensor
- ESP8266 Wi-Fi module for the checked-in primary firmware
- LED/buzzer alarm connected to the pins configured in the sketch
- Battery and solar charging hardware for field deployment

The project concept also describes GSM-based reporting. The checked-in primary sketch currently uses an ESP8266/ThingSpeak path; the MQTT sketch is a separate connectivity experiment.

## Run the Arduino prototype

1. Install the Arduino libraries **ThingSpeak** and **WiFiEsp** from the Library Manager. Use the appropriate board and ESP8266 serial configuration for your hardware.
2. Copy [`Smart Alert/secrets.example.h`](Smart%20Alert/secrets.example.h) to `Smart Alert/secrets.h`.
3. Set your Wi-Fi and ThingSpeak channel values in the local `secrets.h` file. It is intentionally ignored by Git.
4. Open `Smart Alert/Smart Alert.ino` in the Arduino IDE, verify `trigPin`, `echoPin`, serial pins, and alarm pins for your wiring, then upload.
5. Keep the sensor aimed at its dry reference surface during startup. The sketch performs a five-second countdown and uses that measurement as its calibration baseline.

Never rely on a single sensor for safety-critical flood decisions. Test the enclosure, power system, sensor mounting, network coverage, and failure modes in a controlled environment first.

## Open the Android app

1. Open `Android App/` in Android Studio.
2. Add a restricted Google Maps Android key by copying `Android App/app/src/debug/res/values/google_maps_api.example.xml` to `google_maps_api.xml` in the same directory and replacing the placeholder.
3. Download `google-services.json` for your own Firebase project and place it at `Android App/app/google-services.json`. It is intentionally ignored by Git. Review Firebase Authentication, Realtime Database rules, Cloud Messaging, and the Maps API restriction before running the app.
4. Add this to your untracked `Android App/local.properties` if you have a trusted alert-dispatch service:

   ```properties
   ALERTS_ENDPOINT=https://your-server.example/send-alert
   ```

5. Build and run from Android Studio. This is a legacy Android project (Gradle 4.10.1 / Android Gradle Plugin 3.3.2), so an older compatible Android Studio/JDK setup may be required.

The app's current map screen reads a single ThingSpeak channel and shows a fixed prototype location. Scaling it to regional coverage requires a backend that records device identity, location, calibration data, health, and time-series readings for each deployed unit.

## Security and configuration

- Credentials, API keys, and device-specific settings belong only in ignored local files. The repository includes templates, never populated secrets.
- The Android client no longer embeds a Firebase Cloud Messaging server key. Administrator alerts must go through a trusted backend or Firebase Cloud Function configured by `ALERTS_ENDPOINT`.
- Restrict Google Maps API keys by Android package name and signing certificate, and restrict Firebase rules before any public deployment.
- If this repository was previously public, rotate any Wi-Fi, ThingSpeak, Firebase, Maps, or messaging credentials that were committed in its history.

## Future direction

- Add a backend/device registry for multiple sensor locations and calibrated safety thresholds.
- Visualize safe, caution, and high-risk zones using live data and clearly labelled confidence/freshness indicators.
- Support secure over-the-air firmware updates, battery/solar diagnostics, and offline buffering.
- Combine sensor history, weather data, and drainage information for flood forecasting and maintenance planning.
- Provide moderated community reports for passable roads and urgent rescue needs.

## License

No license has been declared for this repository. Add one before distributing or reusing the code.
