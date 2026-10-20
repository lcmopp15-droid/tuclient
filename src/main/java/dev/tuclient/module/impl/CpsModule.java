package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.setting.BooleanSetting;
import dev.tuclient.util.ClickTracker;

public class CpsModule extends HudModule {

    private final BooleanSetting showRight = register(new BooleanSetting("showRight", "Clic derecho", true));

    public CpsModule() {
        super("cps", "CPS", true);
    }

    @Override
    public String getText() {
        if (showRight.get()) {
            return ClickTracker.left() + " | " + ClickTracker.right() + " CPS";
        }
        return ClickTracker.left() + " CPS";
    }
}
