package me.decce.gnetum.mixins.compat.jade;

import me.decce.gnetum.compat.jade.JadeCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void gnetum$render$0(GuiGraphics guiGraphics, CallbackInfo ci) {
		JadeCompat.bossBarShown = false;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", ordinal = 0))
	private void gnetum$render$1(GuiGraphics guiGraphics, CallbackInfo ci) {
		JadeCompat.bossBarShown = true;
	}
}
