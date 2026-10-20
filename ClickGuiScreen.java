package dev.tuclient.gui;

import dev.tuclient.config.ConfigManager;
import dev.tuclient.module.ClientModule;
import dev.tuclient.module.ModuleManager;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.setting.ModeSetting;
import dev.tuclient.setting.NumberSetting;
import dev.tuclient.setting.Setting;
import dev.tuclient.util.Draw;
import dev.tuclient.util.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Menú principal: lista de módulos a la izquierda y sus ajustes a la derecha.
 * Clic en el interruptor = activar/desactivar. Clic en el nombre = ver sus ajustes.
 */
public class ClickGuiScreen extends Screen {

    private static final int W = 340;
    private static final int H = 236;
    private static final int HEADER = 30;
    private static final int SIDE = 120;
    private static final int ROW = 22;
    private static final int PAD = 10;
    private static final int SETTINGS_Y = 46;
    private static final int HUD_BTN_W = 78;
    private static final int SEPARATOR = 0x1AFFFFFF;

    private final Map<Object, Float> anims = new HashMap<>();
    private ClientModule selected;
    private NumberSetting draggingNumber;
    private float scroll;

    private int winX, winY, winW, winH;
    private int listX, listY, listW, listH;
    private int paneX, paneY, paneW;

    public ClickGuiScreen() {
        super(Text.literal("TuClient"));
        List<ClientModule> mods = ModuleManager.all();
        if (!mods.isEmpty()) {
            selected = mods.get(0);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void layout() {
        winW = Math.min(W, this.width - 10);
        winH = Math.min(H, this.height - 10);
        winX = (this.width - winW) / 2;
        winY = (this.height - winH) / 2;
        listX = winX + 6;
        listY = winY + HEADER + 6;
        listW = SIDE - 12;
        listH = winH - HEADER - 12;
        paneX = winX + SIDE + PAD;
        paneY = winY + HEADER + PAD;
        paneW = winW - SIDE - PAD * 2;
    }

    private int maxScroll() {
        return Math.max(0, ModuleManager.all().size() * ROW - listH);
    }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static int settingHeight(Setting s) {
        return s instanceof NumberSetting ? 32 : 24;
    }

    // ---------------------------------------------------------------- animaciones

    private void tickAnims() {
        for (ClientModule m : ModuleManager.all()) {
            step(m, m.isEnabled());
        }
        if (selected != null) {
            for (Setting s : selected.getSettings()) {
                if (s instanceof BooleanSetting b) {
                    step(s, b.get());
                }
            }
        }
    }

    private void step(Object key, boolean on) {
        float target = on ? 1f : 0f;
        float cur = anims.getOrDefault(key, target);
        cur += (target - cur) * 0.3f;
        if (Math.abs(target - cur) < 0.01f) {
            cur = target;
        }
        anims.put(key, cur);
    }

    private float anim(Object key) {
        return anims.getOrDefault(key, 0f);
    }

    // ---------------------------------------------------------------- dibujo

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        layout();
        tickAnims();
        scroll = Math.max(0, Math.min(scroll, maxScroll()));
        int accent = Theme.accent();

        ctx.fill(0, 0, this.width, this.height, 0x80000000);
        Draw.roundRect(ctx, winX - 2, winY - 2, winW + 4, winH + 4, 8, 0x30000000);
        Draw.roundRect(ctx, winX, winY, winW, winH, 6, Theme.WINDOW);

        // Cabecera
        Draw.text(ctx, "TuClient", winX + 12, winY + 11, accent);
        Draw.text(ctx, "v1.1", winX + 12 + Draw.width("TuClient") + 6, winY + 11, Theme.DIM);

        int bx = hudButtonX();
        int by = winY + 6;
        boolean overButton = inRect(mouseX, mouseY, bx, by, HUD_BTN_W, 18);
        Draw.roundRect(ctx, bx, by, HUD_BTN_W, 18, 5,
                overButton ? Draw.lerp(accent, 0xFFFFFFFF, 0.2f) : accent);
        Draw.centeredText(ctx, "Editar HUD", bx + HUD_BTN_W / 2, by + 5, 0xFFFFFFFF);

        ctx.fill(winX, winY + HEADER, winX + winW, winY + HEADER + 1, SEPARATOR);
        ctx.fill(winX + SIDE, winY + HEADER + 1, winX + SIDE + 1, winY + winH - 6, SEPARATOR);

        renderList(ctx, mouseX, mouseY, accent);
        renderPane(ctx, mouseX, mouseY, accent);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private int hudButtonX() {
        return winX + winW - 12 - HUD_BTN_W;
    }

    private void renderList(DrawContext ctx, int mouseX, int mouseY, int accent) {
        List<ClientModule> mods = ModuleManager.all();
        ctx.enableScissor(listX, listY, listX + listW, listY + listH);

        for (int i = 0; i < mods.size(); i++) {
            int ry = listY + i * ROW - (int) scroll;
            if (ry + ROW < listY || ry > listY + listH) {
                continue;
            }
            ClientModule m = mods.get(i);
            boolean isSelected = m == selected;
            boolean hover = inRect(mouseX, mouseY, listX, listY, listW, listH)
                    && mouseY >= ry && mouseY < ry + ROW;

            if (isSelected) {
                Draw.roundRect(ctx, listX, ry, listW, ROW - 2, 4, Draw.withAlpha(accent, 0x38));
            } else if (hover) {
                Draw.roundRect(ctx, listX, ry, listW, ROW - 2, 4, 0x14FFFFFF);
            }

            Draw.text(ctx, m.getName(), listX + 8, ry + 6, isSelected ? Theme.TEXT : Theme.MUTED);
            if (m.isToggleable()) {
                drawSwitch(ctx, listX + listW - 28, ry + 4, 22, 11, anim(m));
            }
        }

        ctx.disableScissor();
    }

    private void renderPane(DrawContext ctx, int mouseX, int mouseY, int accent) {
        if (selected == null) {
            return;
        }

        Draw.text(ctx, selected.getName(), paneX, paneY, Theme.TEXT);
        if (selected.isToggleable()) {
            drawSwitch(ctx, paneX + paneW - 28, paneY - 3, 28, 14, anim(selected));
        }

        List<OrderedText> lines = this.textRenderer.wrapLines(Text.literal(selected.getDescription()), paneW);
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            ctx.drawText(this.textRenderer, lines.get(i), paneX, paneY + 14 + i * 10, Theme.MUTED, false);
        }
        ctx.fill(paneX, paneY + 38, paneX + paneW, paneY + 39, SEPARATOR);

        int y = paneY + SETTINGS_Y;
        for (Setting s : selected.getSettings()) {
            renderSetting(ctx, s, y, accent);
            y += settingHeight(s);
        }
    }

    private void renderSetting(DrawContext ctx, Setting s, int y, int accent) {
        if (s instanceof BooleanSetting) {
            Draw.text(ctx, s.getLabel(), paneX, y + 5, Theme.TEXT);
            drawSwitch(ctx, paneX + paneW - 24, y + 3, 24, 12, anim(s));
        } else if (s instanceof ModeSetting mode) {
            Draw.text(ctx, s.getLabel(), paneX, y + 5, Theme.TEXT);
            String value = mode.get();
            int chip = s.getKey().equals("accent") ? 12 : 0;
            int bw = Math.max(54, Draw.width(value) + 16 + chip);
            int bx = paneX + paneW - bw;
            Draw.roundRect(ctx, bx, y + 1, bw, 17, 4, Theme.CARD);
            if (chip > 0) {
                Draw.roundRect(ctx, bx + 6, y + 6, 7, 7, 3, Theme.accentAt(mode.getIndex()));
            }
            Draw.centeredText(ctx, value, bx + bw / 2 + chip / 2, y + 5, Theme.TEXT);
        } else if (s instanceof NumberSetting num) {
            Draw.text(ctx, s.getLabel(), paneX, y + 3, Theme.TEXT);
            String value = num.format();
            Draw.text(ctx, value, paneX + paneW - Draw.width(value), y + 3, Theme.MUTED);

            int fill = (int) Math.round(paneW * num.fraction());
            Draw.roundRect(ctx, paneX, y + 20, paneW, 4, 2, Theme.OFF);
            Draw.roundRect(ctx, paneX, y + 20, fill, 4, 2, accent);
            int knobX = Math.max(paneX, Math.min(paneX + fill - 4, paneX + paneW - 8));
            Draw.roundRect(ctx, knobX, y + 18, 8, 8, 4, 0xFFFFFFFF);
        }
    }

    private void drawSwitch(DrawContext ctx, int x, int y, int w, int h, float t) {
        Draw.roundRect(ctx, x, y, w, h, h / 2, Draw.lerp(Theme.OFF, Theme.accent(), t));
        int knob = h - 4;
        int kx = x + 2 + Math.round((w - knob - 4) * t);
        Draw.roundRect(ctx, kx, y + 2, knob, knob, knob / 2, 0xFFFFFFFF);
    }

    // ---------------------------------------------------------------- ratón y teclado

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        layout();

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && inRect(mouseX, mouseY, hudButtonX(), winY + 6, HUD_BTN_W, 18)) {
            this.client.setScreen(new HudEditorScreen(this));
            return true;
        }

