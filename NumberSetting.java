package dev.tuclient.setting;

import com.google.gson.JsonObject;

import java.util.Locale;

public class NumberSetting extends Setting {

    private final double min;
    private final double max;
    private final double step;
    private double value;

    public NumberSetting(String key, String label, double min, double max, double step, double defaultValue) {
        super(key, label);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = clamp(defaultValue);
    }

    private double clamp(double v) {
        return Math.max(min, Math.min(max, v));
    }

    public double get() {
        return value;
    }

    public void set(double v) {
        this.value = clamp(v);
    }

    /** Posición del valor dentro del rango, de 0.0 a 1.0. */
    public double fraction() {
        return (value - min) / (max - min);
    }

    public void setFraction(double f) {
        f = Math.max(0.0, Math.min(1.0, f));
        double raw = min + f * (max - min);
        set(Math.round(raw / step) * step);
    }

    public String format() {
        if (step >= 1) {
            return String.valueOf((int) Math.round(value));
        }
        if (step >= 0.1) {
            return String.format(Locale.ROOT, "%.1f", value);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    public void save(JsonObject json) {
        json.addProperty(getKey(), value);
    }

    @Override
    public void load(JsonObject json) {
        if (json.has(getKey())) {
            set(json.get(getKey()).getAsDouble());
        }
    }
}
