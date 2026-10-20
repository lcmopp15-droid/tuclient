package dev.tuclient.setting;

import com.google.gson.JsonObject;

import java.util.List;

/** Ajuste con varias opciones; al hacer clic pasa a la siguiente. */
public class ModeSetting extends Setting {

    private final List<String> options;
    private int index;

    public ModeSetting(String key, String label, List<String> options, int defaultIndex) {
        super(key, label);
        this.options = options;
        this.index = Math.max(0, Math.min(options.size() - 1, defaultIndex));
    }

    public String get() {
        return options.get(index);
    }

    public int getIndex() {
        return index;
    }

    public void next() {
        index = (index + 1) % options.size();
    }

    @Override
    public void save(JsonObject json) {
        json.addProperty(getKey(), get());
    }

    @Override
    public void load(JsonObject json) {
        if (json.has(getKey())) {
            int found = options.indexOf(json.get(getKey()).getAsString());
            if (found >= 0) {
                index = found;
            }
        }
    }
}
