package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

/** Zoom mientras mantienes una tecla (C por defecto). */
public class ZoomModule extends ClientModule {

    private static KeyBinding key;

    private final NumberSetting level = register(new NumberSetting("level", "Nivel de zoom", 1.5, 10, 0.5, 4));
    private final BooleanSetting smooth = register(new BooleanSetting("smooth", "Zoom suave", true));

    private float progress;
    private long lastFrame = System.nanoTime();

    public ZoomModule() {
        super("zoom", "Zoom", "Mantén la tecla C para acercar la vista.", Category.VISUAL, true);
    }

    public static void setKey(KeyBinding keyBinding) {
        key = keyBinding;
    }

    /** Recibe el FOV calculado por el juego y devuelve el FOV con el zoom aplicado. */
    public double apply(double fov) {
        long now = System.nanoTime();
        float dt = Math.min((now - lastFrame) / 1_000_000_000f, 0.1f);
        lastFrame = now;

        boolean active = isEnabled() && key != null && key.isPressed()
                && MinecraftClient.getInstance().currentScreen == null;
        float target = active ? 1f : 0f;

        if (smooth.get()) {
            progress += (target - progress) * (1f - (float) Math.exp(-dt * 16f));
        } else {
            progress = target;
        }

        if (progress < 0.001f) {
            progress = 0f;
            return fov;
        }

        double factor = 1.0 + (level.get() - 1.0) * progress;
        double half = Math.toRadians(fov) / 2.0;
        return Math.toDegrees(2.0 * Math.atan(Math.tan(half) / factor));
    }
}
