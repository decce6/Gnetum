package me.decce.gnetum.mixins.compat.xaerominimap;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
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
    //? }
}
