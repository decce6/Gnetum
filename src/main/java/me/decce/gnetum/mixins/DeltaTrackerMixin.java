package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

// Note: 1.20.4- in MinecraftMixin
//? >=1.21.1 {
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.HudDeltaTracker;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DeltaTracker.Timer.class)
public class DeltaTrackerMixin {
    @ModifyReturnValue(method = "getRealtimeDeltaTicks", at = @At("RETURN"))
    public float gnetum$getRealtimeDeltaTicks(float original) {
        if (!Gnetum.rendering || !HudDeltaTracker.isReady()) return original;
        return HudDeltaTracker.getRealtimeDeltaTicks();
    }

    @ModifyReturnValue(method = "getGameTimeDeltaTicks", at = @At("RETURN"))
    public float gnetum$getGameTimeDeltaTicks(float original) {
        if (!Gnetum.rendering || !HudDeltaTracker.isReady()) return original;
        return HudDeltaTracker.getGameTimeDeltaTicks();
    }
}
//? } else {
/*@Mixin(targets = {})
public class DeltaTrackerMixin {}
*///? }