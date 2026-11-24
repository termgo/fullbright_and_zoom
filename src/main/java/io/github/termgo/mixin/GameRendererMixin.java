package io.github.termgo.mixin;

import io.github.termgo.FullbrightAndZoomClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void fullbright_and_zoom$applyZoom(Camera camera, float tickDelta, boolean changingFov,
                                                CallbackInfoReturnable<Double> cir) {
        if (FullbrightAndZoomClient.isZooming()) {
            cir.setReturnValue(cir.getReturnValue() / FullbrightAndZoomClient.ZOOM_FACTOR);
        }
    }
}
