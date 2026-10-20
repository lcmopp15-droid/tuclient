package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.setting.ModeSetting;
import dev.tuclient.setting.NumberSetting;

import java.util.List;

/** Ajustes de estilo de todo el cliente (siempre activo). */
public class AppearanceModule extends ClientModule {

    private final ModeSetting accent = register(new ModeSetting("accent", "Color principal",
            List.of("Azul", "Violeta", "Verde", "Rojo", "Naranja", "Rosa"), 0));
    private final NumberSetting opacity = register(new NumberSetting("opacity", "Opacidad del fondo", 0, 100, 5, 55));
    private final BooleanSetting rounded = register(new BooleanSetting("rounded", "Bordes redondeados", true));
    private final BooleanSetting bar = register(new BooleanSetting("bar", "Barra de color", true));
    private final BooleanSetting shadow = register(new BooleanSetting("shadow", "Sombra del texto", true));

    public AppearanceModule() {
        super("appearance", "Apariencia", "Colores y estilo de todo el cliente.", Category.SETTINGS, true);
    }

    @Override
    public boolean isToggleable() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public int accentIndex() {
        return accent.getIndex();
    }

    public double opacity() {
        return opacity.get();
    }

    public boolean rounded() {
        return rounded.get();
    }

    public boolean accentBar() {
        return bar.get();
    }

    public boolean textShadow() {
        return shadow.get();
    }
}
