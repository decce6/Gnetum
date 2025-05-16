package me.decce.gnetum.mixins;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Inject(method = "getGameInformation", at = @At("RETURN"))
    private void gnetum$getGameInformation(CallbackInfoReturnable<List<String>> cir) {
        if (!Gnetum.config.isEnabled() || !Gnetum.config.showHudFps.get()) return;
        cir.getReturnValue().add(2, String.format("HUD: %d fps (nr=%d, cap=%s)", Gnetum.FPS_COUNTER.getFps(), Gnetum.config.numberOfPasses, Gnetum.config.maxFps == GnetumConfig.UNLIMITED_FPS ? "unlimited" : Gnetum.config.maxFps));
    }
}