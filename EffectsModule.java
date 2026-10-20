package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.util.Draw;
import dev.tuclient.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Lista tus efectos de poción activos con su tiempo restante. */
public class EffectsModule extends HudModule {

    private static final int ROW = 11;

    public EffectsModule() {
        super("effects", "Efectos", "Tus efectos de poción activos y su duración.", false);
        setDefaultPosition(170, 170);
    }

    private List<StatusEffectInstance> effects() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) {
            return List.of();
        }
        return new ArrayList<>(p.getStatusEffects());
    }

    private static String line(StatusEffectInstance e) {
        String name = e.getEffectType().getName().getString();
        if (e.getAmplifier() > 0) {
            name += " " + (e.getAmplifier() + 1);
        }
        return name + "  " + time(e);
    }

    private static String time(StatusEffectInstance e) {
        if (e.getDuration() < 0) {
            return "inf";
        }
        int s = e.getDuration() / 20;
        return String.format(Locale.ROOT, "%d:%02d", s / 60, s % 60);
    }

    @Override
    public String getLabel() {
        return "Efectos";
    }

    @Override
    public String getValue() {
        return effects().isEmpty() ? "-" : "";
    }

    @Override
    public int getWidth() {
        List<StatusEffectInstance> list = effects();
        if (list.isEmpty()) {
            return super.getWidth();
        }
        int w = 0;
        for (StatusEffectInstance e : list) {
            w = Math.max(w, Draw.width(line(e)));
        }
        return w + 24;
    }

    @Override
    public int getHeight() {
        List<StatusEffectInstance> list = effects();
        if (list.isEmpty()) {
            return super.getHeight();
        }
        return list.size() * ROW + 8;
    }

    @Override
    public void render(DrawContext ctx) {
        List<StatusEffectInstance> list = effects();
        if (list.isEmpty()) {
            super.render(ctx);
            return;
        }
        int x = getX();
        int y = getY();
        Draw.roundRect(ctx, x, y, getWidth(), getHeight(), Theme.radius(4), Theme.hudBackground());
        int ry = y + 4;
        for (StatusEffectInstance e : list) {
            int color = 0xFF000000 | e.getEffectType().getColor();
            Draw.roundRect(ctx, x + 6, ry + 2, 6, 6, 3, color);
            Draw.text(ctx, line(e), x + 17, ry, Theme.TEXT);
            ry += ROW;
        }
    }
}
