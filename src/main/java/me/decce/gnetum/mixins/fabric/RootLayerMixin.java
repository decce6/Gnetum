package me.decce.gnetum.mixins.fabric;

//? fabric && >=1.21.10 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.hud.HudLayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(HudElementRegistryImpl.RootLayer.class)
@Mixin(value = HudElementRegistryImpl.RootLayer.class, remap = false)
public class RootLayerMixin {
	@Shadow
	@Final
	private Identifier id;
	@Shadow
	@Final
	private List<HudLayer> layers;
	@Unique
	private static HudElementRegistryImpl.RootLayer gnetum$getFirst() {
		return HudElementRegistryImpl.getRoot(VanillaHudElements.MISC_OVERLAYS);
	}
	@Unique
	private static HudElementRegistryImpl.RootLayer gnetum$getLast() {
		return HudElementRegistryImpl.getRoot(VanillaHudElements.SUBTITLES);
	}

	@WrapMethod(method = "render")
	public void gnetum$wrapRender(GuiGraphics context, DeltaTracker tickCounter, HudElement vanillaElement, Operation<Void> original) {
		if (!Gnetum.config.isEnabled() || !Gnetum.rendering) {
			original.call(context, tickCounter, vanillaElement);
			return;
		}
		var thisLayer = (HudElementRegistryImpl.RootLayer)(Object)this;
		if (thisLayer == gnetum$getFirst() || thisLayer == gnetum$getLast()) {
			for (HudLayer layer : thisLayer.layers()) {
				if (!layer.isRemoved()) {
					var name = VersionCompatUtil.stringValueOf(layer.id());
					var element = Gnetum.getElement(name);
					if (!element.shouldRender()) {
						continue;
					}
					Gnetum.beginElement(name);
					layer.element(vanillaElement).render(context, tickCounter);
					Gnetum.endElement(name);
				}
			}
		}
		else {
			var name = VersionCompatUtil.stringValueOf(id);
			var element = Gnetum.getElement(name);
			if (!element.shouldRender()) {
				return;
			}
			Gnetum.beginElement(name);
			original.call(context, tickCounter, vanillaElement);
			Gnetum.endElement(name);
		}
	}
}
//?}
