//package me.decce.gnetum.mixins.compat.guiclock;
//
//import com.natamus.guiclock.forge.events.ForgeGUIEvent;
//import net.minecraftforge.client.event.RenderGameOverlayEvent;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(value = ForgeGUIEvent.class, remap = false)
//public class ForgeGUIEventMixin {
//    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true, require = 0, expect = 0)
//    private void gnetum$checkOverlayType(RenderGameOverlayEvent.Post e, CallbackInfo ci) {
//        if (e.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
//            ci.cancel();
//        }
//    }
//}
