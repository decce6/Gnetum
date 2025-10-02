package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.ElementType;
import me.decce.gnetum.GuiHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class)
public class GuiMixin {
    @Shadow
    public int leftHeight;
    @Shadow
    public int rightHeight;
    @Unique
    private int gnetum$lastLeftHeight = 39;
    @Unique
    private int gnetum$lastRightHeight = 39;
    @Unique
    private int gnetum$currentLeftHeight;
    @Unique
    private int gnetum$currentRightHeight;
    @Unique
    private Matrix4f gnetum$defaultGuiPose = new Matrix4f();

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/gui/GuiLayerManager;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    public boolean gnetum$render(GuiLayerManager instance, GuiGraphics guiGraphics, DeltaTracker partialTick)
    {
        if (!Gnetum.rendering) {
            return true;
        }

        // Do not use cached HUD when transformation is used (e.g. OkZoomer mod)
        // Because uncached elements are rendered outside of here (in GameRendererMixin), transformation is not applied
        //  to them otherwise, creating inconsistencies
        var pose = guiGraphics.pose().last().pose();
        if (!pose.equals(gnetum$defaultGuiPose, 0.01F)) {
            FramebufferManager.getInstance().markIncomplete();
        }

        if (Gnetum.passManager.current == 1) {
            gnetum$currentLeftHeight = 39;
            gnetum$currentRightHeight = 39;
        }

        if (Gnetum.passManager.current > 0) {
            Gnetum.renderingCanceled = ((RenderGuiEvent.Pre)GuiHelper.postEvent(new RenderGuiEvent.Pre(guiGraphics, partialTick), modid -> Gnetum.passManager.shouldRender(modid, ElementType.PRE))).isCanceled();

            if (Gnetum.passManager.current != 1) {
                leftHeight = gnetum$currentLeftHeight;
                rightHeight = gnetum$currentRightHeight;
            }

            GuiHelper.renderLayers(GuiHelper.getGuiLayerManagerAccessor().getLayers(), guiGraphics, partialTick, rl -> Gnetum.passManager.shouldRender(rl));

            GuiHelper.postEvent(new RenderGuiEvent.Post(guiGraphics, partialTick), modid -> Gnetum.passManager.shouldRender(modid, ElementType.POST));

            gnetum$currentLeftHeight = leftHeight;
            gnetum$currentRightHeight = rightHeight;
        }

        if (Gnetum.passManager.current != Gnetum.config.numberOfPasses) {
            leftHeight = gnetum$lastLeftHeight;
            rightHeight = gnetum$lastRightHeight;
        }
        else {
            gnetum$lastLeftHeight = leftHeight;
            gnetum$lastRightHeight = rightHeight;
        }

        return false;
    }

    // Some mods inject at the tail of Gui.render to render their elements (example Sodium Extra)
    // This causes issues, because the framebuffer is still bound to ours at this time
    // We only allow these injections to run in a specific pass to solve this issue
    // TODO: We might want to improve the performance analyzer to not produce warnings when it thinks the last pass does
    //  not render elements, because some may be rendered here
    @Inject(method = "render", at = @At("TAIL"), cancellable = true, order = 100)
    private void gnetum$postRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (Gnetum.rendering && Gnetum.passManager.current != Gnetum.config.numberOfPasses) {
            ci.cancel();
        }
    }
}
