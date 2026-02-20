package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.renderer.MappableRingBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// We require additional buffer flushing before switching framebuffers. To avoid client side stall when awaiting
// fence completion on glClientWaitSync, we increase the buffer count here.
@Mixin(MappableRingBuffer.class)
public class MappableRingBufferMixin {
    @ModifyExpressionValue(method = { "<init>", "close" } , at = @At(value = "CONSTANT", args = "intValue=3"))
    private int gnetum$increaseBufferSize(int original) {
        // We must generate a size that is the largest we might need because we cannot resize afterward
        return original * 4;
    }

    @ModifyExpressionValue(method = "rotate" , at = @At(value = "CONSTANT", args = "intValue=3"))
    private int gnetum$rotate$increaseBufferSize(int original) {
        if (!Gnetum.config.isEnabled()) {
            return original;
        }
        if (!Gnetum.getElement(Constants.DEBUG_OVERLAY).isUncached()) {
            // F3 caching requires two additional flushes
            return original * 4;
        }
        return original * 3;
    }
}
