# TuClient (Minecraft 1.20.1 · Fabric)

Cliente ligero de visuales y HUD, del lado del cliente.

## Cómo abrirlo
1. Instala **JDK 17** e **IntelliJ IDEA**.
2. En IntelliJ: `File > Open` y elige la carpeta del proyecto (o el `build.gradle`). Espera a que Gradle sincronice.
3. Ejecuta la configuración **Minecraft Client** (o `gradlew runClient`).
4. Para generar el `.jar`: `gradlew build` -> `build/libs/tuclient-1.0.0.jar`.

> Si no existe `gradlew`, instala Gradle 8.7+ y ejecuta una vez `gradle wrapper`, o deja que IntelliJ use el wrapper.

## Uso en el juego
- **Right Shift**: abre el menú.
- Clic izquierdo en un módulo: activar/desactivar. Clic derecho: ver sus ajustes.
- **Editar HUD**: arrastra los módulos a donde quieras.
- Los ajustes se guardan en `.minecraft/config/tuclient.json`.

## Cómo añadir un módulo
1. Crea una clase en `module/impl` que extienda `HudModule` (texto en pantalla) o `ClientModule`.
2. Regístrala en `ModuleManager.init()`.

## Cambiar el nombre del cliente
Cambia `id`/`name` en `fabric.mod.json`, `MOD_ID` en `TuClient.java`, `archives_base_name` en `gradle.properties`, el nombre de la carpeta `assets/tuclient` y las claves de los archivos de idioma.
