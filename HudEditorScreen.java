package dev.tuclient.gui;

import dev.tuclient.config.ConfigManager;
import dev.tuclient.module.ClientModule;
import dev.tuclient.module.HudModule;
import dev.tuclient.module.ModuleManager;
import dev.tuclient.util.Draw;
import dev.tuclient.util.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Editor de HUD: arrastra los módulos; se ajustan al centro de la pantalla. */
public class HudEditorScreen extends Screen {

    private static final int SNAP = 5;

    private final Screen parent;
    private HudModule dragging;
    private int offsetX;
    private int offsetY;
    private boolean snapV;
    private boolean snapH;

    public HudEditorScreen(Screen parent) {
        super(Text.literal("Editor de HUD"));
        this.parent = parent;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int accent = Theme.accent();
        ctx.fill(0, 0, this.width, this.height, 0x40000000);

        // Guías del centro (se resaltan al ajustarse)
        ctx.fill(this.width / 2, 0, this.width / 2 + 1, this.height,
                snapV ? Draw.withAlpha(accent, 0xCC) : 0x22FFFFFF);
        ctx.fill(0, this.height / 2, this.width, this.height / 2 + 1,
                snapH ? Draw.withAlpha(accent, 0xCC) : 0x22FFFFFF);

        Draw.centeredText(ctx, "Editor de HUD", this.width / 2, 10, Theme.TEXT);
        Draw.centeredText(ctx, "Arrastra los módulos  -  R: restablecer  -  ESC: volver",
                this.width / 2, 22, Theme.MUTED);

        for (ClientModule module : ModuleManager.all()) {
            if (module.isEnabled() && module instanceof HudModule hud) {
                boolean active = hud == dragging || (dragging == null && hud.contains(mouseX, mouseY));
                int px = hud.getX();
                int py = hud.getY();
                int w = hud.getWidth();
                int h = hud.getHeight();

                Draw.outline(ctx, px - 2, py - 2, w + 4, h + 4, active ? accent : 0x88FFFFFF);
                if (active) {
                    int ty = py < 14 ? py + h + 5 : py - 13;
                    Draw.text(ctx, hud.getName() + "  " + px + ", " + py, px, ty, Theme.TEXT);
                }
            }
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            for (ClientModule module : ModuleManager.all()) {
                if (module.isEnabled() && module instanceof HudModule hud && hud.contains(mouseX, mouseY)) {
                    dragging = hud;
                    offsetX = (int) mouseX - hud.getX();
                    offsetY = (int) mouseY - hud.getY();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            int w = dragging.getWidth();
            int h = dragging.getHeight();
            int nx = Math.max(0, Math.min((int) mouseX - offsetX, this.width - w));
            int ny = Math.max(0, Math.min((int) mouseY - offsetY, this.height - h));

            snapV = Math.abs(nx + w / 2 - this.width / 2) <= SNAP;
            if (snapV) {
                nx = this.width / 2 - w / 2;
            }
            snapH = Math.abs(ny + h / 2 - this.height / 2) <= SNAP;
            if (snapH) {
                ny = this.height / 2 - h / 2;
            }

            dragging.setPosition(nx, ny);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        snapV = false;
        snapH = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R) {
            ModuleManager.resetHudPositions();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public void removed() {
        ConfigManager.save();
        super.removed();
    }
}
