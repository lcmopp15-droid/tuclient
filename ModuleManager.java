package dev.tuclient.module;

import dev.tuclient.module.impl.AppearanceModule;
import dev.tuclient.module.impl.ArmorModule;
import dev.tuclient.module.impl.CoordsModule;
import dev.tuclient.module.impl.CpsModule;
import dev.tuclient.module.impl.FovModule;
import dev.tuclient.module.impl.FpsModule;
import dev.tuclient.module.impl.KeystrokesModule;
import dev.tuclient.module.impl.PingModule;
import dev.tuclient.module.impl.ReachModule;
import dev.tuclient.module.impl.TimeModule;
import dev.tuclient.module.impl.ZoomModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {

    private static final List<ClientModule> MODULES = new ArrayList<>();
    private static AppearanceModule appearance;

    private ModuleManager() {
    }

    public static void init() {
        MODULES.clear();
        appearance = new AppearanceModule();

        // Para añadir un módulo nuevo: créalo y agrégalo aquí.
        MODULES.add(new FpsModule());
        MODULES.add(new CpsModule());
        MODULES.add(new CoordsModule());
        MODULES.add(new ReachModule());
        MODULES.add(new PingModule());
        MODULES.add(new TimeModule());
        MODULES.add(new KeystrokesModule());
        MODULES.add(new ArmorModule());
        MODULES.add(new ZoomModule());
        MODULES.add(new FovModule());
        MODULES.add(appearance);
    }

    public static AppearanceModule appearance() {
        return appearance;
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

    public static void resetHudPositions() {
        for (ClientModule m : MODULES) {
            if (m instanceof HudModule hud) {
                hud.resetPosition();
            }
        }
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
