package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.client.Camera;
//$ import_delta_tracker
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

//? <=1.21.4 {
/*import me.decce.gnetum.CachedElement;
import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
import me.decce.gnetum.hud.HudManager;
import me.decce.gnetum.hud.SharedValues;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import static me.decce.gnetum.hud.SharedValues.guiGraphics;
*///? }
//? >=1.21.10 {
import me.decce.gnetum.versioned.StatefulHudHandler;
import org.joml.Matrix3x2f;
//?}
//? >26 {
/*import org.joml.Matrix4fc;
*///? }

//? immediatelyfast
//import me.decce.gnetum.compat.immediatelyfast.ImmediatelyFastCompat;
//? journeymap
//import me.decce.gnetum.compat.journeymap.JourneyMapCompat;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Unique
	private boolean gnetum$wasScreenOpen;
	@Unique
	private boolean gnetum$wasHudHidden;

	//? >=26.2 {
	/*@Inject(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractRenderState(Lnet/minecraft/client/DeltaTracker;ZZ)V"))
	private void gnetum$updateDeltaTrackerAndPoseCatchup(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
	*///? } else >26 {
	/*@Inject(method = "extractGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractRenderState(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void gnetum$updateDeltaTrackerAndPoseCatchup(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci, @Local GuiGraphics guiGraphics) {
	*///? } else >=1.21.1 {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void gnetum$updateDeltaTrackerAndPoseCatchup(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci, @Local GuiGraphics guiGraphics) {
	//? } else {
	/*@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
	private void gnetum$updateDeltaTrackerAndPoseCatchup(DeltaTracker deltaTracker, long l, boolean bl, CallbackInfo ci, @Local GuiGraphics guiGraphics) {
	*///? }
		if (!Gnetum.config.isEnabled()) {
			return;
		}

		//? >=1.21.1 {
		if (deltaTracker instanceof DeltaTracker.Timer timer) {
			HudDeltaTracker.update(timer);
		}
		else {
			HudDeltaTracker.disable();
		}
		//? } else {
		/*HudDeltaTracker.update(deltaTracker);
		*///? }

		// The other half is in GuiMixin
		//? <=26.1 {
		Gnetum.checkForPoseCatchUp(guiGraphics);
		//? }
	}

	//? >=1.21.10 {
	//? >=26.2 {
	/*@Inject(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractRenderState(Lnet/minecraft/client/DeltaTracker;ZZ)V"))
	*///? } else >26 {
	/*@Inject(method = "extractGui", at = @At("HEAD"))
	*///? } else {
	@Inject(method = "render", at = @At("HEAD"))
	//? }
	private void gnetum$updateScreenCatchup(CallbackInfo ci) {
		if (!Gnetum.config.isEnabled()) {
			return;
		}

		gnetum$checkForScreenCatchUp();
	}
	//? }

	//? <=1.21.4 {
	/*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void gnetum$wrapGuiRender(Gui instance, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!Gnetum.config.isEnabled() || Minecraft.getInstance().options.hideGui) {
			original.call(instance, guiGraphics, deltaTracker);
			return;
		}

		SharedValues.guiGraphics = guiGraphics;
		SharedValues.deltaTracker = deltaTracker;

		guiGraphics.pose().pushPose();

		ImmediatelyFastCompat.batchIfInstalled(guiGraphics, () -> {
			JourneyMapCompat.invokeRenderWaypointDecos(guiGraphics);
			XaeroMinimapCompat.tryRenderWaypoint(guiGraphics, deltaTracker);
			gnetum$renderVanillaHuds(CachedElement::shouldRenderAsUncached);
			gnetum$renderFabricHuds(guiGraphics, deltaTracker);
		});

		Gnetum.framebuffers().resize();
		if (Gnetum.pass == 0) {
			VersionCompatUtil.profilerPush("sleep");
		}
		else {
			VersionCompatUtil.profilerPush("pass" + Gnetum.pass);
			Gnetum.framebuffers().bind();
		}

		Gnetum.rendering = true;

		ImmediatelyFastCompat.batchIfInstalled(guiGraphics, () -> {
			gnetum$renderGuiInjection(instance, guiGraphics, deltaTracker);
			gnetum$renderVanillaHuds(CachedElement::shouldRenderAsCached);
			gnetum$renderFabricHuds(guiGraphics, deltaTracker);
		});

		VersionCompatUtil.profilerPop();

		Gnetum.rendering = false;

		Gnetum.nextPass();
		Gnetum.framebuffers().unbind();

		if (Gnetum.framebuffers().needsCatchUp()) {
			original.call(instance, guiGraphics, deltaTracker);
		}
		else {
			Gnetum.framebuffers().blit(guiGraphics);
		}

		guiGraphics.pose().popPose();

		SharedValues.guiGraphics = null;
		//? >=1.21.1
		SharedValues.deltaTracker = null;
	}

	@Unique
	private void gnetum$renderGuiInjection(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		if (Gnetum.pass == 1) { //TODO smarter distribution
			Gnetum.renderingGuiInjection = true;
			gui.render(guiGraphics, deltaTracker);
			Gnetum.renderingGuiInjection = false;
		}
	}

	@Unique
	private void gnetum$renderVanillaHuds(Predicate<CachedElement> check) {
		for (int i = 0; i < HudManager.huds.size(); i++) {
			var hud = HudManager.huds.get(i);
			var element = Gnetum.getElement(hud.id());
			if (hud.isDummy() || check.test(element)) {
				if (Gnetum.rendering) {
					element.begin();
					hud.render();
					//? 1.21.4 {
					/^guiGraphics.flush();
					^///? } else {
					ImmediatelyFastCompat.flushIfInstalledAndUsingHudBatching(guiGraphics); // Duplicates ImmediatelyFast behavior: https://github.com/RaphiMC/ImmediatelyFast/blob/e05390bbc2c2bdc3d19cad458d894dc4f605d3fb/common/src/main/java/net/raphimc/immediatelyfast/injection/mixins/hud_batching/MixinLayeredDrawer.java#L32-L37
					//? }
					element.end();
				}
				else {
					hud.render();
				}
				RenderSystem.enableDepthTest(); // Fixes GL state leak by some mods
				guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
			}
		}
	}

	@Unique
	private void gnetum$renderFabricHuds(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		HudRenderCallback.EVENT.invoker().onHudRender(guiGraphics, deltaTracker);
	}
	*///? }

	@Unique
	private void gnetum$checkForScreenCatchUp() {
		var minecraft = Minecraft.getInstance();

		boolean screenOpen = VersionCompatUtil.isInScreen();
		if (screenOpen != gnetum$wasScreenOpen) {
			gnetum$wasScreenOpen = screenOpen;
			Gnetum.framebuffers().markForCatchUp();
		}

		boolean hudHidden = VersionCompatUtil.isHudHidden();
		if (hudHidden != gnetum$wasHudHidden) {
			gnetum$wasHudHidden = hudHidden;
			Gnetum.framebuffers().markForCatchUp();
		}
	}

	@WrapMethod(method = "renderItemInHand")
	//? if >26 {
	/*private void gnetum$wrapRenderItemInHand(net.minecraft.client.renderer.state.level.CameraRenderState cameraState, float deltaPartialTicks, Matrix4fc modelViewMatrix, Operation<Void> original) {
	*///? } else if >=1.21.10 {
	private void gnetum$wrapRenderItemInHand(float f, boolean bl, Matrix4f matrix4f, Operation<Void> original) {
	//? } else if >=1.21.1 {
	/*private void gnetum$wrapRenderItemInHand(net.minecraft.client.Camera camera, float f, Matrix4f matrix4f, Operation<Void> original) {
	*///? } else {
	/*private void gnetum$wrapRenderItemInHand(PoseStack poseStack, Camera camera, float f, Operation<Void> original) {
	*///? }
		var hand = Gnetum.getElement(Constants.HAND_ELEMENT);
		Runnable callOriginal = () ->
				//? if >26 {
				/*original.call(cameraState, deltaPartialTicks, modelViewMatrix);
				 *///? } else if >=1.21.10 {
				original.call(f, bl, matrix4f);
				//? } else if >=1.21.1 {
				/*original.call(camera, f, matrix4f);
				*///? } else {
				/*original.call(poseStack, camera, f);
				*///? }
		if (!Gnetum.config.isEnabled() || hand.isUncached()) {
			callOriginal.run();
			return;
		}
		if (hand.shouldRender()) {
			// No flush needed
			hand.begin();
			Gnetum.framebuffers().bind();
			callOriginal.run();
			Gnetum.framebuffers().unbind();
			hand.end();
		}
		if (Gnetum.framebuffers().needsCatchUp()) {
			callOriginal.run();
		}
	}

	//Note: 26.2+ is in GuiMixin
	//? if >=1.21.10 && <= 26.1 {
	//? if >26 {
	/*@WrapOperation(method = "extractGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractDebugOverlay(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	*///? } else {
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderDebugOverlay(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	//? }
    private void gnetum$wrapRenderDebugOverlay(Gui gui, GuiGraphics guiGraphics, Operation<Void> original) {
		if (minecraft.isGameLoadFinished() && (!minecraft.options.hideGui || minecraft.screen != null)) {
			var debug = Gnetum.getElement(Constants.DEBUG_OVERLAY);
			if (Minecraft.getInstance().level == null || Minecraft.getInstance().screen != null || !Gnetum.config.isEnabled() || debug.isUncached()) {
				original.call(gui, guiGraphics);
				return;
			}
			if (debug.shouldRender()) {
				debug.begin();

				StatefulHudHandler.alternativeGuiRenderState.reset();
				var guiRenderer = (GuiRendererAccessor) ((GameRendererAccessor) Minecraft.getInstance().gameRenderer).getGuiRenderer();

				var originalState = guiRenderer.getRenderState();
				guiRenderer.setRenderState(StatefulHudHandler.alternativeGuiRenderState);

				Gnetum.framebuffers().bind();

				original.call(gui, StatefulHudHandler.alternativeGuiGraphics);
				VersionCompatUtil.flush(StatefulHudHandler.alternativeGuiGraphics);

				Gnetum.framebuffers().unbind();

				guiRenderer.setRenderState(originalState);

				debug.end();
			}

			if (Gnetum.framebuffers().needsCatchUp()) {
				original.call(gui, guiGraphics);
			}
		}
		else {
			original.call(gui, guiGraphics);
		}
    }
	//? }
}
