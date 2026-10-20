package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class CoordsModule extends HudModule {

    public CoordsModule() {
        super("coords", "Coordenadas", true);
    }

    @Override
    public String getText() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) {
            return "XYZ: -";
        }
        return "XYZ: " + p.getBlockX() + " " + p.getBlockY() + " " + p.getBlockZ();
    }
}
