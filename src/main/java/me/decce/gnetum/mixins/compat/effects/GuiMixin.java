package me.decce.gnetum.mixins.compat.effects;

import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
//$ import_delta_tracker
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? >=26.2 {
/*import net.minecraft.client.gui.Hud;
@Mixin(Hud.class)
*///? } else {
import net.minecraft.client.gui.Gui;
@Mixin(Gui.class)
//? }
public class GuiMixin {
    //? >26 {
    /*@Inject(method = "extractEffects", at = @At("HEAD"))
    *///? } else {
    @Inject(method = "renderEffects", at = @At("HEAD"))
    //? }
    private void gnetum$renderEffects$0(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        XaeroMinimapCompat.statusEffectsShown = false;
    }

    //? >26 {
    /*@Inject(method = "extractEffects", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0))
    *///? } else {
    @Inject(method = "renderEffects", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0))
    //? }
    private void gnetum$renderEffects$1(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        XaeroMinimapCompat.statusEffectsShown = true;
    }
}
