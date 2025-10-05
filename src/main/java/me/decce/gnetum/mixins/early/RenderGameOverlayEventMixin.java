package me.decce.gnetum.mixins.early;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RenderGameOverlayEvent.class, remap = false)
public class RenderGameOverlayEventMixin {
    @ModifyReturnValue(method = "getPartialTicks", at = @At("RETURN"))
    private float gnetum$getPartialTicks(float original) {
        if (Gnetum.rendering) {
            return HudDeltaTracker.getPartialTick();
        }
        return original;
    }
}
