//? >=1.21.10 {
package me.decce.gnetum;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
//$ import_blend_factors
import com.mojang.blaze3d.platform.DestFactor; import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.decce.gnetum.mixins.Gui_Graphics_Accessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
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
    public static final RenderPipeline.Snippet SNIPPET = RenderPipeline.builder()
            //? >=26.2 {
            /*.withBindGroupLayout(BindGroupLayouts.GLOBALS)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
            .withColorTargetState(new ColorTargetState(Optional.of(GNETUM_FBO_BLEND), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_COLOR))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            *///? } else if >=26 {
            /*.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSampler("Sampler0")
            .withColorTargetState(new ColorTargetState(Optional.of(GNETUM_FBO_BLEND), ColorTargetState.WRITE_COLOR))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            *///? } else {
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSampler("Sampler0")
            .withBlend(GNETUM_FBO_BLEND)
            .withColorWrite(true, false)
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            //? }
            .buildSnippet();
    public static final RenderPipeline.Snippet TRIANGLES_SNIPPET = RenderPipeline.builder()
            //? >=26.2 {
            /*.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
             *///? } else {
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES)
            //? }
            .buildSnippet();
    public static final RenderPipeline.Snippet QUADS_SNIPPET = RenderPipeline.builder()
            //? >=26.2 {
            /*.withPrimitiveTopology(PrimitiveTopology.QUADS)
             *///? } else {
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            //? }
            .buildSnippet();
    public static final RenderPipeline FAST_BLIT = RenderPipeline.builder(SNIPPET, TRIANGLES_SNIPPET)
            .withLocation(Identifier.parse("gnetum:fast_blit_pipeline"))
            .withVertexShader(Identifier.parse("gnetum:position_tex_fullscreen"))
            .withFragmentShader(Identifier.parse("gnetum:position_tex_fast"))
            .build();
    public static final RenderPipeline BLIT = RenderPipeline.builder(SNIPPET, TRIANGLES_SNIPPET)
            .withLocation(Identifier.parse("gnetum:blit_pipeline"))
            .withVertexShader(Identifier.parse("gnetum:position_tex_fullscreen"))
            .withFragmentShader(Identifier.parse("gnetum:position_tex"))
            .build();
    public static final RenderPipeline DOWNSCALED_FAST_BLIT = RenderPipeline.builder(SNIPPET, QUADS_SNIPPET)
            .withLocation(Identifier.parse("gnetum:downscaled_fast_blit_pipeline"))
            .withVertexShader("core/position_tex")
            .withFragmentShader(Identifier.parse("gnetum:position_tex_fast"))
            .build();
    public static final RenderPipeline DOWNSCALED_BLIT = RenderPipeline.builder(SNIPPET, QUADS_SNIPPET)
            .withLocation(Identifier.parse("gnetum:downscaled_blit_pipeline"))
            .withVertexShader("core/position_tex")
            .withFragmentShader(Identifier.parse("gnetum:position_tex"))
            .build();

    public static void blit(RenderTarget source, GuiGraphics guiGraphics) {
        if (Gnetum.config.downscale.get()) {
            ((Gui_Graphics_Accessor) guiGraphics).invokeInnerBlit(
                    Gnetum.config.fastFboBlit.get() ? DOWNSCALED_FAST_BLIT : DOWNSCALED_BLIT,
                    source.getColorTextureView(),
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
        else {
            ((Gui_Graphics_Accessor)guiGraphics).getGuiRenderState().submitGuiElement(new FramebufferRenderState(source));
        }
    }

    public record FramebufferRenderState(RenderTarget target) implements GuiElementRenderState {
        @Override
        public void buildVertices(VertexConsumer vertexConsumer) {
            // Dummy vertices to bypass the "Cannot build mesh with no position element" limitation
            // See position_tex_fullscreen.vsh
            vertexConsumer.addVertex(0, 0, 0).setUv(0, 0);
            vertexConsumer.addVertex(0, 0, 0).setUv(0, 0);
            vertexConsumer.addVertex(0, 0, 0).setUv(0, 0);
        }

        @Override
        public RenderPipeline pipeline() {
            return Gnetum.config.fastFboBlit.get() ? FAST_BLIT : BLIT;
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.singleTexture(target.getColorTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
        }

        @Override
        public ScreenRectangle scissorArea() {
            return null;
        }

        @Override
        public ScreenRectangle bounds() {
            return new ScreenRectangle(0, 0, target.width, target.height);
        }
    }
}
//? }