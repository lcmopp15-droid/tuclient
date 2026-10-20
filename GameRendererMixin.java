package dev.tuclient.mixin;

import dev.tuclient.module.ModuleManager;
import dev.tuclient.module.impl.FovModule;
import dev.tuclient.module.impl.NoHurtCamModule;
import dev.tuclient.module.impl.ZoomModule;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void tuclient$getFov(Camera camera, float tickDelta, boolean changingFov,
                                 CallbackInfoReturnable<Double> cir) {
        // changingFov = false es el FOV de la mano; se deja como en vanilla
        if (!changingFov) {
            return;
        }
        double fov = cir.getReturnValue();

        FovModule fovModule = ModuleManager.get(FovModule.class);
        if (fovModule != null && fovModule.isEnabled()) {
            fov = fovModule.scale(fov);
        }

        ZoomModule zoom = ModuleManager.get(ZoomModule.class);
        if (zoom != null) {
            fov = zoom.apply(fov);
        }

        cir.setReturnValue(fov);
    }

    /** Sin sacudida de cámara al recibir daño. */
    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void tuclient$noHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        NoHurtCamModule module = ModuleManager.get(NoHurtCamModule.class);
        if (module != null && module.isEnabled()) {
            ci.cancel();
        }
    }
}
