package dev.tuclient.mixin;

import dev.tuclient.module.ModuleManager;
import dev.tuclient.module.impl.FovModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {

    /** FOV estático: quita el cambio de FOV por sprint/velocidad (respeta el zoom del arco/catalejo). */
    @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
    private void tuclient$staticFov(CallbackInfoReturnable<Float> cir) {
        FovModule module = ModuleManager.get(FovModule.class);
        AbstractClientPlayerEntity self = (AbstractClientPlayerEntity) (Object) this;
        if (module != null && module.isEnabled() && module.isStatic() && !self.isUsingItem()) {
            cir.setReturnValue(1.0F);
        }
    }
}
