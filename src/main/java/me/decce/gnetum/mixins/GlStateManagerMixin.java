package me.decce.gnetum.mixins;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import me.decce.gnetum.ElementType;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.gl.FramebufferTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class GlStateManagerMixin {
    @Inject(method = "_glBindFramebuffer", at = @At("HEAD"), cancellable = true)
    private static void gnetum$bindFramebuffer(int p_84487_, int p_84488_, CallbackInfo ci) {
        if (p_84487_ == GlConst.GL_FRAMEBUFFER) {
            FramebufferTracker.setCurrentlyBoundFbo(p_84488_);
        }
        if (Gnetum.rendering && p_84488_ == Minecraft.getInstance().getMainRenderTarget().frameBufferId) {
            ci.cancel();
            FramebufferManager.getInstance().bind();
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
        if (FramebufferTracker.getCurrentlyBoundFbo() == FramebufferManager.getInstance().id() && gnetum$isBlendFuncDangerous(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha)) {
            // TODO: the problem with this detection mechanism is that, if the destination (i.e. our backFramebuffer) is
            //  already filled with pixels then the DST_COLOR factors WILL work properly, and there is no need to
            //  disable caching.
            //  I'll just special-case Xaero's Minimap until I come up with a better solution.
            if (Gnetum.currentElementType != ElementType.VANILLA || !"xaerohud:hud".equals(Gnetum.currentElement)) {
                Gnetum.disableCachingForCurrentElement();
            }
        }
    }

    @Unique
    private static boolean gnetum$isBlendFuncDangerous(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        if (gnetum$isUsingDestColor(srcFactor) || gnetum$isUsingDestColor(dstFactor)) return true;
        if (dstFactor == GlConst.GL_SRC_COLOR || dstFactor == GlConst.GL_ONE_MINUS_SRC_COLOR || dstFactor == GlConst.GL_ONE) return true;
        return false;
    }

    @Unique
    private static boolean gnetum$isUsingDestColor(int factor) {
        return factor == GlConst.GL_DST_COLOR || factor == GlConst.GL_ONE_MINUS_DST_COLOR;
    }
}