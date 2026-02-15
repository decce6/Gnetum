package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.10 {
import net.minecraft.client.renderer.fog.FogRenderer;
import me.decce.gnetum.versioned.StatefulHudHandler;
//?}

@Mixin(value = Gui.class, priority = 5000)
public class GuiMixin {
	@WrapMethod(method = "render")
	private void gnetum$wrapGuiRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!Gnetum.config.isEnabled()) {
			original.call(guiGraphics, deltaTracker);
			return;
		}

		//TODO: optimize string alloc & hash (cache concat result)
		var mc = Minecraft.getInstance();
		var game = (GameRendererAccessor) mc.gameRenderer;

		Gnetum.framebuffers().resize();
		Gnetum.framebuffers().bind();

		if (Gnetum.pass == 0) {
			VersionCompatUtil.profilerPush("sleep");
		}
		else {
			VersionCompatUtil.profilerPush("pass" + Gnetum.pass);
		}

		Gnetum.rendering = true;

		original.call(guiGraphics, deltaTracker);

		if (Gnetum.pass > 0) {
			//? >=1.21.10 {
			game.getGuiRenderer().render(game.getFogRenderer().getBuffer(FogRenderer.FogMode.NONE));
			//?} else {
			/*guiGraphics.flush();
			*///?}
		}

		Gnetum.rendering = false;
		Gnetum.nextPass();

		Gnetum.framebuffers().unbind();

		VersionCompatUtil.profilerPopPush("uncached");
		// Note: these are not rendered instantly, but batched with the rest of the GUI - profiler data may be not useful

		//? >=1.21.10 {
		if (Gnetum.catchingUp) {
			StatefulHudHandler.dropDeferredSubmission();
		}
		else {
			StatefulHudHandler.performDeferredSubmission(guiGraphics);
			Gnetum.framebuffers().blit();
		}
		//?} else {
		//?}

		VersionCompatUtil.profilerPop();
	}
}
