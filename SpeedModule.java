package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Locale;

public class SpeedModule extends HudModule {

    public SpeedModule() {
        super("speed", "Velocidad", "Tu velocidad horizontal en bloques por segundo.", false);
        setDefaultPosition(170, 46);
    }

    @Override
    public String getLabel() {
        return "Vel";
    }

    @Override
    public String getValue() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) {
            return "-";
        }
        double dx = p.getX() - p.prevX;
        double dz = p.getZ() - p.prevZ;
        return String.format(Locale.ROOT, "%.1f b/s", Math.sqrt(dx * dx + dz * dz) * 20.0);
    }
}
