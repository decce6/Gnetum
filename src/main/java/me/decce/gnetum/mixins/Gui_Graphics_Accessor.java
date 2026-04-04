package me.decce.gnetum.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >=1.21.10 {
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.gen.Invoker;

//?}
@Mixin(GuiGraphics.class)
public interface Gui_Graphics_Accessor // Named with _ because we use stonecutter to rename GuiGraphics to GuiGraphics for 26.1+; this line must not be renamed as the class name needs to be identical as the file name
{
	//? >=1.21.10 {
	@Accessor
	GuiRenderState getGuiRenderState();
	@Invoker
	void invokeInnerBlit(RenderPipeline pipeline,
	                     GpuTextureView textureView,
	                     GpuSampler sampler,
	                     int x0,
	                     int y0,
	                     int x1,
	                     int y1,
	                     float u0,
	                     float u1,
	                     float v0,
	                     float v1,
	                     int color);
	//?}
}
