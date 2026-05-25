//? >=1.21.10 {
package me.decce.gnetum;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
//$ import_blend_factors
import com.mojang.blaze3d.platform.DestFactor; import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.decce.gnetum.mixins.Gui_Graphics_Accessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.util.Optional;

//? >=26.2 {
/*import com.mojang.blaze3d.GpuFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
*///? } else if >=26 {
/*import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.shaders.UniformType;
*///? } else {
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.platform.DepthTestFunction;
//? }

public class FramebufferBlitter {
    public static final BlendFunction GNETUM_FBO_BLEND = new BlendFunction(
            /*$src_factor ONE*/ SourceFactor.ONE
            , /*$dest_factor ONE_MINUS_SRC_ALPHA*/ DestFactor.ONE_MINUS_SRC_ALPHA
            , /*$src_factor ONE*/ SourceFactor.ONE
            , /*$dest_factor ONE_MINUS_SRC_ALPHA*/ DestFactor.ONE_MINUS_SRC_ALPHA
    );
    public static final RenderPipeline GNETUM_FBO_BLIT_PIPELINE = configure(RenderPipeline.builder())
            .withLocation(Identifier.parse("gnetum:fast_fbo_blit_pipeline"))
            .build();

    private static RenderPipeline.Builder configure(RenderPipeline.Builder builder) {
        return builder
            .withVertexShader("core/position_tex")
            .withFragmentShader("core/position_tex")
            //? >=26.2 {
            /*.withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(new ColorTargetState(Optional.of(GNETUM_FBO_BLEND), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_COLOR))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            *///? } else if >=26 {
            /*.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSampler("Sampler0")
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            .withColorTargetState(new ColorTargetState(Optional.of(GNETUM_FBO_BLEND), ColorTargetState.WRITE_COLOR))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            *///? } else {
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSampler("Sampler0")
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            .withBlend(GNETUM_FBO_BLEND)
            .withColorWrite(true, false)
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            //? }
        ;
    }

    public static void blit(GpuTextureView source, GuiGraphics guiGraphics) {
        ((Gui_Graphics_Accessor) guiGraphics).invokeInnerBlit(GNETUM_FBO_BLIT_PIPELINE,
                source,
                RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST),
                0,
                0,
                guiGraphics.guiWidth(),
                guiGraphics.guiHeight(),
                0,
                1,
                1,
                0,
                0xFFFFFFFF);
    }
}
//? }