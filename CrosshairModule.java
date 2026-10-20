package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.setting.ModeSetting;
import dev.tuclient.setting.NumberSetting;
import dev.tuclient.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/** Mira personalizada que reemplaza a la de Minecraft. */
public class CrosshairModule extends ClientModule {

    private final ModeSetting style = register(new ModeSetting("style", "Estilo",
            List.of("Cruz", "Cruz con punto", "Punto", "Cuadrado"), 1));
    private final NumberSetting size = register(new NumberSetting("size", "Largo", 1, 20, 1, 6));
    private final NumberSetting thickness = register(new NumberSetting("thickness", "Grosor", 1, 5, 1, 1));
    private final NumberSetting gap = register(new NumberSetting("gap", "Separación", 0, 12, 1, 3));
    private final ModeSetting color = register(new ModeSetting("color", "Color",
            List.of("Acento", "Blanco", "Rojo", "Verde", "Amarillo", "Cian"), 1));
    private final BooleanSetting outline = register(new BooleanSetting("outline", "Contorno negro", true));

    public CrosshairModule() {
        super("crosshair", "Mira personalizada", "Elige el estilo, tamaño y color de tu mira.",
                Category.VISUAL, false);
    }

    private int colorValue() {
        return switch (color.getIndex()) {
            case 0 -> Theme.accent();
            case 2 -> 0xFFEF4444;
            case 3 -> 0xFF22C55E;
            case 4 -> 0xFFFACC15;
            case 5 -> 0xFF22D3EE;
            default -> 0xFFFFFFFF;
        };
    }

    @Override
    public void renderOverlay(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null || !mc.options.getPerspective().isFirstPerson()) {
            return;
        }

        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;
        int t = (int) thickness.get();
        int s = (int) size.get();
        int g = (int) gap.get();
        int half = t / 2;

        List<int[]> rects = new ArrayList<>();
        String mode = style.get();

        if (mode.equals("Cruz") || mode.equals("Cruz con punto")) {
            rects.add(new int[]{cx - half, cy - g - s, cx - half + t, cy - g});
            rects.add(new int[]{cx - half, cy + g, cx - half + t, cy + g + s});
            rects.add(new int[]{cx - g - s, cy - half, cx - g, cy - half + t});
            rects.add(new int[]{cx + g, cy - half, cx + g + s, cy - half + t});
        }
        if (mode.equals("Cruz con punto") || mode.equals("Punto")) {
            int d = Math.max(t, 2);
            rects.add(new int[]{cx - d / 2, cy - d / 2, cx - d / 2 + d, cy - d / 2 + d});
        }
        if (mode.equals("Cuadrado")) {
            int r = s + g;
            rects.add(new int[]{cx - r, cy - r, cx + r, cy - r + t});
            rects.add(new int[]{cx - r, cy + r - t, cx + r, cy + r});
            rects.add(new int[]{cx - r, cy - r, cx - r + t, cy + r});
            rects.add(new int[]{cx + r - t, cy - r, cx + r, cy + r});
        }

        if (outline.get()) {
            for (int[] r : rects) {
                ctx.fill(r[0] - 1, r[1] - 1, r[2] + 1, r[3] + 1, 0xFF000000);
            }
        }
        int c = colorValue();
        for (int[] r : rects) {
            ctx.fill(r[0], r[1], r[2], r[3], c);
        }
    }
}
