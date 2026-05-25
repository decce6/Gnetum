package me.decce.gnetum.mixins;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
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
	//? >=26.2 {
	/*@Accessor("mainRenderTarget")
	@Mutable
	void gnetum$setMainRenderTarget(RenderTarget target);
	@Accessor("mainRenderTarget")
	RenderTarget gnetum$getMainRenderTarget();
	*///? }
}
