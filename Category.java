package dev.tuclient.module;

public enum Category {
    HUD("HUD"),
    VISUAL("Visual"),
    GAME("Juego"),
    SETTINGS("Ajustes");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
