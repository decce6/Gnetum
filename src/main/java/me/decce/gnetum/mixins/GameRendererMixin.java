package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.decce.gnetum.ElementType;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GuiHelper;
import me.decce.gnetum.HudDeltaTracker;
import me.decce.gnetum.compat.immediatelyfast.ImmediatelyFastCompat;
import me.decce.gnetum.compat.journeymap.JourneyMapCompat;
import me.decce.gnetum.gl.FramebufferTracker;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 1500) // priority needs to be higher than ImmediatelyFast
public class GameRendererMixin {
    @Unique
    private int gnetum$previouslyBoundFbo;
    @Unique
    private boolean gnetum$renderingCachedHand;

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void gnetum$preRenderItemInHand(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (!Gnetum.config.isEnabled() || Gnetum.passManager.cachingDisabled(Gnetum.HAND_ELEMENT)) return;
        if (Gnetum.passManager.shouldRender(Gnetum.HAND_ELEMENT)) {
            gnetum$previouslyBoundFbo = FramebufferTracker.getCurrentlyBoundFbo();
            Gnetum.passManager.begin();
            FramebufferManager.getInstance().bind();
            this.gnetum$renderingCachedHand = true;
        }
        else {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemInHand", at = @At("RETURN"))
    private void gnetum$postRenderItemInHand(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (this.gnetum$renderingCachedHand) {
            this.gnetum$renderingCachedHand = false;
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, gnetum$previouslyBoundFbo);
            Gnetum.passManager.end();
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void gnetum$wrapGuiRender(Gui instance, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (!Gnetum.config.isEnabled() || Minecraft.getInstance().options.hideGui) {
            original.call(instance, guiGraphics, deltaTracker);
            return;
        }

        FramebufferManager.getInstance().ensureSize();

        boolean fboCompleteBeforeRendering = FramebufferManager.getInstance().isComplete();

        if (fboCompleteBeforeRendering) {
            Minecraft.getInstance().getProfiler().push("uncached");
            ImmediatelyFastCompat.batchIfInstalled(guiGraphics, () -> {
                JourneyMapCompat.invokeRenderWaypointDecos(guiGraphics);
                GuiHelper.postEvent(new RenderGuiEvent.Pre(guiGraphics, deltaTracker), modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.PRE));
                GuiHelper.renderLayers(GuiHelper.getGuiLayerManagerAccessor().getLayers(), guiGraphics, deltaTracker, overlay -> Gnetum.passManager.cachingDisabled(overlay));
            });
            Minecraft.getInstance().getProfiler().pop();
        }

        Gnetum.passManager.begin();
        if (deltaTracker instanceof DeltaTracker.Timer timer) {
            HudDeltaTracker.update(timer);
        }
        if (Gnetum.passManager.current > 0) {
            FramebufferManager.getInstance().bind();
        }
        Gnetum.rendering = true;

        original.call(instance, guiGraphics, deltaTracker);

        Gnetum.rendering = false;
        Gnetum.currentElement = null;
        Gnetum.passManager.end();

        Gnetum.passManager.nextPass();

        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        FramebufferManager.getInstance().unbind();

        boolean fboCompleteAfterRendering = FramebufferManager.getInstance().isComplete();

        if (fboCompleteBeforeRendering && fboCompleteAfterRendering) {
            FramebufferManager.getInstance().blit();

            Minecraft.getInstance().getProfiler().push("uncached");
            ImmediatelyFastCompat.batchIfInstalled(guiGraphics, () -> {
                GuiHelper.postEvent(new RenderGuiEvent.Post(guiGraphics, deltaTracker), modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.POST));
            });
            Minecraft.getInstance().getProfiler().pop();
        }
        else {// render the HUD to screen if framebuffer does not contain the HUD
            original.call(instance, guiGraphics, deltaTracker);
        }
    }
}