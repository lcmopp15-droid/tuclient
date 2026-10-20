package dev.tuclient.module;

import com.google.gson.JsonObject;
import dev.tuclient.setting.Setting;

import java.util.ArrayList;
import java.util.List;

/** Clase base de cualquier módulo del cliente. */
public abstract class ClientModule {

    private final String id;
    private final String name;
    private final String description;
    private boolean enabled;
    private final List<Setting> settings = new ArrayList<>();

    protected ClientModule(String id, String name, String description, boolean enabledByDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabledByDefault;
    }

    protected <T extends Setting> T register(T setting) {
        settings.add(setting);
        return setting;
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
