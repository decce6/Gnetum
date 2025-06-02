package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DeltaTracker.Timer.class)
public class DeltaTrackerMixin {
    @ModifyReturnValue(method = "getGameTimeDeltaTicks", at = @At("RETURN"))
    public float gnetum$getGameTimeDeltaTicks(float original) {
        if (!Gnetum.rendering) return original;
        return HudDeltaTracker.getGameTimeDeltaTicks();
    }

    @ModifyReturnValue(method = "getGameTimeDeltaPartialTick", at = @At("RETURN"))
    public float gnetum$getGameTimeDeltaPartialTick(float original) {
        if (!Gnetum.rendering || original == 1.0F) return original;
        return HudDeltaTracker.getGameTimeDeltaPartialTick();
    }

    @ModifyReturnValue(method = "getRealtimeDeltaTicks", at = @At("RETURN"))
    public float gnetum$getRealtimeDeltaTicks(float original) {
        if (!Gnetum.rendering) return original;
        return HudDeltaTracker.getRealtimeDeltaTicks();
    }
}