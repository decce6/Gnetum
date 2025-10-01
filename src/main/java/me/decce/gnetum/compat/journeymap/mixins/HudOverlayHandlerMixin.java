package me.decce.gnetum.compat.journeymap.mixins;

import journeymap.client.event.handlers.HudOverlayHandler;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HudOverlayHandler.class, remap = false)
public class HudOverlayHandlerMixin {
	@Inject(method = "renderWaypointDecos", at = @At("HEAD"), cancellable = true)
	private void gnetum$cancelRenderWaypointDecos(GuiGraphics graphics, CallbackInfo ci) {
		if (Gnetum.rendering) {
			ci.cancel();
		}
	}
}
