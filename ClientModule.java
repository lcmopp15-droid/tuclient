package dev.tuclient.module;

import com.google.gson.JsonObject;
import dev.tuclient.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/** Clase base de cualquier módulo del cliente. */
public abstract class ClientModule {

    private final String id;
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private final List<Setting> settings = new ArrayList<>();

    protected ClientModule(String id, String name, String description, Category category, boolean enabledByDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = enabledByDefault;
    }

    protected <T extends Setting> T register(T setting) {
        settings.add(setting);
        return setting;
    }

    /** Se llama en cada tick del juego (también si el módulo está apagado). */
    public void onTick(MinecraftClient client) {
    }

    /** Dibujo extra en pantalla para módulos que no son del HUD (por ejemplo la mira). */
    public void renderOverlay(DrawContext ctx) {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Los módulos que siempre están activos (como Apariencia) devuelven false. */
    public boolean isToggleable() {
        return true;
    }

    public void toggle() {
        if (isToggleable()) {
            this.enabled = !this.enabled;
        }
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void save(JsonObject json) {
        json.addProperty("enabled", enabled);
        JsonObject s = new JsonObject();
        for (Setting setting : settings) {
            setting.save(s);
        }
        json.add("settings", s);
    }

    public void load(JsonObject json) {
        if (json.has("enabled")) {
            enabled = json.get("enabled").getAsBoolean();
        }
        if (json.has("settings")) {
            JsonObject s = json.getAsJsonObject("settings");
            for (Setting setting : settings) {
                setting.load(s);
            }
        }
    }
}
