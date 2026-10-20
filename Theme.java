package dev.tuclient.util;

import dev.tuclient.module.ModuleManager;
import dev.tuclient.module.impl.AppearanceModule;

/** Colores y estilo compartidos por todo el cliente. */
public final class Theme {

    public static final int TEXT = 0xFFF2F4F8;
    public static final int MUTED = 0xFF9AA3B2;
    public static final int DIM = 0xFF5B6475;
    public static final int WINDOW = 0xF2121419;
    public static final int CARD = 0xFF232733;
    public static final int OFF = 0xFF3A404C;

    private static final int[] ACCENTS = {
            0xFF4F8EF7, // azul
            0xFF8B5CF6, // violeta
            0xFF22C55E, // verde
            0xFFEF4444, // rojo
            0xFFF59E0B, // naranja
            0xFFEC4899  // rosa
    };

    private Theme() {
    }

    private static AppearanceModule ap() {
        return ModuleManager.appearance();
    }

    public static int accentAt(int index) {
        return ACCENTS[Math.floorMod(index, ACCENTS.length)];
    }

    public static int accent() {
        AppearanceModule a = ap();
        return a == null ? ACCENTS[0] : accentAt(a.accentIndex());
    }

    public static int hudBackground() {
        AppearanceModule a = ap();
        int alpha = a == null ? 140 : (int) Math.round(a.opacity() / 100.0 * 255.0);
        return (alpha << 24) | 0x0E1116;
    }

    public static int radius(int r) {
        AppearanceModule a = ap();
        return (a == null || a.rounded()) ? r : 0;
    }

    public static boolean accentBar() {
        AppearanceModule a = ap();
        return a == null || a.accentBar();
    }

    public static boolean shadow() {
        AppearanceModule a = ap();
        return a == null || a.textShadow();
    }
}
