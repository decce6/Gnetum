package me.decce.gnetum.mixins;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
//? <1.21.10{
/*import com.mojang.blaze3d.platform.GlConst;
import me.decce.gnetum.FramebufferTracker;
import me.decce.gnetum.Gnetum;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {
    //? <=1.21.1 {
    /*@Inject(method = "_glBindFramebuffer", at = @At("TAIL"))
    private static void gnetum$bindFramebuffer(int target, int framebuffer, CallbackInfo ci) {
        if (target == GlConst.GL_FRAMEBUFFER) {
            FramebufferTracker.setCurrentlyBoundFbo(framebuffer);
        }
    }

    @Inject(method = "_blendFunc(II)V", at = @At("HEAD"), cancellable = true)
    private static void gnetum$blendFunc(int srcFactor, int dstFactor, CallbackInfo ci) {
        if (Gnetum.rendering) {
            GlStateManager._blendFuncSeparate(srcFactor, dstFactor, GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
        }
    }

    @Inject(method = "_blendFuncSeparate(IIII)V", at = @At("HEAD"), cancellable = true)
    private static void gnetum$blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        if (!Gnetum.rendering) return;
        if (srcFactorAlpha != GlConst.GL_ONE || dstFactorAlpha != GlConst.GL_ONE_MINUS_SRC_ALPHA) {
            GlStateManager._blendFuncSeparate(srcFactor, dstFactor, GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
            return;
        }
        if (FramebufferTracker.getCurrentlyBoundFbo() == Gnetum.framebuffers().back().frameBufferId && gnetum$isBlendFuncDangerous(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha)) {
            Gnetum.disableCachingForCurrentElement(String.format("Blending Function (%d, %d, %d, %d)", srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha));
        }
    }

    @Unique
    private static boolean gnetum$isBlendFuncDangerous(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        if (srcFactor == GlConst.GL_ONE_MINUS_DST_COLOR && dstFactor == GlConst.GL_ZERO) return false; // Do not disable cache for Xaero's Minimap
        if (gnetum$isUsingDestColor(srcFactor) || gnetum$isUsingDestColor(dstFactor)) return true;
        if (dstFactor == GlConst.GL_SRC_COLOR || dstFactor == GlConst.GL_ONE_MINUS_SRC_COLOR) return true;
        if (srcFactor == GlConst.GL_ONE && dstFactor == GlConst.GL_ONE) return true;
        return false;
    }

    @Unique
    private static boolean gnetum$isUsingDestColor(int factor) {
        return factor == GlConst.GL_DST_COLOR || factor == GlConst.GL_ONE_MINUS_DST_COLOR;
    }
    *///? }

}
