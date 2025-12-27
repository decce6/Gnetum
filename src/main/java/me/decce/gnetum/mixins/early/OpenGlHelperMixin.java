package me.decce.gnetum.mixins.early;

import me.decce.gnetum.ElementType;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.gl.FramebufferTracker;
import me.decce.gnetum.hud.VanillaHuds;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public class OpenGlHelperMixin {
    @Inject(method = "glBindFramebuffer", at = @At("TAIL"))
    private static void glBindFramebuffer(int target, int framebufferIn, CallbackInfo ci) {
        if (target != GL30.GL_FRAMEBUFFER) {
            return;
        }
        FramebufferTracker.setCurrentlyBoundFbo(framebufferIn);
    }

    @Inject(method = "glBlendFunc", at = @At("HEAD"), cancellable = true)
    private static void gnetum$blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        if (!Gnetum.rendering) return;
        if (srcFactorAlpha != GL11.GL_ONE || dstFactorAlpha != GL11.GL_ONE_MINUS_SRC_ALPHA) {
            GlStateManager.tryBlendFuncSeparate(srcFactor, dstFactor, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
            return;
        }
        if (FramebufferTracker.getCurrentlyBoundFbo() == FramebufferManager.getInstance().id() && gnetum$isBlendFuncDangerous(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha)) {
            // Do not disable caching for Xaero's Minimap
            if (Gnetum.currentElementType != ElementType.VANILLA || !VanillaHuds.HELMET.id().toString().equals(Gnetum.currentElement)) {
                Gnetum.disableCachingForCurrentElement(String.format("Blending Function (%d, %d, %d, %d)", srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha));
            }
        }
    }

    @Unique
    private static boolean gnetum$isBlendFuncDangerous(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        if (gnetum$isUsingDestColor(srcFactor) || gnetum$isUsingDestColor(dstFactor)) return true;
        if (dstFactor == GL11.GL_SRC_COLOR || dstFactor == GL11.GL_ONE_MINUS_SRC_COLOR) return true;
        if (srcFactor == GL11.GL_ONE && dstFactor == GL11.GL_ONE) return true;
        return false;
    }

    @Unique
    private static boolean gnetum$isUsingDestColor(int factor) {
        return factor == GL11.GL_DST_COLOR || factor == GL11.GL_ONE_MINUS_DST_COLOR;
    }
}
