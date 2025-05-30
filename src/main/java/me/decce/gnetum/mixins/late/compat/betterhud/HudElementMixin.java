package me.decce.gnetum.mixins.late.compat.betterhud;

import jobicade.betterhud.element.HudElement;
import me.decce.gnetum.GnetumConfig;
import me.decce.gnetum.Passes;
import me.decce.gnetum.compat.betterhud.BetterHudCompat;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = HudElement.class, remap = false)
public class HudElementMixin {
    @Inject(method = "renderAll", at = @At("HEAD"), cancellable = true)
    private static void gnetum$renderAll(Event event, CallbackInfo ci) {
        if (!GnetumConfig.isEnabled()) return;
        ci.cancel();
        for (HudElement element : HudElement.SORTER.getSortedData(HudElement.SortType.PRIORITY)) {
            if (BetterHudCompat.element2PassMap.containsKey(element)) {
                if (BetterHudCompat.element2PassMap.getInt(element) != Passes.current) continue;
            }
            element.tryRender(event);
        }
    }
}
