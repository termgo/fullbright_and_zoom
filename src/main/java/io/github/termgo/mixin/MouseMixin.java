package io.github.termgo.mixin;

import io.github.termgo.FullbrightAndZoomClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
	@Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
	private void fullbright_and_zoom$adjustZoom(long window, double horizontal, double vertical,
												CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (vertical != 0.0
				&& FullbrightAndZoomClient.isZooming()
				&& client.currentScreen == null) {
			FullbrightAndZoomClient.adjustZoom(vertical);
			ci.cancel();
		}
	}
}
