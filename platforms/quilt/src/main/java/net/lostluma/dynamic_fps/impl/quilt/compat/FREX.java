package net.lostluma.dynamic_fps.impl.quilt.compat;

import dynamic_fps.impl.DynamicFPSMod;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * Implements the FREX Flawless Frames API to allow other mods to request all
 * frames to be processed until requested to
 * go back to normal operation, such as ReplayMod rendering a video.
 * <p>
 * See https://github.com/grondag/frex/pull/9
 */
public final class FREX implements ClientModInitializer {
	private static final Set<Object> ACTIVE = ConcurrentHashMap.newKeySet();

	/**
	 * Returns whether one or more mods have requested Flawless Frames to be active,
	 * and therefore frames should not be skipped.
	 */
	public static boolean isFlawlessFramesActive() {
		return !ACTIVE.isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onInitializeClient(ModContainer mod) {
		Function<String, Consumer<Boolean>> provider = name -> {
			Object token = new Object();

			return active -> {
				if (active) {
					ACTIVE.add(token);
				} else {
					ACTIVE.remove(token);
				}

				DynamicFPSMod.onStatusChanged(false);
			};
		};

		QuiltLoader.getEntrypoints("frex_flawless_frames", Consumer.class).forEach(api -> api.accept(provider));
	}
}
