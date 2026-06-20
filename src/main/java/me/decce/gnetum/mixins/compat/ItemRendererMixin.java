package me.decce.gnetum.mixins.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemRenderer.class, priority = 2000)
public class ItemRendererMixin {
    // Some mods are missing the alpha channel in the color of their blocks. This means when they are rendered, the
    // relevant pixels will have the correct color but alpha value zero. In vanilla, this isn't visible, because
    // the main framebuffer is blitted with blending disabled. With Gnetum, this needs to be corrected, as otherwise
    // these blocks would appear semi-transparent with our blending function.
    // See: https://github.com/BlakeBr0/MysticalAgriculture/blob/b38533ab79443a5d1622d96c1917e50cc814b700/src/main/java/com/blakebr0/mysticalagriculture/client/handler/ColorHandler.java#L15-L54
    // See: https://github.com/vadis365/Mob-Grinding-Utils/blob/e7e4a15d377899d7f988d0d33bbd0d7e9c2f897e/MobGrindingUtils/MobGrindingUtils/src/main/java/mob_grinding_utils/ModColourManager.java#L26-L56
    // Fixes https://github.com/decce6/Gnetum/issues/92
    @ModifyExpressionValue(method = "renderQuadList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/item/ItemColors;getColor(Lnet/minecraft/world/item/ItemStack;I)I"))
    private int gnetum$getColor$fixAlpha(int original) {
        if (Gnetum.rendering) {
            return original | 0xFF000000;
        }
        else {
            return original;
        }
    }

    // https://github.com/FiniteReality/embeddium/blob/dee0ebde6aea8b158613cd3b8ea5cdf0c1f6a9bf/src/main/java/me/jellysquid/mods/sodium/mixin/features/render/model/item/ItemRendererMixin.java#L37-L99
    @SuppressWarnings("MixinAnnotationTarget")
    @ModifyExpressionValue(method = "renderBakedItemQuads", remap = false, require = 0, expect = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/item/ItemColors;getColor(Lnet/minecraft/world/item/ItemStack;I)I"))
    private int gnetum$getColor$fixAlpha$sodium(int original) {
        if (Gnetum.rendering) {
            return original | 0xFF000000;
        }
        else {
            return original;
        }
    }
}
