package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.10 {
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

		//? >=1.21.10 {
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
		//?} else {
		//?}

	}
}
