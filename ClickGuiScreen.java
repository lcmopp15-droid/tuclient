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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Menú principal de TungTungClient: banner animado, pestañas por categoría,
 * tarjetas de módulos a la izquierda y ajustes del módulo elegido a la derecha.
 */
public class ClickGuiScreen extends Screen {

    private static final int W = 440;
    private static final int H = 290;
    private static final int BANNER = 50;
    private static final int TAB_Y = BANNER + 6;
    private static final int TAB_H = 16;
    private static final int CONTENT_Y = BANNER + 28;
    private static final int PADX = 12;
    private static final int PANE_W = 150;
    private static final int PANE_HEAD = 46;
    private static final int CARD_H = 40;
    private static final int GAP = 8;
    private static final int HUD_BTN_W = 78;

    private static final int SEPARATOR = 0x1AFFFFFF;
    private static final int CARD_HOVER = 0xFF2B3141;
    private static final String[] TAB_NAMES = {"Todos", "HUD", "Visual", "Juego", "Ajustes"};

    private final Map<Object, Float> anims = new HashMap<>();
    private final long openedAt = System.currentTimeMillis();
    private ClientModule selected;
    private NumberSetting draggingNumber;
    private int tab;
    private float gridScroll;
    private float paneScroll;

    private float ease;
    private int winX, winY, winW, winH;
    private int gridX, gridY, gridW, gridH, cardW;
    private int paneX, paneY, paneW, paneH;

