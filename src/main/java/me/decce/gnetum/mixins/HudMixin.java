package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

//? >=26.2 {
/*import net.minecraft.client.gui.Hud;
*///? } else {
import net.minecraft.client.gui.Gui;
//? }
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
/*import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
*///? }

//? >=26.2 {
/*@Mixin(value = Hud.class, priority = 5000)
*///? } else {
@Mixin(value = Gui.class, priority = 5000)
//? }
public class HudMixin {
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

		//? >=26.2 {
		/*Gnetum.checkForPoseCatchUp(guiGraphics);
		*///? }

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
		/*VersionCompatUtil.profilerPopPush("uncached");
		XaeroMinimapCompat.tryRenderWaypoint(guiGraphics, deltaTracker);
		*///? }
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
	//? }
}
