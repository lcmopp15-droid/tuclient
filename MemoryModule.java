package dev.tuclient.module.impl;

import dev.tuclient.module.HudModule;

public class MemoryModule extends HudModule {

    public MemoryModule() {
        super("memory", "Memoria", "La memoria RAM que usa el juego.", false);
        setDefaultPosition(170, 66);
    }

    @Override
    public String getLabel() {
        return "RAM";
    }

    @Override
    public String getValue() {
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
        long max = rt.maxMemory() / 1048576L;
        return used + " / " + max + " MB";
    }
}
