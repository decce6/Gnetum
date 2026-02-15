package me.decce.gnetum.mixins;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >=1.21.10 {
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
//?}

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
	//? >=1.21.10 {
	@Accessor
	GuiRenderer getGuiRenderer();
	@Accessor
	FogRenderer getFogRenderer();
	//?}
}
