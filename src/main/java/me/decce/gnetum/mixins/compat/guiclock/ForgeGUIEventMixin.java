package me.decce.gnetum.mixins.compat.guiclock;

import com.natamus.guiclock.forge.events.ForgeGUIEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeGUIEvent.class, remap = false)
public class ForgeGUIEventMixin {
    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void gnetum$checkOverlayType(RenderGuiOverlayEvent.Post e, CallbackInfo ci) {
        if (VanillaGuiOverlay.TITLE_TEXT.type() != e.getOverlay()) {
            ci.cancel();
        }
    }
}
