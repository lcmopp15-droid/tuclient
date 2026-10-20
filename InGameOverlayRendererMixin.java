package dev.tuclient.mixin;

import dev.tuclient.module.ModuleManager;
import dev.tuclient.module.impl.LowFireModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    /** Fuego bajo: baja el overlay de fuego de la pantalla. */
    @Inject(method = "renderFireOverlay", at = @At("HEAD"))
    private static void tuclient$lowFire(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        LowFireModule module = ModuleManager.get(LowFireModule.class);
        if (module != null && module.isEnabled()) {
            matrices.translate(0.0, -module.offset(), 0.0);
        }
    }
}
