package me.decce.gnetum.mixins.early;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RenderGameOverlayEvent.class, remap = false)
public class RenderGameOverlayEventMixin {
//    @ModifyReturnValue(method = "getPartialTicks", at = @At("RETURN"))
//    private float gnetum$getPartialTicks(float original) {
//        var event = (RenderGameOverlayEvent)(Object)this;
//        if (event instanceof RenderGameOverlayEvent.Pre && event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
//            // Fixes https://github.com/decce6/Gnetum/issues/46
//            // Reference: https://github.com/strubium/TheOneSmeagle/blob/8a8b3e331bf26c42cb4671cbf5754de280cbf5e8/src/main/java/mcjty/theoneprobe/rendering/OverlayRenderer.java#L130-L131
//            return original;
//        }
//        if (Gnetum.rendering) {
//            return HudDeltaTracker.getPartialTick();
//        }
//        return original;
//    }
}
