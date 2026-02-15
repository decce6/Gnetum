package me.decce.gnetum.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

//? >=1.21.10 {
import com.mojang.blaze3d.pipeline.BlendFunction;
//?}

@Mixin(RenderPipeline.class)
public interface RenderPipelineAccessor {
	//? >=1.21.10 {
	@Accessor @Mutable
	void setBlendFunction(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<BlendFunction> blend);
	//?}
}
