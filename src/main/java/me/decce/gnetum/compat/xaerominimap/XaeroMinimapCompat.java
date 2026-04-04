package me.decce.gnetum.compat.xaerominimap;

import me.decce.gnetum.Gnetum;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
//? if xaerominimap {
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;
//? }

public class XaeroMinimapCompat {
	public static final boolean INSTALLED = Gnetum.platform().isModLoaded("xaerominimap");
	public static final String ELEMENT = "xaero_minimap";
	public static boolean statusEffectsShown;
	public static boolean bossBarShown;
	public static int bossBarHeight;
	public static boolean shouldRenderWaypoint;
	public static boolean error;

	//? if xaerominimap {
	public static void update() {
		runSafely(() -> {
			if (statusEffectsShown) {
				XaeroMinimapCore.onRenderStatusEffectOverlayPost(null);
			}
			if (bossBarShown) {
				XaeroMinimapCore.onBossHealthRender(bossBarHeight);
			}
		});
	}

	public static void tryRenderWaypoint(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		runSafely(() -> {
			shouldRenderWaypoint = true;
			HudMod.INSTANCE.getEvents().handleRenderGameOverlayEventPre(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
			shouldRenderWaypoint = false;
		});
	}
	//? }

	private static void runSafely(Runnable runnable) {
		if (!INSTALLED || error) {
			return;
		}
		try {
			runnable.run();
		}
		catch (Throwable throwable) {
			error = true;
			Gnetum.LOGGER.error("Error while invoking external method. Please report to Gnetum developer.", throwable);
		}
	}
}