        // Lista de módulos
        if (inRect(mouseX, mouseY, listX, listY, listW, listH)) {
            List<ClientModule> mods = ModuleManager.all();
            int idx = (int) ((mouseY - listY + scroll) / ROW);
            if (idx >= 0 && idx < mods.size()) {
                ClientModule m = mods.get(idx);
                boolean onSwitch = mouseX >= listX + listW - 32;
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && onSwitch && m.isToggleable()) {
                    m.toggle();
                } else {
                    selected = m;
                }
            }
            return true;
        }

        // Panel derecho
        if (selected != null) {
            if (selected.isToggleable() && inRect(mouseX, mouseY, paneX + paneW - 28, paneY - 3, 28, 14)) {
                selected.toggle();
                return true;
            }
            int y = paneY + SETTINGS_Y;
            for (Setting s : selected.getSettings()) {
                int h = settingHeight(s);
                if (inRect(mouseX, mouseY, paneX, y, paneW, h)) {
                    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        if (s instanceof BooleanSetting b) {
                            b.toggle();
                        } else if (s instanceof ModeSetting ms) {
                            ms.next();
                        } else if (s instanceof NumberSetting n) {
                            draggingNumber = n;
                            updateSlider(n, mouseX);
                        }
                    }
                    return true;
                }
                y += h;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingNumber != null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            updateSlider(draggingNumber, mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingNumber = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        layout();
        if (inRect(mouseX, mouseY, winX, winY + HEADER, SIDE, winH - HEADER)) {
            scroll = Math.max(0, Math.min(scroll - (float) amount * ROW, maxScroll()));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    private void updateSlider(NumberSetting setting, double mouseX) {
        setting.setFraction((mouseX - paneX) / (double) paneW);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        ConfigManager.save();
        super.removed();
    }
}
