package io.github.termgo.mixin;

import io.github.termgo.FullbrightAndZoomClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("HEAD"))
    private void fullbright_and_zoom$pollZoomKey(Camera camera, float tickDelta, boolean changingFov,
                                                 CallbackInfoReturnable<Double> cir) {
        FullbrightAndZoomClient.updateZoomState(MinecraftClient.getInstance());
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void fullbright_and_zoom$applyZoom(Camera camera, float tickDelta, boolean changingFov,
                                               CallbackInfoReturnable<Double> cir) {
        double progress = FullbrightAndZoomClient.getZoomProgress();
        if (progress > 0.0) {
            double zoomFactor = 1.0
                    + (FullbrightAndZoomClient.getZoomFactor() - 1.0) * progress;
            cir.setReturnValue(cir.getReturnValue() / zoomFactor);
        }
    }
}
