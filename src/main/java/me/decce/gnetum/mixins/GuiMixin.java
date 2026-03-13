package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


//? >=1.21.10 {
import me.decce.gnetum.versioned.StatefulHudHandler;
//?} else {
/*import me.decce.gnetum.versioned.HudHandler;
import static me.decce.gnetum.versioned.HudHandler.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
*///?}

@Mixin(value = Gui.class, priority = 5000)
public class GuiMixin {
	//? >=1.21.10 {
	@WrapMethod(method = "render")
	private void gnetum$wrapGuiRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!Gnetum.config.isEnabled()) {
			original.call(guiGraphics, deltaTracker);
			return;
		}

		if (Gnetum.pass == 0) {
			VersionCompatUtil.profilerPush("sleep");
		}
		else {
			VersionCompatUtil.profilerPush("pass" + Gnetum.pass);
			Gnetum.framebuffers().resize();
			Gnetum.framebuffers().bind();
		}

		Gnetum.rendering = true;

		original.call(guiGraphics, deltaTracker);

		if (Gnetum.pass > 0) {
			VersionCompatUtil.flush(guiGraphics);
			Gnetum.framebuffers().unbind();
		}

		Gnetum.rendering = false;

		Gnetum.nextPass();

		if (Gnetum.framebuffers().needsCatchUp()) {
			StatefulHudHandler.dropDeferredSubmission();
			original.call(guiGraphics, deltaTracker);
		}
		else {
			VersionCompatUtil.profilerPopPush("uncached");
			StatefulHudHandler.performDeferredSubmission(guiGraphics);
			VersionCompatUtil.flush(guiGraphics);
			Gnetum.framebuffers().blit();
			VersionCompatUtil.profilerPop();
		}
	}

	//? } else {
	/*@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void gnetum$beforeRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (!Gnetum.config.isEnabled()) {
			return;
		}

		if (Gnetum.pass == 0) {
			VersionCompatUtil.profilerPush("sleep");
		}
		else {
			VersionCompatUtil.profilerPush("pass" + Gnetum.pass);
			Gnetum.framebuffers().resize();
			Gnetum.framebuffers().bind();
		}

		Gnetum.rendering = true;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void gnetum$afterRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Gnetum.pass > 0) {
			VersionCompatUtil.flush(guiGraphics);
			Gnetum.framebuffers().unbind();
		}

		Gnetum.rendering = false;

		Gnetum.nextPass();

		if (Gnetum.framebuffers().needsCatchUp()) {
			//TODO
//			StatefulHudHandler.dropDeferredSubmission();
//			original.call(guiGraphics, deltaTracker);
		}
		else {
			VersionCompatUtil.profilerPopPush("uncached");
			// StatefulHudHandler.performDeferredSubmission(guiGraphics);
			// VersionCompatUtil.flush(guiGraphics);
			VersionCompatUtil.profilerPop();
			Gnetum.framebuffers().blit();
		}
	}

	@WrapMethod(method = "renderChat")
	private void gnetum$wrapRenderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		gnetum$wrapElementRendering(CHAT, guiGraphics, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderHotbarAndDecorations")
	private void gnetum$wrapRenderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		gnetum$wrapElementRendering(HOTBAR, guiGraphics, () -> original.call(guiGraphics, deltaTracker));
	}

	@Unique
	private static void gnetum$wrapElementRendering(Identifier location, GuiGraphics guiGraphics, Runnable runnable) {
		var element = Gnetum.getElement(location);
		if (element.shouldRender()) {
			element.begin();
			runnable.run();
			element.end();
		}
	}
	*///? }


}
