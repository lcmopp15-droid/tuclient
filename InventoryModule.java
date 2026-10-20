package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.util.Draw;
import dev.tuclient.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

/** Muestra el contenido de tu inventario (sin la barra de acceso rápido). */
public class InventoryModule extends HudModule {

    public InventoryModule() {
        super("inventory", "Inventario", "Muestra el contenido de tu inventario en pantalla.", false);
        setDefaultPosition(6, 180);
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public int getWidth() {
        return 9 * 18 + 6;
    }

    @Override
    public int getHeight() {
        return 3 * 18 + 6;
    }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int x = getX();
        int y = getY();
        Draw.roundRect(ctx, x, y, getWidth(), getHeight(), Theme.radius(4), Theme.hudBackground());

        ClientPlayerEntity p = mc.player;
        if (p == null) {
            return;
        }
        for (int i = 0; i < 27; i++) {
            ItemStack stack = p.getInventory().getStack(9 + i);
            if (stack.isEmpty()) {
                continue;
            }
            int ix = x + 4 + (i % 9) * 18;
            int iy = y + 4 + (i / 9) * 18;
            ctx.drawItem(stack, ix, iy);
            ctx.drawItemInSlot(mc.textRenderer, stack, ix, iy);
        }
    }
}
