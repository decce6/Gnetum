package me.decce.gnetum.mixins.compat.xaerominimap;

import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "xaero.common.events.ClientEvents", remap = false)
public class ClientEventsMixin {
    //? if xaerominimap {
    @Inject(method = "handleRenderGameOverlayEventPre", at = @At("HEAD"), cancellable = true)
    private static void gnetum$beforeIngameGuiRender(CallbackInfo ci) {
        if (!XaeroMinimapCompat.error && !XaeroMinimapCompat.shouldRenderWaypoint) {
            ci.cancel();
        }
    }
    //? }
}
