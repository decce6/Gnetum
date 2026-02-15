package me.decce.gnetum.mixins;

//? >=1.21.10 {

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.versioned.StatefulHudHandler;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.ScreenArea;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin {
	@Unique
	private void gnetum$submit(ScreenArea state, CallbackInfo ci) {
		if (!Gnetum.rendering) {
			return;
		}
		if (Gnetum.isCurrentElementUncached()) {
			gnetum$submitForUncached(state);
			ci.cancel();
		}
	}

	@Inject(method = "submitText", at = @At("HEAD"), cancellable = true)
	private void gnetum$submitText(GuiTextRenderState state, CallbackInfo ci) {
		gnetum$submit(state, ci);
	}

	@Inject(method = "submitPicturesInPictureState", at = @At("HEAD"), cancellable = true)
	private void gnetum$submitPIP(PictureInPictureRenderState state, CallbackInfo ci) {
		gnetum$submit(state, ci);
	}

	@Inject(method = "submitItem", at = @At("HEAD"), cancellable = true)
	private void gnetum$submitItem(GuiItemRenderState state, CallbackInfo ci) {
		gnetum$submit(state, ci);
	}

	@Inject(method = "submitGuiElement", at = @At("HEAD"), cancellable = true)
	private void gnetum$submitGuiElement(GuiElementRenderState state, CallbackInfo ci) {
		if (!Gnetum.rendering) {
			return;
		}
		if (Gnetum.isCurrentElementUncached()) {
			gnetum$submitForUncached(state);
			ci.cancel();
			return;
		}
		var pipeline = state.pipeline();
		var optionalBlend = pipeline.getBlendFunction();
		if (optionalBlend.isEmpty()) {
			return;
		}
		var blend = optionalBlend.get();
		var pipelineAccessor = (RenderPipelineAccessor) pipeline;
		if (blend.sourceAlpha() != SourceFactor.ONE || blend.destAlpha() != DestFactor.ONE_MINUS_SRC_ALPHA) {
			// TODO: optimize alloc
			blend = new BlendFunction(blend.sourceColor(), blend.destColor(), SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
			pipelineAccessor.setBlendFunction(Optional.of(blend));
		}
		if (gnetum$isBlendIncompatible(blend)) {
			gnetum$submitForUncached(state);
			Gnetum.disableCachingForCurrentElement("Blend Func (%s, %s, %s, %s)".formatted(blend.sourceColor(), blend.destColor(), blend.sourceAlpha(), blend.destAlpha()));
			ci.cancel();
		}
	}

	@Unique
	private void gnetum$submitForUncached(ScreenArea state) {
		StatefulHudHandler.submitLater(state);
	}

	@Unique
	private boolean gnetum$isBlendIncompatible(BlendFunction blend) {
		if (gnetum$isUsingDestColor(blend.sourceColor()) || gnetum$isUsingDestColor(blend.destColor())) return true;
		if (blend.destColor() == DestFactor.SRC_COLOR || blend.destColor() == DestFactor.ONE_MINUS_SRC_COLOR) return true;
		if (blend.sourceColor() == SourceFactor.ONE && blend.destColor() == DestFactor.ONE) return true;
		return false;
	}

	@Unique
	private static boolean gnetum$isUsingDestColor(SourceFactor factor) {
		return factor == SourceFactor.DST_COLOR || factor == SourceFactor.ONE_MINUS_DST_COLOR;
	}

	@Unique
	private static boolean gnetum$isUsingDestColor(DestFactor factor) {
		return factor == DestFactor.DST_COLOR || factor == DestFactor.ONE_MINUS_DST_COLOR;
	}
}
//?} else {

/*import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Gui.class)
public class GuiRenderStateMixin { }
*///?}
