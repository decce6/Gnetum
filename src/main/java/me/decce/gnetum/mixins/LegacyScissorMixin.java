package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? <1.21.10 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///? }

//? <1.21.10 {
/*@Mixin(GuiGraphics.class)
public class LegacyScissorMixin {
	@Inject(method = "applyScissor", at = @At("HEAD"), cancellable = true)
	private void gnetum$scissorInGuiSpace(ScreenRectangle rectangle, CallbackInfo ci) {
		if (rectangle == null || !Gnetum.rendering || !Gnetum.config.downscale.get()) {
			return;
		}
		((GuiGraphics) (Object) this).flush();
		var window = Minecraft.getInstance().getWindow();
		int width = window.getGuiScaledWidth();
		int height = window.getGuiScaledHeight();
		int left = Math.max(0, rectangle.left());
		int top = Math.max(0, rectangle.top());
		int right = Math.min(rectangle.right(), width);
		int bottom = Math.min(rectangle.bottom(), height);
		RenderSystem.enableScissor(left, height - bottom, Math.max(0, right - left), Math.max(0, bottom - top));
		ci.cancel();
	}
}
*///? } else {
@Mixin(targets = {})
public class LegacyScissorMixin {}
//? }
