package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/** Cuenta tus golpes seguidos; se reinicia al recibir daño o tras 3 segundos sin golpear. */
public class ComboModule extends HudModule {

    private static int combo;
    private static long lastHit;

    private int lastHurt;

    public ComboModule() {
        super("combo", "Combo", "Tus golpes seguidos sin recibir daño.", false);
        setDefaultPosition(170, 146);
    }

    public static void registerHit() {
        combo++;
        lastHit = System.currentTimeMillis();
    }

    @Override
    public void onTick(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (p == null) {
            return;
        }
        if (p.hurtTime > lastHurt) {
            combo = 0;
        }
        lastHurt = p.hurtTime;
        if (combo > 0 && System.currentTimeMillis() - lastHit > 3000L) {
            combo = 0;
        }
    }

    @Override
    public String getLabel() {
        return "Combo";
    }

    @Override
    public String getValue() {
        return String.valueOf(combo);
    }
}
