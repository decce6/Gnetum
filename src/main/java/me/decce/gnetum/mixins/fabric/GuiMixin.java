package me.decce.gnetum.mixins.fabric;

//? fabric && >=1.21.10 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Gui.class)
public class GuiMixin {
    // Fabric API only wraps the renderBackground and renderExperienceLevel calls - we need to wrap the render call as well
    // Fixes https://github.com/decce6/Gnetum/issues/84
    //? >=26 {
    /*@WrapOperation(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;extractRenderState(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    *///? } else {
    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    //? }
    @SuppressWarnings("UnstableApiUsage")
    private void gnetum$wrapInfoBar(ContextualBarRenderer instance, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (!Gnetum.config.isEnabled() || !Gnetum.rendering) {
            original.call(instance, guiGraphics, deltaTracker);
            return;
        }
        // Note: use EXPERIENCE_LEVEL to ensure correct rendering order (after instead of before it)
        var root = HudElementRegistryImpl.getRoot(VanillaHudElements.EXPERIENCE_LEVEL);
        var name = VersionCompatUtil.stringValueOf(root.id());
        var element = Gnetum.getElement(name);
        if (!element.shouldRender()) {
            return;
        }
        element.begin();
        original.call(instance, guiGraphics, deltaTracker);
        element.end();
    }
}
//? } else {

/*import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {})
public class GuiMixin {}
*///? }
