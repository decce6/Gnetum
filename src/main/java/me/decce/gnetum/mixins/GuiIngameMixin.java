package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void gnetum$renderPumpkinOverlay(ScaledResolution scaledRes, CallbackInfo ci){
        if (Gnetum.rendering) {
            ci.cancel();
        }
    }

    // Inspired by Angelica: https://github.com/GTNewHorizons/Angelica/pull/232
    @ModifyExpressionValue(method = "renderScoreboard", at = @At(value = "CONSTANT", args = "intValue=553648127"))
    private int gnetum$correctScoreboardTextColor(int original) {
        return GnetumConfig.isEnabled() ? original | 0xFF000000 : original;
    }
}
