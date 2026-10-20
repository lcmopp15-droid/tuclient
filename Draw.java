package dev.tuclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Utilidades de dibujo: rectángulos redondeados, texto y colores. */
public final class Draw {

    private Draw() {
    }

    public static void roundRect(DrawContext c, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            c.fill(x, y, x + w, y + h, color);
            return;
        }
        // Parte central (sin esquinas)
        c.fill(x, y + r, x + w, y + h - r, color);
        // Filas de arriba y abajo con las esquinas recortadas
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * (double) r - dy * dy));
            c.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    public static void outline(DrawContext c, int x, int y, int w, int h, int color) {
        c.fill(x, y, x + w, y + 1, color);
        c.fill(x, y + h - 1, x + w, y + h, color);
        c.fill(x, y, x + 1, y + h, color);
        c.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void text(DrawContext c, String s, int x, int y, int color) {
        c.drawText(MinecraftClient.getInstance().textRenderer, s, x, y, color, Theme.shadow());
    }

    public static void centeredText(DrawContext c, String s, int centerX, int y, int color) {
        text(c, s, centerX - width(s) / 2, y, color);
    }

    public static int width(String s) {
        return MinecraftClient.getInstance().textRenderer.getWidth(s);
    }

    public static int fontHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0xFFFFFF);
    }

    public static int lerp(int c1, int c2, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int a = mix((c1 >>> 24) & 0xFF, (c2 >>> 24) & 0xFF, t);
        int r = mix((c1 >> 16) & 0xFF, (c2 >> 16) & 0xFF, t);
        int g = mix((c1 >> 8) & 0xFF, (c2 >> 8) & 0xFF, t);
        int b = mix(c1 & 0xFF, c2 & 0xFF, t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int mix(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }
}
