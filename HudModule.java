package dev.tuclient.module;

import com.google.gson.JsonObject;
import dev.tuclient.util.Draw;
import dev.tuclient.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Módulo que se dibuja en pantalla y se puede mover con el editor de HUD.
 * Por defecto dibuja una "pastilla" con una etiqueta (gris) y un valor (blanco).
 */
public abstract class HudModule extends ClientModule {

    private static final int PAD = 6;
    private static final int BAR_LEAD = 10;
    private static final int GAP = 4;

    private int x;
    private int y;
    private int defaultX;
    private int defaultY;

    protected HudModule(String id, String name, String description, boolean enabledByDefault) {
        super(id, name, description, enabledByDefault);
    }

    /** Texto pequeño y gris, por ejemplo "FPS". */
    public String getLabel() {
        return "";
    }

    /** Texto principal, por ejemplo "144". */
    public abstract String getValue();

    protected void setDefaultPosition(int x, int y) {
        this.defaultX = x;
        this.defaultY = y;
        this.x = x;
        this.y = y;
    }

    public void resetPosition() {
        this.x = defaultX;
        this.y = defaultY;
    }

    /** Posición ya ajustada para que nunca quede fuera de la pantalla. */
    public int getX() {
        int max = MinecraftClient.getInstance().getWindow().getScaledWidth() - getWidth();
        return Math.max(0, Math.min(x, max));
    }

    public int getY() {
        int max = MinecraftClient.getInstance().getWindow().getScaledHeight() - getHeight();
        return Math.max(0, Math.min(y, max));
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getWidth() {
        int lead = Theme.accentBar() ? BAR_LEAD : PAD;
        int w = lead + Draw.width(getValue()) + PAD;
        String label = getLabel();
        if (!label.isEmpty()) {
            w += Draw.width(label) + GAP;
        }
        return w;
    }

    public int getHeight() {
        return Draw.fontHeight() + 8;
    }

    public boolean contains(double mx, double my) {
        int px = getX();
        int py = getY();
        return mx >= px && mx < px + getWidth() && my >= py && my < py + getHeight();
    }

    public void render(DrawContext ctx) {
        int px = getX();
        int py = getY();
        int w = getWidth();
        int h = getHeight();

        Draw.roundRect(ctx, px, py, w, h, Theme.radius(4), Theme.hudBackground());

        int tx = px + PAD;
        if (Theme.accentBar()) {
            Draw.roundRect(ctx, px + 4, py + 4, 2, h - 8, 1, Theme.accent());
            tx = px + BAR_LEAD;
        }

        String label = getLabel();
        if (!label.isEmpty()) {
            Draw.text(ctx, label, tx, py + 4, Theme.MUTED);
            tx += Draw.width(label) + GAP;
        }
        Draw.text(ctx, getValue(), tx, py + 4, Theme.TEXT);
    }

    @Override
    public void save(JsonObject json) {
        super.save(json);
        json.addProperty("x", x);
        json.addProperty("y", y);
    }

    @Override
    public void load(JsonObject json) {
        super.load(json);
        if (json.has("x")) x = json.get("x").getAsInt();
        if (json.has("y")) y = json.get("y").getAsInt();
    }
}
