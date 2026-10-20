package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;

public class FpsModule extends HudModule {

    public FpsModule() {
        super("fps", "FPS", true);
    }

    @Override
    public String getText() {
        return MinecraftClient.getInstance().getCurrentFps() + " FPS";
    }
}
