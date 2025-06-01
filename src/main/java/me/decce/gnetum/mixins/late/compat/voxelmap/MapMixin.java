package me.decce.gnetum.mixins.late.compat.voxelmap;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mamiyaotaru.voxelmap.Map;
import me.decce.gnetum.Gnetum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = Map.class, remap = false)
public class MapMixin {
    // For some reason VoxelMap clears the alpha bit of the color buffer before rendering their minimap, which caused HUDs to disappear.
    @WrapWithCondition(method = "renderMap", at = @At(value = "INVOKE", target = "Lcom/mamiyaotaru/voxelmap/util/GLShim;glClear(I)V", ordinal = 0))
    public boolean gnetum$dontClearAlphaBits(int mask) {
        boolean cancel = Gnetum.rendering;
        return !cancel;
    }
}
