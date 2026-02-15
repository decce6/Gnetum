package me.decce.gnetum.mixins;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "buildInitialScreens", at = @At("TAIL"))
	private void gnetum$finishInitialization(CallbackInfoReturnable<Runnable> cir) {
		Gnetum.platform().elementGatherer().gather();
		Gnetum.config.save();
	}

	@Inject(method = "setLevel", at = @At("TAIL"))
	private void gnetum$setLevel(CallbackInfo ci) {
		Gnetum.reset();
	}
}
