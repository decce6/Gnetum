package me.decce.gnetum;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.decce.gnetum.mixins.GameRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
//? >=26.2 {
/*import net.minecraft.locale.Language;
*///? } else {
import net.minecraft.client.resources.language.I18n;
import me.decce.gnetum.mixins.MinecraftAccessor;
//? }
//? >=1.21.10 {
import net.minecraft.client.renderer.fog.FogRenderer;
//? }

public class VersionCompatUtil {
	public static void profilerPush(String str) {
		//? if >1.21.1 {
		net.minecraft.util.profiling.Profiler.get().push(str);
		//?} else {
		/*net.minecraft.client.Minecraft.getInstance().getProfiler().push(str);
		 *///?}
	}

	public static void profilerPop() {
		//? if >1.21.1 {
		net.minecraft.util.profiling.Profiler.get().pop();
		//?} else {
		/*net.minecraft.client.Minecraft.getInstance().getProfiler().pop();
		 *///?}
	}

	public static void profilerPopPush(String str) {
		profilerPop();
		profilerPush(str);
	}

	public static String stringValueOf(Identifier identifier) {
		//TODO optimize string alloc & hash (cache concat result)
		return identifier.getNamespace().equals("minecraft")
				? identifier.getPath()
				: identifier.toString();
	}

	public static void flush(GuiGraphics guiGraphics) {
		//? >=26.2 {
		/*Gnetum.flushing = true;
		var game = (GameRendererAccessor) Minecraft.getInstance().gameRenderer;
		game.getGuiRenderer().render();
		Gnetum.flushing = false;
		*///?} else >=1.21.10 {
		var game = (GameRendererAccessor) Minecraft.getInstance().gameRenderer;
		game.getGuiRenderer().render(game.getFogRenderer().getBuffer(FogRenderer.FogMode.NONE));
		//?} else {
		/*guiGraphics.flush();
		 *///?}
	}

	public static boolean isHudHidden() {
		//? >=26.2 {
		/*return Minecraft.getInstance().gui.hud.isHidden();
		*///? } else {
		return Minecraft.getInstance().options.hideGui;
		//? }
	}

	public static boolean isInScreen() {
		//? >=26.2 {
		/*return Minecraft.getInstance().gui.screen() != null;
		*///? } else {
		return Minecraft.getInstance().screen != null;
		//? }
	}

	public static boolean i18nExists(String key) {
		//? >=26.2 {
		/*return Language.getInstance().has(key);
		*///? } else {
		return I18n.exists(key);
		//? }
	}

	public static void setMainRenderTarget(RenderTarget renderTarget) {
		//? >=26.2 {
		/*((GameRendererAccessor)Minecraft.getInstance().gameRenderer).gnetum$setMainRenderTarget(renderTarget);
		*///? } else {
		((MinecraftAccessor)Minecraft.getInstance()).gnetum$setMainRenderTarget(renderTarget);
		//? }
	}

	public static RenderTarget getRawMainRenderTarget() {
		//? >=26.2 {
		/*return ((GameRendererAccessor)Minecraft.getInstance().gameRenderer).gnetum$getMainRenderTarget();
		*///? } else {
		return ((MinecraftAccessor)Minecraft.getInstance()).gnetum$getMainRenderTarget();
		//? }
	}
}
