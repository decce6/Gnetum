package me.decce.gnetum.mixins.fabric;

//? fabric && >=1.21.10 && <26 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.decce.gnetum.Gnetum;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("deprecation")
@Mixin(value = HudRenderCallback.class, remap = false)
public interface HudRenderCallbackMixin {
	@WrapOperation(method = "lambda$static$0", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/rendering/v1/HudRenderCallback;onHudRender(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private static void gnetum$render(HudRenderCallback callback, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!Gnetum.config.isEnabled() || !Gnetum.rendering) {
			original.call(callback, guiGraphics, deltaTracker);
			return;
		}
		var modid = Gnetum.platform().getModId(callback.getClass());
		var element = Gnetum.getElement(modid);
		if (element.shouldRender()) {
			element.begin();
			original.call(callback, guiGraphics, deltaTracker);
			element.end();
		}
	}
}
//?} else if fabric && <=1.21.1 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.decce.gnetum.Gnetum;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HudRenderCallback.class, remap = false)
public interface HudRenderCallbackMixin {
    @WrapOperation(method = "lambda$static$0", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/rendering/v1/HudRenderCallback;onHudRender(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private static void gnetum$render(HudRenderCallback callback, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (Gnetum.renderingGuiInjection) {
            return;
        }
        if (!Gnetum.config.isEnabled() || !Gnetum.rendering) {
            original.call(callback, guiGraphics, deltaTracker);
            return;
        }
        var modid = Gnetum.platform().getModId(callback.getClass());
        var element = Gnetum.getElement(modid);
        if (Gnetum.rendering) {
            if (element.shouldRenderAsCached()) {
                element.begin();
                original.call(callback, guiGraphics, deltaTracker);
                element.end();
            }
        }
        else {
            if (element.shouldRenderAsUncached()) {
                original.call(callback, guiGraphics, deltaTracker);
            }
        }
    }
}
*///?} else {

/*import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {})
public class HudRenderCallbackMixin {}
*///? }
