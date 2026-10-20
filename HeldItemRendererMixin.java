package dev.tuclient.mixin;

import dev.tuclient.module.ModuleManager;
import dev.tuclient.module.impl.ViewmodelModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Viewmodel: mueve y escala la mano/objeto en primera persona. */
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void tuclient$viewmodelStart(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                         Hand hand, float swingProgress, ItemStack item, float equipProgress,
                                         MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                         int light, CallbackInfo ci) {
        ViewmodelModule module = ModuleManager.get(ViewmodelModule.class);
        if (module != null && module.isEnabled()) {
            matrices.push();
            module.apply(matrices, hand);
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At("RETURN"))
    private void tuclient$viewmodelEnd(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                       Hand hand, float swingProgress, ItemStack item, float equipProgress,
                                       MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                       int light, CallbackInfo ci) {
        ViewmodelModule module = ModuleManager.get(ViewmodelModule.class);
        if (module != null && module.isEnabled()) {
            matrices.pop();
        }
    }
}
