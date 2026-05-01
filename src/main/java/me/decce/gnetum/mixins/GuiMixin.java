package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.decce.gnetum.CachedElement;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.10 {
import me.decce.gnetum.versioned.StatefulHudHandler;
//?} else {
/*import static me.decce.gnetum.hud.SharedValues.guiGraphics;
import me.decce.gnetum.hud.HudManager;
import me.decce.gnetum.hud.SharedValues;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;
*///?}
//? xaerominimap {
import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
//? }

@Mixin(value = Gui.class, priority = 5000)
public class GuiMixin {
	//? if >=1.21.10 {
	//? if >26 {
	/*@WrapMethod(method = "extractRenderState")
	*///? } else {
	@WrapMethod(method = "render")
	//? }
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

		//? xaerominimap {
		VersionCompatUtil.profilerPopPush("uncached");
		XaeroMinimapCompat.tryRenderWaypoint(guiGraphics, deltaTracker);
		//? }
		if (Gnetum.framebuffers().needsCatchUp()) {
			StatefulHudHandler.dropDeferredSubmission();
			original.call(guiGraphics, deltaTracker);
		}
		else {
			VersionCompatUtil.profilerPopPush("uncached");
			StatefulHudHandler.performDeferredSubmission(guiGraphics);
			Gnetum.framebuffers().blit(guiGraphics);
		}
		VersionCompatUtil.profilerPop();
	}

	//? } else {
	/*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void gnetum$render(LayeredDraw layeredDraw, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!Gnetum.config.isEnabled()) {
			original.call(layeredDraw, guiGraphics, deltaTracker);
			return;
		}

		SharedValues.guiGraphics = guiGraphics;
		SharedValues.deltaTracker = deltaTracker;

		guiGraphics.pose().pushPose();

		gnetum$renderVanillaHuds(CachedElement::shouldRenderAsUncached);

		Gnetum.framebuffers().resize();
		if (Gnetum.pass == 0) {
			VersionCompatUtil.profilerPush("sleep");
		}
		else {
			VersionCompatUtil.profilerPush("pass" + Gnetum.pass);
			VersionCompatUtil.flush(guiGraphics);
			Gnetum.framebuffers().bind();
		}

		Gnetum.rendering = true;

		gnetum$renderVanillaHuds(CachedElement::shouldRenderAsCached);


		if (Gnetum.pass > 0) {
			VersionCompatUtil.flush(guiGraphics);
			Gnetum.framebuffers().unbind();
		}
		VersionCompatUtil.profilerPop();

		Gnetum.rendering = false;

		Gnetum.nextPass();

		if (Gnetum.framebuffers().needsCatchUp()) {
			original.call(layeredDraw, guiGraphics, deltaTracker);
		}
		else {
			Gnetum.framebuffers().blit(guiGraphics);
		}

		guiGraphics.pose().popPose();

		SharedValues.guiGraphics = null;
		SharedValues.deltaTracker = null;
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
					element.end();
				}
				else {
					hud.render();
				}
				guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
			}
		}
	}
	*///? }


}
