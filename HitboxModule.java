package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

/** Muestra las hitboxes de las entidades (igual que F3 + B). */
public class HitboxModule extends ClientModule {

    private boolean applied;

    public HitboxModule() {
        super("hitbox", "Hitboxes", "Muestra las cajas de colisión de jugadores y mobs.", Category.VISUAL, false);
    }

    @Override
    public void onTick(MinecraftClient client) {
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        if (isEnabled()) {
            dispatcher.setRenderHitboxes(true);
            applied = true;
        } else if (applied) {
            dispatcher.setRenderHitboxes(false);
            applied = false;
        }
    }
}
