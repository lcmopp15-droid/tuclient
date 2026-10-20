package dev.tuclient.module.impl;

import dev.tuclient.module.Category;
import dev.tuclient.module.ClientModule;
import dev.tuclient.setting.NumberSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

/** Cambia la posición y el tamaño de la mano y el objeto en primera persona. */
public class ViewmodelModule extends ClientModule {

    private final NumberSetting x = register(new NumberSetting("x", "Posición X", -1.0, 1.0, 0.05, 0.0));
    private final NumberSetting y = register(new NumberSetting("y", "Posición Y", -1.0, 1.0, 0.05, 0.0));
    private final NumberSetting z = register(new NumberSetting("z", "Posición Z", -1.0, 1.0, 0.05, 0.0));
    private final NumberSetting scale = register(new NumberSetting("scale", "Tamaño", 0.3, 1.5, 0.05, 1.0));

    public ViewmodelModule() {
        super("viewmodel", "Viewmodel", "Mueve y cambia el tamaño de tu mano y de lo que sostienes.",
                Category.VISUAL, false);
    }

    public void apply(MatrixStack matrices, Hand hand) {
        float side = hand == Hand.MAIN_HAND ? 1f : -1f;
        matrices.translate(x.get() * side, y.get(), z.get());
        float s = (float) scale.get();
        matrices.scale(s, s, s);
    }
}
