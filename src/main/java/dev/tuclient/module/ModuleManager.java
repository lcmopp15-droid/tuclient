package dev.tuclient.module;

import dev.tuclient.module.impl.CoordsModule;
import dev.tuclient.module.impl.CpsModule;
import dev.tuclient.module.impl.FovModule;
import dev.tuclient.module.impl.FpsModule;
import dev.tuclient.module.impl.ReachModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {

    private static final List<ClientModule> MODULES = new ArrayList<>();

    private ModuleManager() {
    }

    public static void init() {
        MODULES.clear();

        // Para añadir un módulo nuevo: créalo y agrégalo aquí.
        MODULES.add(new FpsModule());
        MODULES.add(new CpsModule());
        MODULES.add(new CoordsModule());
        MODULES.add(new ReachModule());
        MODULES.add(new FovModule());

        // Posición inicial de los módulos del HUD (apilados arriba a la izquierda)
        int y = 6;
        for (ClientModule m : MODULES) {
            if (m instanceof HudModule hud) {
                hud.setPosition(6, y);
                y += 18;
            }
        }
    }

    public static List<ClientModule> all() {
        return Collections.unmodifiableList(MODULES);
    }

    public static <T extends ClientModule> T get(Class<T> type) {
        for (ClientModule m : MODULES) {
            if (type.isInstance(m)) {
                return type.cast(m);
            }
        }
        return null;
    }

    public static void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden || mc.options.debugEnabled) {
            return;
        }
        for (ClientModule m : MODULES) {
            if (m.isEnabled() && m instanceof HudModule hud) {
                hud.render(ctx);
            }
        }
    }
}
