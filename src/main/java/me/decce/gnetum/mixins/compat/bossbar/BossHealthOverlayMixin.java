package me.decce.gnetum.mixins.compat.bossbar;

import me.decce.gnetum.compat.jade.JadeCompat;
import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
	//? >26 {
	/*@Inject(method = "extractRenderState", at = @At("HEAD"))
	*///? } else {
	@Inject(method = "render", at = @At("HEAD"))
	//? }
	private void gnetum$render$0(GuiGraphics guiGraphics, CallbackInfo ci) {
		//? if jade {
		JadeCompat.bossBarShown = false;
		//? }
		//? if xaerominimap {
		XaeroMinimapCompat.bossBarShown = false;
		//? }
	}

	//? >26 {
	/*@Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", ordinal = 0))
	*///? } else {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", ordinal = 0))
	//? }
	private void gnetum$render$1(GuiGraphics guiGraphics, CallbackInfo ci) {
		//? if jade {
		JadeCompat.bossBarShown = true;
		//? }
		//? if xaerominimap {
		XaeroMinimapCompat.bossBarShown = true;
		//? }
	}

	//? >26 {
	/*@Inject(method = "extractBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"))
	*///? } else {
	@Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"))
	//? }
	private void gnetum$drawBar(GuiGraphics guiGraphics, int i, int j, BossEvent bossEvent, CallbackInfo ci) {
		//? if xaerominimap {
		XaeroMinimapCompat.bossBarHeight = j + 19;
		//? }
	}
}
