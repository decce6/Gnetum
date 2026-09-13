package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.10 {
import com.mojang.blaze3d.systems.RenderPass;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? }

//? >=1.21.10 {
@Mixin(GuiRenderer.class)
public class GuiRendererScissorMixin {
	@Inject(method = "enableScissor", at = @At("HEAD"), cancellable = true)
	private void gnetum$scissorInGuiSpace(ScreenRectangle rectangle, RenderPass renderPass, CallbackInfo ci) {
		if (!Gnetum.flushing || !Gnetum.config.downscale.get()) {
			return;
		}
		var window = Minecraft.getInstance().getWindow();
		int width = window.getGuiScaledWidth();
		int height = window.getGuiScaledHeight();
		int left = Math.max(0, rectangle.left());
		int top = Math.max(0, rectangle.top());
		int right = Math.min(rectangle.right(), width);
		int bottom = Math.min(rectangle.bottom(), height);
		renderPass.enableScissor(left, height - bottom, Math.max(0, right - left), Math.max(0, bottom - top));
		ci.cancel();
	}
}
//? } else {
/*@Mixin(targets = {})
public class GuiRendererScissorMixin {}
*///? }
