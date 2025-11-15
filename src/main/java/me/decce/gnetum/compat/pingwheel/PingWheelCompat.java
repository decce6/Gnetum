package me.decce.gnetum.compat.pingwheel;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.fml.ModList;
import nx.pingwheel.common.CommonClient;

public class PingWheelCompat {
	public static final boolean INSTALLED = ModList.get().isLoaded("pingwheel") || ModList.get().isLoaded("ping-wheel");

	public static void invokeRenderGUI(GuiGraphics guiGraphics, float tickDelta) {
		if (INSTALLED) {
            var stack = guiGraphics.pose();
            stack.pushPose();
            try {
                stack.translate(0, 0, -90);
                CommonClient.INSTANCE.onRenderGUI(guiGraphics, tickDelta);
            } finally {
                stack.popPose();
            }
        }
	}
}
