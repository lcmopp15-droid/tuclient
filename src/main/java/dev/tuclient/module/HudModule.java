package dev.tuclient.module;

import com.google.gson.JsonObject;
import dev.tuclient.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Módulo que dibuja un texto en pantalla y se puede mover con el editor de HUD. */
public abstract class HudModule extends ClientModule {

    protected static final int PAD = 3;

    protected final BooleanSetting background = register(new BooleanSetting("background", "Fondo", true));

    private int x;
    private int y;

    protected HudModule(String id, String name, boolean enabledByDefault) {
        super(id, name, enabledByDefault);
    }

    /** Texto que se muestra en el HUD. */
    public abstract String getText();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getWidth() {
        return MinecraftClient.getInstance().textRenderer.getWidth(getText()) + PAD * 2;
    }

    public int getHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight + PAD * 2;
    }

    public boolean contains(double mx, double my) {
        return mx >= x && mx < x + getWidth() && my >= y && my < y + getHeight();
    }

    public void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (background.get()) {
            ctx.fill(x, y, x + getWidth(), y + getHeight(), 0x90000000);
        }
        ctx.drawTextWithShadow(mc.textRenderer, getText(), x + PAD, y + PAD, 0xFFFFFFFF);
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
