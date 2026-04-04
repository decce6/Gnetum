package me.decce.gnetum.mixins.compat.xaerominimap;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "xaero.common.core.XaeroMinimapCore", remap = false)
public class XaeroMinimapCoreMixin {
    //? if xaerominimap {
    @Inject(method = "beforeIngameGuiRender", at = @At("HEAD"))
    private static void gnetum$beforeIngameGuiRender(CallbackInfo ci) {
        //? fabric {
        if (Gnetum.config.isEnabled()) {
            XaeroMinimapCompat.update();
        }
        //? }
    }

    //? fabric {
    @WrapMethod(method = "handleRenderModOverlay")
    private static void gnetum$handleRenderModOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (!Gnetum.rendering) {
            original.call(guiGraphics, deltaTracker);
            return;
        }
        var element = Gnetum.getElement(XaeroMinimapCompat.ELEMENT);
        if (element.shouldRender()) {
            element.begin();
            original.call(guiGraphics, deltaTracker);
            element.end();
        }
    }
    //? }
    //? }
}
