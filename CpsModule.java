package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.util.ClickTracker;

public class CpsModule extends HudModule {

    private final BooleanSetting showRight = register(new BooleanSetting("showRight", "Mostrar clic derecho", true));

    public CpsModule() {
        super("cps", "CPS", "Clics por segundo del ratón.", true);
        setDefaultPosition(6, 26);
    }

    @Override
    public String getLabel() {
        return "CPS";
    }

    @Override
    public String getValue() {
        if (showRight.get()) {
            return ClickTracker.left() + " | " + ClickTracker.right();
        }
        return String.valueOf(ClickTracker.left());
    }
}
