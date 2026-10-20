package dev.tuclient.mixin;

import dev.tuclient.module.ModuleManager;
import dev.tuclient.module.impl.CrosshairModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    /** Si la mira personalizada está activa, se oculta la de Minecraft. */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void tuclient$hideVanillaCrosshair(DrawContext context, CallbackInfo ci) {
        CrosshairModule module = ModuleManager.get(CrosshairModule.class);
        if (module != null && module.isEnabled()) {
            ci.cancel();
        }
    }
}
