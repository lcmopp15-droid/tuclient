package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;

public class FpsModule extends HudModule {

    public FpsModule() {
        super("fps", "FPS", "Muestra los fotogramas por segundo.", true);
        setDefaultPosition(6, 6);
    }

    @Override
    public String getLabel() {
        return "FPS";
    }

    @Override
    public String getValue() {
        return String.valueOf(MinecraftClient.getInstance().getCurrentFps());
    }
}
