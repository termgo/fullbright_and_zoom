package io.github.termgo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FullbrightAndZoomClient implements ClientModInitializer {
	public static final double FULLBRIGHT_GAMMA = 10.0;
	public static final double ZOOM_FACTOR = 4.0;
	private static final long ZOOM_TRANSITION_NANOS = 220_000_000L;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance()
			.getConfigDir().resolve("fullbright_and_zoom.json");

	public static KeyBinding FULLBRIGHT_KEY;
	public static KeyBinding ZOOM_KEY;

	private static boolean fullbrightEnabled;
	private static boolean zooming;
	private static boolean zoomTarget;
	private static boolean previousSmoothCamera;
	private static boolean cinematicCameraOverridden;
	private static double zoomTransitionStartProgress;
	private static long zoomTransitionStartNanos = System.nanoTime();

	@Override
	public void onInitializeClient() {
		fullbrightEnabled = loadConfig().fullbrightEnabled;

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

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (FULLBRIGHT_KEY.wasPressed()) {
				toggleFullbright(client);
			}

			boolean shouldZoom = client.player != null
					&& client.currentScreen == null
					&& ZOOM_KEY.isPressed();
			setZooming(client, shouldZoom);
		});
	}

	public static boolean isFullbrightEnabled() {
		return fullbrightEnabled;
	}

	/** Returns an eased zoom amount between 0 (normal FOV) and 1 (full zoom). */
	public static double getZoomProgress() {
		long elapsed = Math.max(0L, System.nanoTime() - zoomTransitionStartNanos);
		double amount = Math.min(1.0, (double) elapsed / ZOOM_TRANSITION_NANOS);
		// Smoothstep easing gives the transition a gentle start and stop.
		double eased = amount * amount * (3.0 - 2.0 * amount);
		return zoomTransitionStartProgress + ((zoomTarget ? 1.0 : 0.0) - zoomTransitionStartProgress) * eased;
	}

	private static void setZooming(MinecraftClient client, boolean shouldZoom) {
		if (zooming == shouldZoom) {
			return;
		}

		zoomTransitionStartProgress = getZoomProgress();
		zoomTransitionStartNanos = System.nanoTime();
		zoomTarget = shouldZoom;
		zooming = shouldZoom;

		if (client.options == null) {
			return;
		}

		if (shouldZoom) {
			// Temporarily enable Minecraft's cinematic camera behavior while zooming.
			previousSmoothCamera = client.options.smoothCamera;
			client.options.smoothCamera = true;
			cinematicCameraOverridden = true;
		} else if (cinematicCameraOverridden) {
			client.options.smoothCamera = previousSmoothCamera;
			cinematicCameraOverridden = false;
		}
	}

	private static void toggleFullbright(MinecraftClient client) {
		fullbrightEnabled = !fullbrightEnabled;
		saveConfig();

		if (client.player != null) {
			Text status = Text.literal(fullbrightEnabled ? "Enabled (1000%)" : "Disabled")
					.formatted(fullbrightEnabled ? Formatting.GREEN : Formatting.RED);
			Text message = Text.literal("[" + Fullbright_and_zoom.MOD_ID + "] ")
					.append(Text.literal("Fullbright "))
					.append(status);
			client.player.sendMessage(message, false);
		}
	}

	private static Config loadConfig() {
		if (Files.isRegularFile(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
				Config config = GSON.fromJson(reader, Config.class);
				if (config != null) {
					return config;
				}
			} catch (IOException e) {
				Fullbright_and_zoom.LOGGER.warn("Failed to load Fullbright configuration", e);
			}
		}
		return new Config();
	}

	private static void saveConfig() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(new Config(fullbrightEnabled), writer);
			}
		} catch (IOException e) {
			Fullbright_and_zoom.LOGGER.warn("Failed to save Fullbright configuration", e);
		}
	}

	private static class Config {
		boolean fullbrightEnabled;

		Config() {
		}

		Config(boolean fullbrightEnabled) {
			this.fullbrightEnabled = fullbrightEnabled;
		}
	}
}
