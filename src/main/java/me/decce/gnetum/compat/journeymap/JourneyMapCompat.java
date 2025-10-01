package me.decce.gnetum.compat.journeymap;

import journeymap.client.event.handlers.HudOverlayHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.fml.ModList;

public class JourneyMapCompat {
	public static final boolean INSTALLED = ModList.get().isLoaded("journeymap");

	public static void invokeRenderWaypointDecos(GuiGraphics graphics) {
		if (INSTALLED) {
			HudOverlayHandler.getInstance().renderWaypointDecos(graphics);
		}
	}
}
