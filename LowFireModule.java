package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import dev.tuclient.setting.NumberSetting;

/** Baja el fuego de la pantalla cuando te quemas para que estorbe menos. */
public class LowFireModule extends ClientModule {

    private final NumberSetting offset = register(new NumberSetting("offset", "Qué tanto baja", 0.1, 0.6, 0.05, 0.3));

    public LowFireModule() {
        super("lowfire", "Fuego bajo", "El fuego de la pantalla se ve más abajo y no tapa la vista.",
                Category.VISUAL, false);
    }

    public double offset() {
        return offset.get();
    }
}
