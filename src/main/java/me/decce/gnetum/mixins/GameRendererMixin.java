package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? >=1.21.10 {
import org.joml.Matrix3x2f;
//?} else {
/*import org.joml.Matrix4f;
*///?}

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Unique
	private boolean gnetum$wasChatScreenOpen;
	@Unique
	private boolean gnetum$renderingCachedHand;
	@Unique
	//? >=1.21.10 {
	private final Matrix3x2f gnetum$lastGuiMatrix = new Matrix3x2f();
	//?} else {
	/*private final Matrix4f gnetum$lastGuiMatrix = new Matrix4f();
	*///?}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER))
	private void gnetum$catchUpGui(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci, @Local GuiGraphics guiGraphics) {
		if (!Gnetum.config.isEnabled()) {
			return;
		}

		if (deltaTracker instanceof DeltaTracker.Timer timer) {
			HudDeltaTracker.update(timer);
		}
		else {
			HudDeltaTracker.disable();
		}

		gnetum$checkForCatchUp(guiGraphics);

		/*
		while (Gnetum.framebuffers().needsCatchUp()) {
			Gnetum.catchingUp = true;
			this.minecraft.gui.render(guiGraphics, deltaTracker);
			Gnetum.catchingUp = false;
		}
		*/

	}

	@Unique
	private void gnetum$checkForCatchUp(GuiGraphics guiGraphics) {
		boolean chatScreenOpen = minecraft.screen instanceof ChatScreen;
		if (chatScreenOpen != gnetum$wasChatScreenOpen) {
			gnetum$wasChatScreenOpen = chatScreenOpen;
			Gnetum.framebuffers().markForCatchUp();
		}

		var pose = guiGraphics.pose();
        //? >= 1.21.10 {
        if (!pose.equals(gnetum$lastGuiMatrix, 0.01F)) {
            gnetum$lastGuiMatrix.set(pose);
            Gnetum.framebuffers().markForCatchUp();
        }
        //?} else {
		/*if (!pose.last().pose().equals(gnetum$lastGuiMatrix, 0.01F)) {
			gnetum$lastGuiMatrix.set(pose.last().pose());
			Gnetum.framebuffers().markForCatchUp();
		}
        *///?}
	}

	@WrapMethod(method = "renderItemInHand")
	//? if >26 {
	private void gnetum$wrapRenderItemInHand(CameraRenderState cameraState, float deltaPartialTick, Matrix4f modelViewMatrix, Operation<Void> original) {
	//? } else {
	/*private void gnetum$wrapRenderItemInHand(float f, boolean bl, Matrix4f matrix4f, Operation<Void> original) {
	*///? }
		var hand = Gnetum.getElement(Constants.HAND_ELEMENT);
		if (!Gnetum.config.isEnabled() || hand.isUncached()) {
			//? if >26 {
			original.call(cameraState, deltaPartialTick, modelViewMatrix);
			//? } else {
			/*original.call(f, bl, matrix4f);
			*///? }
			return;
		}
		if (hand.shouldRender()) {
			// No flush needed
			Gnetum.beginElement(Constants.HAND_ELEMENT);
			Gnetum.framebuffers().bind();
			//? if >26 {
			original.call(cameraState, deltaPartialTick, modelViewMatrix);
			//? } else {
			/*original.call(f, bl, matrix4f);
			*///? }
			Gnetum.framebuffers().unbind();
			Gnetum.endElement(Constants.HAND_ELEMENT);
		}
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderDebugOverlay(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void gnetum$wrapRenderDebugOverlay(Gui gui, GuiGraphics guiGraphics, Operation<Void> original) {
		if (minecraft.isGameLoadFinished() && (!minecraft.options.hideGui || minecraft.screen != null)) {
			var debug = Gnetum.getElement(Constants.DEBUG_OVERLAY);
			if (Minecraft.getInstance().level == null || Minecraft.getInstance().screen != null || !Gnetum.config.isEnabled() || debug.isUncached()) {
				original.call(gui, guiGraphics);
				return;
			}
			if (debug.shouldRender()) {
				Gnetum.beginElement(Constants.DEBUG_OVERLAY);
				VersionCompatUtil.flush(guiGraphics);
				Gnetum.framebuffers().bind();
				original.call(gui, guiGraphics);
				VersionCompatUtil.flush(guiGraphics);
				Gnetum.framebuffers().unbind();
				Gnetum.endElement(Constants.DEBUG_OVERLAY);
			}
		}
		else {
			original.call(gui, guiGraphics);
		}
    }
}
