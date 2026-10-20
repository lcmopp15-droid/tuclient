package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;

public class DayModule extends HudModule {

    public DayModule() {
        super("day", "Día", "Cuántos días llevas en el mundo.", false);
        setDefaultPosition(170, 106);
    }

    @Override
    public String getLabel() {
        return "Día";
    }

    @Override
    public String getValue() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            return "-";
        }
        return String.valueOf(mc.world.getTime() / 24000L + 1L);
    }
}
