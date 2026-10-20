package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.util.Draw;
import dev.tuclient.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

/** Muestra tu armadura con los usos que le quedan a cada pieza. */
public class ArmorModule extends HudModule {

    private static final int ROW = 18;

    public ArmorModule() {
        super("armor", "Armadura", "Tu armadura y su durabilidad.", true);
        setDefaultPosition(110, 90);
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public int getWidth() {
        return 62;
    }

    @Override
    public int getHeight() {
        return ROW * 4 + 6;
    }

    @Override
    public void render(DrawContext ctx) {
        int x = getX();
        int y = getY();
        Draw.roundRect(ctx, x, y, getWidth(), getHeight(), Theme.radius(4), Theme.hudBackground());

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        for (int i = 0; i < 4; i++) {
            int slot = 3 - i; // casco primero, botas al final
            ItemStack stack = player == null ? ItemStack.EMPTY : player.getInventory().getArmorStack(slot);
            int ry = y + 3 + i * ROW;

            if (stack.isEmpty()) {
                Draw.text(ctx, "-", x + 24, ry + 5, Theme.DIM);
                continue;
            }

            ctx.drawItem(stack, x + 4, ry + 1);
            if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                int left = max - stack.getDamage();
                float ratio = max <= 0 ? 1f : left / (float) max;
                Draw.text(ctx, String.valueOf(left), x + 24, ry + 5,
                        Draw.lerp(0xFFEF4444, 0xFF22C55E, ratio));
            }
        }
    }
}
