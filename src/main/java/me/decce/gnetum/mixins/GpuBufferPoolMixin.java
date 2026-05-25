package me.decce.gnetum.mixins;

//? >=26.2 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.decce.gnetum.Gnetum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.StagedVertexBuffer$GpuBufferPool")
public class GpuBufferPoolMixin {
    @ModifyExpressionValue(method = "endFrame", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean gnetum$doNotRecycleBufferWhenFlushing(boolean original) {
        return original || Gnetum.flushing;
    }
}
*///? } else {
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {})
public class GpuBufferPoolMixin {}
//? }
