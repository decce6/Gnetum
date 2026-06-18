package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	//? >=26.2 {
	/*@Inject(method = "onGameLoadFinished", at = @At("TAIL"))
	private void gnetum$finishInitialization(CallbackInfo ci)
	*///? } else {
	@Inject(method = "buildInitialScreens", at = @At("TAIL"))
	private void gnetum$finishInitialization(CallbackInfoReturnable<Runnable> cir)
	//? }
	{
		Gnetum.platform().elementGatherer().gather();
		Gnetum.config.save();
	}

	@Inject(method = "setLevel", at = @At("TAIL"))
	private void gnetum$setLevel(CallbackInfo ci) {
		Gnetum.reset();
	}

	//? <=1.20.4 {
	/*@ModifyReturnValue(method = "getDeltaFrameTime", at = @At("RETURN"))
	public float getDeltaFrameTime(float original) {
		if (!Gnetum.rendering || !HudDeltaTracker.isReady()) return original;
		return HudDeltaTracker.getGameTimeDeltaTicks();
	}
	*///? }
}
