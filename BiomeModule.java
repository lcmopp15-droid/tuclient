package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;

public class BiomeModule extends HudModule {

    public BiomeModule() {
        super("biome", "Bioma", "El bioma en el que estás.", false);
        setDefaultPosition(170, 86);
    }

    @Override
    public String getLabel() {
        return "Bioma";
    }

    @Override
    public String getValue() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) {
            return "-";
        }
        String path = mc.world.getBiome(mc.player.getBlockPos())
                .getKey()
                .map(key -> key.getValue().getPath())
                .orElse("?");
        String pretty = path.replace('_', ' ');
        return Character.toUpperCase(pretty.charAt(0)) + pretty.substring(1);
    }
}
