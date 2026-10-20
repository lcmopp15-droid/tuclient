package dev.tuclient.gui;

import dev.tuclient.config.ConfigManager;
import dev.tuclient.module.ClientModule;
import dev.tuclient.module.HudModule;
import dev.tuclient.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Editor de HUD: arrastra los módulos con el ratón para moverlos. */
public class HudEditorScreen extends Screen {

    private static final int ACCENT = 0xFF4F8EF7;
    private static final int IDLE = 0x88FFFFFF;

    private final Screen parent;
    private HudModule dragging;
    private int offsetX;
    private int offsetY;

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
        ctx.fill(0, 0, this.width, this.height, 0x22000000);
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Arrastra los módulos para moverlos - ESC para volver"),
                this.width / 2, 14, 0xFFFFFFFF);

        for (ClientModule module : ModuleManager.all()) {
            if (module.isEnabled() && module instanceof HudModule hud) {
                boolean active = hud == dragging || (dragging == null && hud.contains(mouseX, mouseY));
                outline(ctx, hud.getX() - 1, hud.getY() - 1, hud.getWidth() + 2, hud.getHeight() + 2,
                        active ? ACCENT : IDLE);
            }
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    private static void outline(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
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
            int nx = (int) mouseX - offsetX;
            int ny = (int) mouseY - offsetY;
            nx = Math.max(0, Math.min(nx, this.width - dragging.getWidth()));
            ny = Math.max(0, Math.min(ny, this.height - dragging.getHeight()));
            dragging.setPosition(nx, ny);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        return super.mouseReleased(mouseX, mouseY, button);
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
