package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;
import dev.tuclient.setting.BooleanSetting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeModule extends HudModule {

    private static final DateTimeFormatter H24 = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter H12 = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    private final BooleanSetting format24 = register(new BooleanSetting("format24", "Formato de 24 horas", true));

    public TimeModule() {
        super("time", "Hora", "La hora real de tu computadora.", false);
        setDefaultPosition(170, 26);
    }

    @Override
    public String getValue() {
        return LocalTime.now().format(format24.get() ? H24 : H12);
    }
}
