package me.decce.gnetum.mixins.fabric;

//? fabric {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.decce.gnetum.Gnetum;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("deprecation")
@Mixin(HudRenderCallback.class)
public interface HudRenderCallbackMixin {
	@WrapOperation(method = "lambda$static$0", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/rendering/v1/HudRenderCallback;onHudRender(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private static void gnetum$render(HudRenderCallback callback, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!Gnetum.config.isEnabled() || !Gnetum.rendering) {
			original.call(callback, guiGraphics, deltaTracker);
			return;
		}
		var modid = Gnetum.platform().getModId(callback.getClass());
		if (Gnetum.shouldRender(modid)) {
			Gnetum.beginElement(modid);
			original.call(callback, guiGraphics, deltaTracker);
			Gnetum.endElement(modid);
		}
	}
}
//?}
