//Courtesy of Moulberry
//Adapted from https://github.com/Moulberry/MCHUDCaching/blob/33750bed95cfbbf6e63dca90bdb9c9d767a218e2/src/main/java/io/github/moulberry/hudcaching/mixins/MixinGlStateManager.java
//under the CC BY 3.0 license (https://github.com/Moulberry/MCHUDCaching/blob/33750bed95cfbbf6e63dca90bdb9c9d767a218e2/LICENSE)

package me.decce.gnetum.mixins;

import me.decce.gnetum.Gnetum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class GlStateManagerMixin {
    @Unique
    private static boolean gnetum$blendEnabled;

    @Inject(method = "blendFunc(II)V", at = @At("HEAD"), cancellable = true)
    private static void gnetum$blendFunc(int srcFactor, int dstFactor, CallbackInfo ci) {
        if (Gnetum.rendering) {
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
        }
    }

    @Inject(method = "tryBlendFuncSeparate(IIII)V", at = @At("HEAD"), cancellable = true)
    private static void gnetum$tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        if (Gnetum.rendering && dstFactorAlpha != GL11.GL_ONE_MINUS_SRC_ALPHA) {
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
        }
    }

    @Inject(method = "disableBlend", at = @At("HEAD"))
    private static void gnetum$disableBlend(CallbackInfo ci) {
        if (Gnetum.rendering) {
            gnetum$blendEnabled = false;
        }
    }

    @Inject(method = "enableBlend", at = @At("HEAD"))
    private static void gnetum$enableBlend(CallbackInfo ci) {
        if (Gnetum.rendering) {
            gnetum$blendEnabled = true;
        }
    }

    @Inject(method = "color(FFFF)V", at = @At("HEAD"), cancellable = true)
    private static void gnetum$color(float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (!gnetum$blendEnabled && Gnetum.rendering && alpha < 1f) {
            GlStateManager.color(red, green, blue, 1f);
            ci.cancel();
        }
    }
}
