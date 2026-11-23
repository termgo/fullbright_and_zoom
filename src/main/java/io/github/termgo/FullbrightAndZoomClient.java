package io.github.termgo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FullbrightAndZoomClient implements ClientModInitializer {
	public static KeyBinding FULLBRIGHT_KEY;
	public static KeyBinding ZOOM_KEY;

	@Override
	public void onInitializeClient() {
		FULLBRIGHT_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.fullbright_and_zoom.fullbright",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"key.categories.fullbright_and_zoom"
		));

		ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.fullbright_and_zoom.zoom",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_C,
				"key.categories.fullbright_and_zoom"
		));
	}
}
