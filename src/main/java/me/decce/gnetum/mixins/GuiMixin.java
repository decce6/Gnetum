package me.decce.gnetum.mixins;

//? >=26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import me.decce.gnetum.versioned.StatefulHudHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final
    private Minecraft minecraft;

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractDebugOverlay(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void gnetum$wrapRenderDebugOverlay(Hud hud, GuiGraphics guiGraphics, Operation<Void> original) {
        if (this.minecraft.isGameLoadFinished() && (!this.minecraft.gui.hud.isHidden() || this.minecraft.gui.screen() != null)) {
            var debug = Gnetum.getElement(Constants.DEBUG_OVERLAY);
            if (minecraft.level == null || minecraft.gui.screen() != null || !Gnetum.config.isEnabled() || debug.isUncached()) {
                original.call(hud, guiGraphics);
                return;
            }
            if (debug.shouldRender()) {
                debug.begin();

                StatefulHudHandler.alternativeGuiRenderState.reset();
                var guiRenderer = (GuiRendererAccessor) ((GameRendererAccessor) Minecraft.getInstance().gameRenderer).getGuiRenderer();

                var originalState = guiRenderer.getRenderState();
                guiRenderer.setRenderState(StatefulHudHandler.alternativeGuiRenderState);

                Gnetum.framebuffers().bind();

                original.call(hud, StatefulHudHandler.alternativeGuiGraphics);
                VersionCompatUtil.flush(StatefulHudHandler.alternativeGuiGraphics);

                Gnetum.framebuffers().unbind();

                guiRenderer.setRenderState(originalState);

                debug.end();
            }

            if (Gnetum.framebuffers().needsCatchUp()) {
                original.call(hud, guiGraphics);
            }
        }
        else {
            original.call(hud, guiGraphics);
        }
    }
}
*///? } else {
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {})
public class GuiMixin { }
//? }