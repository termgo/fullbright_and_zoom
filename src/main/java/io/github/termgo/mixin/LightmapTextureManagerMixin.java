package io.github.termgo.mixin;

import io.github.termgo.FullbrightAndZoomClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"
            )
    )
    private Object fullbright_and_zoom$overrideGamma(SimpleOption<?> option) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (FullbrightAndZoomClient.isFullbrightEnabled()
                && client.options != null
                && option == client.options.getGamma()) {
            return FullbrightAndZoomClient.FULLBRIGHT_GAMMA;
        }

        return option.getValue();
    }
}
