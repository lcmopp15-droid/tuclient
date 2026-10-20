package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import dev.tuclient.setting.ModeSetting;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/** Fija la hora del mundo solo en tu pantalla (no cambia nada en el servidor). */
public class TimeChangerModule extends ClientModule {

    private final ModeSetting time = register(new ModeSetting("time", "Momento del día",
            List.of("Amanecer", "Día", "Atardecer", "Noche"), 1));

    public TimeChangerModule() {
        super("timechanger", "Cambiador de hora", "Ves siempre el momento del día que elijas.",
                Category.GAME, false);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.world == null) {
            return;
        }
        long ticks = switch (time.getIndex()) {
            case 0 -> 23000L;
            case 2 -> 12500L;
            case 3 -> 18000L;
            default -> 6000L;
        };
        client.world.setTimeOfDay(ticks);
    }
}
