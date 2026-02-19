package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.MappableRingBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MappableRingBuffer.class)
public class MappableRingBufferMixin {
    // We require additional buffer flushing before switching framebuffers. Thus, we double the buffer count to avoid
    // pipeline stall.
    @ModifyExpressionValue(method = { "<init>", "rotate", "close" } , at = @At(value = "CONSTANT", args = "intValue=3"))
    private int gnetum$increaseBufferSize(int original) {
        return original * 2;
    }
}
