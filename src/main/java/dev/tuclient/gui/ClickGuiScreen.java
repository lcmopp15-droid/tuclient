package dev.tuclient.gui;

import dev.tuclient.config.ConfigManager;
import dev.tuclient.module.ClientModule;
import dev.tuclient.module.ModuleManager;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.setting.NumberSetting;
import dev.tuclient.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Menú principal. Clic izquierdo en un módulo = activar/desactivar.
 * Clic derecho = desplegar sus ajustes.
 */
public class ClickGuiScreen extends Screen {

    private static final int W = 190;
    private static final int TOP = 20;
    private static final int HEADER = 22;
    private static final int ROW_H = 18;
    private static final int BOOL_H = 16;
    private static final int NUM_H = 24;
    private static final int SLIDER_L = 14;
    private static final int SLIDER_R = 8;

    private static final int ACCENT = 0xFF4F8EF7;
    private static final int ACCENT_HOVER = 0xFF6AA0F9;
    private static final int PANEL = 0xE0161A22;
    private static final int ROW = 0xE01E232D;
    private static final int ROW_HOVER = 0xE0293040;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED = 0xFFAAB2C0;
    private static final int OFF = 0xFF5A6272;
    private static final int ON = 0xFF43C463;

    private enum Kind { MODULE, SETTING, BUTTON }

    private record Row(Kind kind, ClientModule module, Setting setting, int y, int h) {
    }

    private final Set<ClientModule> expanded = new HashSet<>();
    private NumberSetting draggingNumber;

    public ClickGuiScreen() {
        super(Text.literal("TuClient"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private int left() {
        return (this.width - W) / 2;
    }

    private List<Row> buildRows() {
        List<Row> rows = new ArrayList<>();
        int y = HEADER;
        for (ClientModule module : ModuleManager.all()) {
            rows.add(new Row(Kind.MODULE, module, null, y, ROW_H));
            y += ROW_H;
            if (expanded.contains(module)) {
                for (Setting setting : module.getSettings()) {
                    int h = setting instanceof NumberSetting ? NUM_H : BOOL_H;
                    rows.add(new Row(Kind.SETTING, module, setting, y, h));
                    y += h;
                }
            }
        }
        rows.add(new Row(Kind.BUTTON, null, null, y + 2, ROW_H + 2));
        return rows;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x66000000);

        List<Row> rows = buildRows();
        Row last = rows.get(rows.size() - 1);
        int left = left();
        int panelH = last.y() + last.h() + 6;

        ctx.fill(left, TOP, left + W, TOP + panelH, PANEL);
        ctx.fill(left, TOP, left + W, TOP + HEADER, ACCENT);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, left + W / 2, TOP + 7, TEXT);

        for (Row row : rows) {
            int ry = TOP + row.y();
            boolean hover = mouseX >= left && mouseX < left + W && mouseY >= ry && mouseY < ry + row.h();

            switch (row.kind()) {
                case MODULE -> renderModule(ctx, row, left, ry, hover);
                case SETTING -> renderSetting(ctx, row, left, ry);
                case BUTTON -> {
                    ctx.fill(left + 8, ry, left + W - 8, ry + row.h(), hover ? ACCENT_HOVER : ACCENT);
                    ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Editar HUD"),
                            left + W / 2, ry + 6, TEXT);
                }
            }
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderModule(DrawContext ctx, Row row, int left, int ry, boolean hover) {
        ClientModule module = row.module();
        ctx.fill(left, ry, left + W, ry + row.h(), hover ? ROW_HOVER : ROW);
        ctx.drawTextWithShadow(this.textRenderer, module.getName(), left + 8, ry + 5, TEXT);

        boolean on = module.isEnabled();
        int pillW = 26;
        int px = left + W - pillW - 8;
        ctx.fill(px, ry + 3, px + pillW, ry + row.h() - 3, on ? ON : OFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(on ? "ON" : "OFF"),
                px + pillW / 2, ry + 5, TEXT);

        if (!module.getSettings().isEmpty()) {
            String sign = expanded.contains(module) ? "-" : "+";
            ctx.drawTextWithShadow(this.textRenderer, sign, px - 10, ry + 5, MUTED);
        }
    }

    private void renderSetting(DrawContext ctx, Row row, int left, int ry) {
        Setting setting = row.setting();
        ctx.fill(left, ry, left + W, ry + row.h(), ROW);

        if (setting instanceof BooleanSetting bool) {
            ctx.drawTextWithShadow(this.textRenderer, bool.getLabel(), left + SLIDER_L, ry + 4, MUTED);
            int bx = left + W - SLIDER_R - 10;
            ctx.fill(bx, ry + 3, bx + 10, ry + 13, bool.get() ? ACCENT : OFF);
        } else if (setting instanceof NumberSetting num) {
            ctx.drawTextWithShadow(this.textRenderer, num.getLabel(), left + SLIDER_L, ry + 3, MUTED);
            String value = num.format();
            ctx.drawTextWithShadow(this.textRenderer, value,
                    left + W - SLIDER_R - this.textRenderer.getWidth(value), ry + 3, TEXT);

            int sx = left + SLIDER_L;
            int ex = left + W - SLIDER_R;
            ctx.fill(sx, ry + 15, ex, ry + 19, OFF);
            ctx.fill(sx, ry + 15, sx + (int) ((ex - sx) * num.fraction()), ry + 19, ACCENT);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int left = left();
        for (Row row : buildRows()) {
            int ry = TOP + row.y();
            if (mouseX < left || mouseX >= left + W || mouseY < ry || mouseY >= ry + row.h()) {
                continue;
            }

            switch (row.kind()) {
                case MODULE -> {
                    ClientModule module = row.module();
                    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        module.toggle();
                    } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && !module.getSettings().isEmpty()) {
                        if (!expanded.remove(module)) {
                            expanded.add(module);
                        }
                    }
                }
                case SETTING -> {
                    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        if (row.setting() instanceof BooleanSetting bool) {
                            bool.toggle();
                        } else if (row.setting() instanceof NumberSetting num) {
                            draggingNumber = num;
                            updateSlider(num, mouseX);
                        }
                    }
                }
                case BUTTON -> {
                    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        this.client.setScreen(new HudEditorScreen(this));
                    }
                }
            }
            return true;
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

    private void updateSlider(NumberSetting setting, double mouseX) {
        double start = left() + SLIDER_L;
        double end = left() + W - SLIDER_R;
        setting.setFraction((mouseX - start) / (end - start));
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
