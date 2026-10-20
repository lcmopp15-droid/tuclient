package dev.tuclient.setting;

import com.google.gson.JsonObject;

public abstract class Setting {

    private final String key;
    private final String label;

    protected Setting(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public abstract void save(JsonObject json);

    public abstract void load(JsonObject json);
}
