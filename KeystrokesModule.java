package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.util.ClickTracker;
import dev.tuclient.util.Draw;
import dev.tuclient.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

/** Muestra las teclas WASD, los clics del ratón (con CPS) y el espacio. */
public class KeystrokesModule extends HudModule {

    private static final int KEY = 22;
    private static final int GAP = 2;
    private static final int SPACE_H = 14;

    private final BooleanSetting showMouse = register(new BooleanSetting("showMouse", "Mostrar clics (CPS)", true));
    private final BooleanSetting showSpace = register(new BooleanSetting("showSpace", "Mostrar espacio", true));

    private final float[] progress = new float[7];
    private long lastFrame = System.nanoTime();
    private float dt;

    public KeystrokesModule() {
        super("keystrokes", "Keystrokes", "Muestra las teclas WASD, los clics y el espacio.", true);
        setDefaultPosition(6, 90);
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public int getWidth() {
        return KEY * 3 + GAP * 2;
    }

    @Override
    public int getHeight() {
        int h = KEY * 2 + GAP;
        if (showMouse.get()) {
            h += GAP + KEY;
        }
        if (showSpace.get()) {
            h += GAP + SPACE_H;
        }
        return h;
    }

    @Override
    public void render(DrawContext ctx) {
        GameOptions o = MinecraftClient.getInstance().options;

        long now = System.nanoTime();
        dt = Math.min((now - lastFrame) / 1_000_000_000f, 0.1f);
        lastFrame = now;

        int x = getX();
        int y = getY();
        int step = KEY + GAP;

        drawKey(ctx, 0, o.forwardKey.isPressed(), "W", x + step, y);
        drawKey(ctx, 1, o.leftKey.isPressed(), "A", x, y + step);
        drawKey(ctx, 2, o.backKey.isPressed(), "S", x + step, y + step);
        drawKey(ctx, 3, o.rightKey.isPressed(), "D", x + step * 2, y + step);

        int cy = y + step * 2;
        if (showMouse.get()) {
            int half = (getWidth() - GAP) / 2;
            drawMouse(ctx, 4, o.attackKey.isPressed(), "LMB", ClickTracker.left(), x, cy, half);
            drawMouse(ctx, 5, o.useKey.isPressed(), "RMB", ClickTracker.right(), x + half + GAP, cy, getWidth() - half - GAP);
            cy += step;
        }

        if (showSpace.get()) {
            float p = animate(6, o.jumpKey.isPressed());
            Draw.roundRect(ctx, x, cy, getWidth(), SPACE_H, Theme.radius(3),
                    Draw.lerp(Theme.hudBackground(), Theme.accent(), p));
            int mid = x + getWidth() / 2;
            ctx.fill(mid - 8, cy + SPACE_H / 2, mid + 8, cy + SPACE_H / 2 + 1,
                    Draw.lerp(Theme.DIM, 0xFFFFFFFF, p));
        }
    }

    private float animate(int index, boolean pressed) {
        float target = pressed ? 1f : 0f;
        progress[index] += (target - progress[index]) * (1f - (float) Math.exp(-dt * 18f));
        return progress[index];
    }

    private void drawKey(DrawContext ctx, int index, boolean pressed, String label, int x, int y) {
        float p = animate(index, pressed);
        Draw.roundRect(ctx, x, y, KEY, KEY, Theme.radius(3), Draw.lerp(Theme.hudBackground(), Theme.accent(), p));
        Draw.centeredText(ctx, label, x + KEY / 2, y + (KEY - Draw.fontHeight()) / 2 + 1, Theme.TEXT);
    }

    private void drawMouse(DrawContext ctx, int index, boolean pressed, String label, int cps, int x, int y, int w) {
        float p = animate(index, pressed);
        Draw.roundRect(ctx, x, y, w, KEY, Theme.radius(3), Draw.lerp(Theme.hudBackground(), Theme.accent(), p));
        Draw.centeredText(ctx, label, x + w / 2, y + 3, Theme.TEXT);
        Draw.centeredText(ctx, String.valueOf(cps), x + w / 2, y + 12, Theme.MUTED);
    }
}
