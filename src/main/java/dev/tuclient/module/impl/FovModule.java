package dev.tuclient.module.impl;

import dev.tuclient.module.ClientModule;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;

/** FOV propio, independiente de la opción de Minecraft, con modo "estático". */
public class FovModule extends ClientModule {

    private final NumberSetting fov = register(new NumberSetting("fov", "FOV", 30, 110, 1, 90));
    private final BooleanSetting staticFov = register(new BooleanSetting("static", "FOV estático", true));

    public FovModule() {
        super("fov", "Control de FOV", false);
    }

    /** Si es true, se quitan los cambios de FOV por sprint y velocidad. */
    public boolean isStatic() {
        return staticFov.get();
    }

    /** Convierte el FOV calculado por el juego en el FOV deseado. */
    public double scale(double original) {
        double vanilla = MinecraftClient.getInstance().options.getFov().getValue();
        if (vanilla <= 0) {
            return original;
        }
        return original * fov.get() / vanilla;
    }
}
