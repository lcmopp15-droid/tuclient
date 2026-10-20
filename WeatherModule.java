package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import net.minecraft.client.MinecraftClient;

/** Quita la lluvia y las tormentas solo en tu pantalla. */
public class WeatherModule extends ClientModule {

    public WeatherModule() {
        super("weather", "Clima despejado", "No ves lluvia ni tormenta, aunque el servidor la tenga.",
                Category.GAME, false);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.world == null) {
            return;
        }
        client.world.setRainGradient(0f);
        client.world.setThunderGradient(0f);
    }
}
