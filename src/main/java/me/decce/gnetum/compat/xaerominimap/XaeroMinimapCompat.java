package me.decce.gnetum.compat.xaerominimap;

import me.decce.gnetum.Gnetum;
//? if xaerominimap {
import xaero.common.core.XaeroMinimapCore;
//? }

public class XaeroMinimapCompat {
	public static final boolean INSTALLED = Gnetum.platform().isModLoaded("xaerominimap");
	public static boolean statusEffectsShown;
	public static boolean bossBarShown;
	public static int bossBarHeight;
	public static boolean error;

	//? if xaerominimap {
	public static void update() {
		if (!INSTALLED || error) {
			return;
		}
		try {
			if (statusEffectsShown) {
				XaeroMinimapCore.onRenderStatusEffectOverlayPost(null);
			}
			if (bossBarShown) {
				XaeroMinimapCore.onBossHealthRender(bossBarHeight);
			}
		} catch (Throwable throwable) {
			error = true;
			Gnetum.LOGGER.error("Error while invoking external method. Please report to Gnetum developer.", throwable);
		}
	}
	//? }
}
