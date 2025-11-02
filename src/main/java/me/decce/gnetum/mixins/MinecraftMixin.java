package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @ModifyReturnValue(method = "getDeltaFrameTime", at = @At("RETURN"))
    public float getDeltaFrameTime(float original) {
        if (!Gnetum.rendering || !HudDeltaTracker.isReady()) return original;
        return HudDeltaTracker.getTickDelta();
    }
}
