package me.decce.gnetum.mixins.compat.pingwheel;

import me.decce.gnetum.Gnetum;
import nx.pingwheel.common.CommonClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = CommonClient.class, remap = false)
public class CommonClientMixin {
	@Inject(method = "onRenderGUI", at = @At("HEAD"), cancellable = true)
	private void gnetum$cancelRenderGUI(CallbackInfo ci) {
		if (Gnetum.rendering) {
			ci.cancel();
		}
	}
}