    public ClickGuiScreen() {
        super(Text.literal("TungTungClient"));
        List<ClientModule> mods = ModuleManager.all();
        if (!mods.isEmpty()) {
            selected = mods.get(0);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ---------------------------------------------------------------- layout

    private void layout() {
        float t = Math.min(1f, (System.currentTimeMillis() - openedAt) / 240f);
        ease = 1f - (1f - t) * (1f - t) * (1f - t);

        winW = Math.min(W, this.width - 10);
        winH = Math.min(H, this.height - 10);
        winX = (this.width - winW) / 2;
        winY = (this.height - winH) / 2 + Math.round((1f - ease) * 16f);

        paneW = PANE_W;
        gridX = winX + PADX;
        gridY = winY + CONTENT_Y;
        gridW = winW - PADX * 2 - paneW - 14;
        gridH = winY + winH - 10 - gridY;
        cardW = (gridW - GAP) / 2;
        paneX = winX + winW - PADX - paneW;
        paneY = gridY;
        paneH = gridH;
    }

    private List<ClientModule> visible() {
        List<ClientModule> out = new ArrayList<>();
        for (ClientModule m : ModuleManager.all()) {
            if (tab == 0 || m.getCategory().ordinal() == tab - 1) {
                out.add(m);
            }
        }
        return out;
    }

    private int maxGridScroll() {
        int rows = (visible().size() + 1) / 2;
        int total = rows * (CARD_H + GAP) - GAP;
        return Math.max(0, total - gridH);
    }

    private static int settingHeight(Setting s) {
        return s instanceof NumberSetting ? 28 : 22;
    }

    private int maxPaneScroll() {
        int total = 0;
        if (selected != null) {
            for (Setting s : selected.getSettings()) {
                total += settingHeight(s);
            }
        }
        return Math.max(0, total - (paneH - PANE_HEAD));
    }

    private int tabX(int index) {
        int x = winX + PADX;
        for (int k = 0; k < index; k++) {
            x += tabW(k) + 4;
        }
        return x;
    }

    private int tabW(int index) {
        return Draw.width(TAB_NAMES[index]) + 16;
    }

    private int hudButtonX() {
        return winX + winW - 12 - HUD_BTN_W;
    }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
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
        gridScroll = clamp(gridScroll, 0, maxGridScroll());
        paneScroll = clamp(paneScroll, 0, maxPaneScroll());
        int accent = Theme.accent();

        ctx.fill(0, 0, this.width, this.height, ((int) (0x88 * ease)) << 24);
        Draw.roundRect(ctx, winX - 2, winY - 2, winW + 4, winH + 4, 8, 0x30000000);
        Draw.roundRect(ctx, winX, winY, winW, winH, 6, Theme.WINDOW);

        renderBanner(ctx, mouseX, mouseY, accent);
        renderTabs(ctx, mouseX, mouseY, accent);
        renderGrid(ctx, mouseX, mouseY, accent);

        ctx.fill(paneX - 8, gridY, paneX - 7, gridY + gridH, SEPARATOR);
        renderPane(ctx, accent);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderBanner(DrawContext ctx, int mouseX, int mouseY, int accent) {
        ctx.fillGradient(winX + 2, winY + 2, winX + winW - 2, winY + BANNER,
                Draw.withAlpha(accent, 0x70), Draw.withAlpha(accent, 0x00));

        // Brillo que recorre el banner
        int span = winW + 80;
        int sx = winX - 40 + (int) ((System.currentTimeMillis() / 12L) % span);
        int x1 = Math.max(sx, winX + 2);
        int x2 = Math.min(sx + 36, winX + winW - 2);
        if (x1 < x2) {
            ctx.fill(x1, winY + 2, x2, winY + BANNER, 0x12FFFFFF);
        }

        // Título grande
        MatrixStack ms = ctx.getMatrices();
        ms.push();
        ms.translate(winX + 14, winY + 9, 0);
        ms.scale(2f, 2f, 1f);
        Draw.text(ctx, "TungTungClient", 0, 0, 0xFFFFFFFF);
        ms.pop();

        Draw.roundRect(ctx, winX + 14, winY + 30, 44, 2, 1, accent);
        int modules = Math.max(0, ModuleManager.all().size() - 1);
        Draw.text(ctx, "v1.2  -  " + modules + " módulos", winX + 14, winY + 37, Theme.MUTED);

        // Botón del editor de HUD
        int bx = hudButtonX();
        int by = winY + 14;
        boolean over = inRect(mouseX, mouseY, bx, by, HUD_BTN_W, 20);
        Draw.roundRect(ctx, bx, by, HUD_BTN_W, 20, 6, over ? Draw.lerp(accent, 0xFFFFFFFF, 0.2f) : accent);
        Draw.centeredText(ctx, "Editar HUD", bx + HUD_BTN_W / 2, by + 6, 0xFFFFFFFF);
    }

    private void renderTabs(DrawContext ctx, int mouseX, int mouseY, int accent) {
        for (int i = 0; i < TAB_NAMES.length; i++) {
            int x = tabX(i);
            int y = winY + TAB_Y;
            int w = tabW(i);
            boolean sel = i == tab;
            boolean hover = inRect(mouseX, mouseY, x, y, w, TAB_H);
            Draw.roundRect(ctx, x, y, w, TAB_H, 8, sel ? accent : (hover ? CARD_HOVER : Theme.CARD));
            Draw.centeredText(ctx, TAB_NAMES[i], x + w / 2, y + 4, sel ? 0xFFFFFFFF : Theme.MUTED);
        }
    }

    private String fit(String s, int width) {
        if (Draw.width(s) <= width) {
            return s;
        }
        return this.textRenderer.trimToWidth(s, width - Draw.width("...")) + "...";
    }

    private void renderGrid(DrawContext ctx, int mouseX, int mouseY, int accent) {
        List<ClientModule> mods = visible();
        ctx.enableScissor(gridX, gridY, gridX + gridW, gridY + gridH);

        for (int i = 0; i < mods.size(); i++) {
            int cx = gridX + (i % 2) * (cardW + GAP);
            int cy = gridY + (i / 2) * (CARD_H + GAP) - (int) gridScroll;
            if (cy + CARD_H < gridY || cy > gridY + gridH) {
                continue;
            }
            ClientModule m = mods.get(i);
            boolean sel = m == selected;
            boolean hover = inRect(mouseX, mouseY, gridX, gridY, gridW, gridH)
                    && inRect(mouseX, mouseY, cx, cy, cardW, CARD_H);

            int bg = sel ? Draw.lerp(Theme.CARD, accent, 0.22f) : (hover ? CARD_HOVER : Theme.CARD);
            Draw.roundRect(ctx, cx, cy, cardW, CARD_H, 5, bg);
            if (sel) {
                Draw.roundRect(ctx, cx, cy + 8, 2, CARD_H - 16, 1, accent);
            }

            Draw.text(ctx, fit(m.getName(), cardW - 46), cx + 10, cy + 8, Theme.TEXT);
            Draw.text(ctx, fit(m.getDescription(), cardW - 20), cx + 10, cy + 24, Theme.MUTED);
            if (m.isToggleable()) {
                drawSwitch(ctx, cx + cardW - 32, cy + 8, 22, 11, anim(m));
            }
        }

        ctx.disableScissor();

        // Barra de scroll
        int max = maxGridScroll();
        if (max > 0) {
            int thumbH = Math.max(16, gridH * gridH / (gridH + max));
            int thumbY = gridY + Math.round((gridH - thumbH) * (gridScroll / max));
            Draw.roundRect(ctx, gridX + gridW + 3, thumbY, 2, thumbH, 1, Draw.withAlpha(accent, 0xAA));
        }
    }

    private void renderPane(DrawContext ctx, int accent) {
        if (selected == null) {
            return;
        }

        Draw.text(ctx, selected.getName(), paneX, paneY, Theme.TEXT);
        if (selected.isToggleable()) {
            drawSwitch(ctx, paneX + paneW - 28, paneY - 2, 28, 14, anim(selected));
        }

        List<OrderedText> lines = this.textRenderer.wrapLines(Text.literal(selected.getDescription()), paneW);
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            ctx.drawText(this.textRenderer, lines.get(i), paneX, paneY + 14 + i * 10, Theme.MUTED, false);
        }
        ctx.fill(paneX, paneY + PANE_HEAD - 8, paneX + paneW, paneY + PANE_HEAD - 7, SEPARATOR);

        int top = paneY + PANE_HEAD;
        ctx.enableScissor(paneX - 2, top, paneX + paneW + 2, paneY + paneH);
        int y = top - (int) paneScroll;
        for (Setting s : selected.getSettings()) {
            renderSetting(ctx, s, y, accent);
            y += settingHeight(s);
        }
        ctx.disableScissor();
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
            Draw.text(ctx, s.getLabel(), paneX, y + 2, Theme.TEXT);
            String value = num.format();
            Draw.text(ctx, value, paneX + paneW - Draw.width(value), y + 2, Theme.MUTED);

            int fill = (int) Math.round(paneW * num.fraction());
            Draw.roundRect(ctx, paneX, y + 16, paneW, 4, 2, Theme.OFF);
            Draw.roundRect(ctx, paneX, y + 16, fill, 4, 2, accent);
            int knobX = Math.max(paneX, Math.min(paneX + fill - 4, paneX + paneW - 8));
            Draw.roundRect(ctx, knobX, y + 14, 8, 8, 4, 0xFFFFFFFF);
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

        // Botón del editor de HUD
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && inRect(mouseX, mouseY, hudButtonX(), winY + 14, HUD_BTN_W, 20)) {
            this.client.setScreen(new HudEditorScreen(this));
            return true;
        }

        // Pestañas
        for (int i = 0; i < TAB_NAMES.length; i++) {
            if (inRect(mouseX, mouseY, tabX(i), winY + TAB_Y, tabW(i), TAB_H)) {
                tab = i;
                gridScroll = 0;
                List<ClientModule> mods = visible();
                if (!mods.isEmpty() && !mods.contains(selected)) {
                    selected = mods.get(0);
                    paneScroll = 0;
                }
                return true;
            }
        }

        // Tarjetas de módulos
        if (inRect(mouseX, mouseY, gridX, gridY, gridW, gridH)) {
            int localX = (int) (mouseX - gridX);
            int localY = (int) (mouseY - gridY + gridScroll);
            int col = localX / (cardW + GAP);
            int row = localY / (CARD_H + GAP);
            boolean inCard = col <= 1
                    && localX - col * (cardW + GAP) < cardW
                    && localY - row * (CARD_H + GAP) < CARD_H;
            List<ClientModule> mods = visible();
            int idx = row * 2 + col;
            if (inCard && idx >= 0 && idx < mods.size()) {
                ClientModule m = mods.get(idx);
                int cx = gridX + col * (cardW + GAP);
                boolean onSwitch = mouseX >= cx + cardW - 36 && localY - row * (CARD_H + GAP) < 26;
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && onSwitch && m.isToggleable()) {
                    m.toggle();
                } else {
                    if (selected != m) {
                        paneScroll = 0;
                    }
                    selected = m;
                }
            }
            return true;
        }

        // Panel de ajustes
        if (selected != null) {
            if (selected.isToggleable() && inRect(mouseX, mouseY, paneX + paneW - 28, paneY - 2, 28, 14)) {
                selected.toggle();
                return true;
            }
            int top = paneY + PANE_HEAD;
            if (inRect(mouseX, mouseY, paneX, top, paneW, paneY + paneH - top)) {
                int y = top - (int) paneScroll;
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
        if (inRect(mouseX, mouseY, gridX, gridY, gridW, gridH)) {
            gridScroll = clamp(gridScroll - (float) amount * 20f, 0, maxGridScroll());
            return true;
        }
        if (inRect(mouseX, mouseY, paneX, paneY, paneW, paneH)) {
            paneScroll = clamp(paneScroll - (float) amount * 20f, 0, maxPaneScroll());
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
