package me.decce.gnetum.mixins;

//? <1.21.10 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.versioned.HudHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LayeredDraw.class)
public class LayeredDrawMixin {
	@WrapMethod(method = "render")
	private void gnetum$render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!Gnetum.rendering) {
			original.call(guiGraphics, deltaTracker);
			return;
		}
		var element = HudHandler.VANILLA_LAYERS;
		if (Gnetum.shouldRender(element)) {
			Gnetum.beginElement(element);
			original.call(guiGraphics, deltaTracker);
			Gnetum.endElement(element);
		}
	}
}
*///?}
