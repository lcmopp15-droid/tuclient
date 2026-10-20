package dev.tuclient.module;

import dev.tuclient.TuClient;
import dev.tuclient.module.impl.AppearanceModule;
import dev.tuclient.module.impl.ArmorModule;
import dev.tuclient.module.impl.AutoSprintModule;
import dev.tuclient.module.impl.BiomeModule;
import dev.tuclient.module.impl.ComboModule;
import dev.tuclient.module.impl.CoordsModule;
import dev.tuclient.module.impl.CpsModule;
import dev.tuclient.module.impl.CrosshairModule;
import dev.tuclient.module.impl.DayModule;
import dev.tuclient.module.impl.EffectsModule;
import dev.tuclient.module.impl.FovModule;
import dev.tuclient.module.impl.FpsModule;
import dev.tuclient.module.impl.HitboxModule;
import dev.tuclient.module.impl.InventoryModule;
import dev.tuclient.module.impl.KeystrokesModule;
import dev.tuclient.module.impl.LowFireModule;
import dev.tuclient.module.impl.MemoryModule;
import dev.tuclient.module.impl.NoHurtCamModule;
import dev.tuclient.module.impl.PingModule;
import dev.tuclient.module.impl.ReachModule;
import dev.tuclient.module.impl.ServerModule;
import dev.tuclient.module.impl.SpeedModule;
import dev.tuclient.module.impl.TimeChangerModule;
import dev.tuclient.module.impl.TimeModule;
import dev.tuclient.module.impl.ViewmodelModule;
import dev.tuclient.module.impl.WeatherModule;
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
        // HUD
        MODULES.add(new FpsModule());
        MODULES.add(new CpsModule());
        MODULES.add(new CoordsModule());
        MODULES.add(new ReachModule());
        MODULES.add(new KeystrokesModule());
        MODULES.add(new ArmorModule());
        MODULES.add(new PingModule());
        MODULES.add(new TimeModule());
        MODULES.add(new SpeedModule());
        MODULES.add(new MemoryModule());
        MODULES.add(new BiomeModule());
        MODULES.add(new DayModule());
        MODULES.add(new ServerModule());
        MODULES.add(new ComboModule());
        MODULES.add(new EffectsModule());
        MODULES.add(new InventoryModule());
        // Visual
        MODULES.add(new HitboxModule());
        MODULES.add(new ViewmodelModule());
        MODULES.add(new CrosshairModule());
        MODULES.add(new LowFireModule());
        MODULES.add(new NoHurtCamModule());
        MODULES.add(new ZoomModule());
        MODULES.add(new FovModule());
        // Juego
        MODULES.add(new AutoSprintModule());
        MODULES.add(new TimeChangerModule());
        MODULES.add(new WeatherModule());
        // Ajustes
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

    public static void tick(MinecraftClient client) {
        for (ClientModule m : MODULES) {
            try {
                m.onTick(client);
            } catch (Exception e) {
                TuClient.LOGGER.error("Error en el módulo " + m.getId(), e);
            }
        }
    }

    public static void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) {
            return;
        }
        boolean debug = mc.options.debugEnabled;
        for (ClientModule m : MODULES) {
            if (!m.isEnabled()) {
                continue;
            }
            if (m instanceof HudModule hud) {
                if (!debug) {
                    hud.render(ctx);
                }
            } else {
                m.renderOverlay(ctx);
            }
        }
    }
}
