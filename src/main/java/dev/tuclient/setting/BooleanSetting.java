package dev.tuclient.setting;

import com.google.gson.JsonObject;

public class BooleanSetting extends Setting {

    private boolean value;

    public BooleanSetting(String key, String label, boolean defaultValue) {
        super(key, label);
        this.value = defaultValue;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public void save(JsonObject json) {
        json.addProperty(getKey(), value);
    }

    @Override
    public void load(JsonObject json) {
        if (json.has(getKey())) {
            value = json.get(getKey()).getAsBoolean();
        }
    }
}
