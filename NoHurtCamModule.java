package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;

/** Quita la sacudida de la cámara al recibir daño (la lógica está en el mixin). */
public class NoHurtCamModule extends ClientModule {

    public NoHurtCamModule() {
        super("nohurtcam", "Sin sacudida", "La cámara no se sacude cuando recibes daño.",
                Category.VISUAL, false);
    }
}
