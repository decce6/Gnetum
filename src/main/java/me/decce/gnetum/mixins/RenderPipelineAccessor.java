package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.10 {
import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

//? if >26 {
/*import com.mojang.blaze3d.pipeline.ColorTargetState;
*///? } else {
import com.mojang.blaze3d.pipeline.BlendFunction;
//? }

@Mixin(RenderPipeline.class)
public interface RenderPipelineAccessor {
	//? if >=26.2 {
	/*@Accessor @Mutable
	void setColorTargetStates(ColorTargetState[] color);
	*///? } else if >=26 {
	/*@Accessor @Mutable
	void setColorTargetState(ColorTargetState color);
	*///? } else {
	@Accessor @Mutable
	void setBlendFunction(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<BlendFunction> blend);
	//?}
}
//? } else {
/*@Mixin(targets = {})
public interface RenderPipelineAccessor {}
*///? }