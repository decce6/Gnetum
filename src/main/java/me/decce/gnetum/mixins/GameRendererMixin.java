package me.decce.gnetum.mixins;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.gl.FramebufferTracker;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private int gnetum$previouslyBoundFbo;
    @Unique
    private boolean gnetum$renderingCachedHand;

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void gnetum$preRenderItemInHand(PoseStack p_109121_, Camera p_109122_, float p_109123_, CallbackInfo ci) {
        if (Gnetum.passManager.cachingDisabled(Gnetum.HAND_ELEMENT)) return;
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
    private void gnetum$postRenderItemInHand(PoseStack p_109121_, Camera p_109122_, float p_109123_, CallbackInfo ci) {
        if (this.gnetum$renderingCachedHand) {
            this.gnetum$renderingCachedHand = false;
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, gnetum$previouslyBoundFbo);
            Gnetum.passManager.end();
        }
    }
}