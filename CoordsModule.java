package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class CoordsModule extends HudModule {

    private final BooleanSetting showDirection = register(new BooleanSetting("direction", "Mostrar dirección", true));

    public CoordsModule() {
        super("coords", "Coordenadas", "Tu posición X, Y, Z y hacia dónde miras.", true);
        setDefaultPosition(6, 46);
    }

    @Override
    public String getLabel() {
        return "XYZ";
    }

    @Override
    public String getValue() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) {
            return "-";
        }
        String text = p.getBlockX() + " " + p.getBlockY() + " " + p.getBlockZ();
        if (showDirection.get()) {
            String dir = switch (p.getHorizontalFacing()) {
                case NORTH -> "N";
                case SOUTH -> "S";
                case EAST -> "E";
                default -> "O";
            };
            text += "  " + dir;
        }
        return text;
    }
}
