package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/** Corre automáticamente mientras avanzas. */
public class AutoSprintModule extends ClientModule {

    public AutoSprintModule() {
        super("autosprint", "Sprint automático", "Corres solo mientras avanzas, sin tocar la tecla.",
                Category.GAME, false);
    }

    @Override
    public void onTick(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (!isEnabled() || p == null || client.currentScreen != null) {
            return;
        }
        if (client.options.forwardKey.isPressed()
                && !p.isSneaking()
                && !p.horizontalCollision
                && !p.isUsingItem()
                && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}
