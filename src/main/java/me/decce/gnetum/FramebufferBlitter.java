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
import net.minecraft.client.Minecraft;
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
    //? >=26.3 {
    /*public static final String POSITION_TEX_SHADER = "gnetum:shaderc/position_tex";
    public static final String POSITION_TEX_FAST_SHADER = "gnetum:shaderc/position_tex_fast";
    public static final String POSITION_TEX_FULLSCREEN_SHADER = "gnetum:shaderc/position_tex_fullscreen";
    *///? } else {
    public static final String POSITION_TEX_SHADER = "gnetum:position_tex";
    public static final String POSITION_TEX_FAST_SHADER = "gnetum:position_tex_fast";
    public static final String POSITION_TEX_FULLSCREEN_SHADER = "gnetum:position_tex_fullscreen";
    //? }
    public static final BlendFunction GNETUM_FBO_BLEND = new BlendFunction(
            /*$src_factor ONE*/ SourceFactor.ONE
            , /*$dest_factor ONE_MINUS_SRC_ALPHA*/ DestFactor.ONE_MINUS_SRC_ALPHA
            , /*$src_factor ONE*/ SourceFactor.ONE
            , /*$dest_factor ONE_MINUS_SRC_ALPHA*/ DestFactor.ONE_MINUS_SRC_ALPHA
    );
    public static final RenderPipeline.Snippet SNIPPET = RenderPipeline.builder()
            //? >=26.2 {
            /*//? >=26.3 {
            /^.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            ^///? } else {
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            //? }
            .withBindGroupLayout(BindGroupLayouts.GLOBALS)
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
            //? >=26.3 {
            /*.withBindGroupLayout(BindGroupLayouts.PROJECTION)
            *///? }
            //? >=26.2 {
            /*.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
             *///? } else {
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES)
            //? }
            .buildSnippet();
    public static final RenderPipeline.Snippet QUADS_SNIPPET = RenderPipeline.builder()
            //? >=26.3 {
            /*.withBindGroupLayout(BindGroupLayouts.PROJECTION)
            *///? }
            //? >=26.2 {
            /*.withPrimitiveTopology(PrimitiveTopology.QUADS)
             *///? } else {
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            //? }
            .buildSnippet();
    public static final RenderPipeline FAST_BLIT = RenderPipeline.builder(SNIPPET, TRIANGLES_SNIPPET)
            .withLocation(Identifier.parse("gnetum:fast_blit_pipeline"))
            .withVertexShader(Identifier.parse(POSITION_TEX_FULLSCREEN_SHADER))
            .withFragmentShader(Identifier.parse(POSITION_TEX_FAST_SHADER))
            .build();
    public static final RenderPipeline BLIT = RenderPipeline.builder(SNIPPET, TRIANGLES_SNIPPET)
            .withLocation(Identifier.parse("gnetum:blit_pipeline"))
            .withVertexShader(Identifier.parse(POSITION_TEX_FULLSCREEN_SHADER))
            .withFragmentShader(Identifier.parse(POSITION_TEX_SHADER))
            .build();
    public static final RenderPipeline QUAD_FAST_BLIT = RenderPipeline.builder(SNIPPET, QUADS_SNIPPET)
            .withLocation(Identifier.parse("gnetum:quad_fast_blit_pipeline"))
            .withVertexShader("core/position_tex")
            .withFragmentShader(Identifier.parse(POSITION_TEX_FAST_SHADER))
            .build();
    public static final RenderPipeline QUAD_BLIT = RenderPipeline.builder(SNIPPET, QUADS_SNIPPET)
            .withLocation(Identifier.parse("gnetum:quad_blit_pipeline"))
            .withVertexShader("core/position_tex")
            .withFragmentShader(Identifier.parse(POSITION_TEX_SHADER))
            .build();

    public static boolean isBlitPipeline(RenderPipeline pipeline) {
        return pipeline == FAST_BLIT || pipeline == BLIT || pipeline == QUAD_FAST_BLIT || pipeline == QUAD_BLIT;
    }

    public static void blit(RenderTarget source, TileMask tiles, GuiGraphics guiGraphics) {
        var state = ((Gui_Graphics_Accessor) guiGraphics).getGuiRenderState();
        if (Gnetum.config.downscale.get()) {
            state.submitGuiElement(new TiledRenderState(source, tiles, guiGraphics.guiWidth(), guiGraphics.guiHeight()));
        }
        else if (tiles.isAll()) {
            state.submitGuiElement(new FramebufferRenderState(source));
        }
        else {
            var window = Minecraft.getInstance().getWindow();
            state.submitGuiElement(new TiledRenderState(source, tiles,
                    (float) window.getWidth() / window.getGuiScale(),
                    (float) window.getHeight() / window.getGuiScale()));
        }
    }

    public record TiledRenderState(RenderTarget target, TileMask tiles, float width, float height) implements GuiElementRenderState {
        @Override
        public void buildVertices(VertexConsumer vertexConsumer) {
            if (tiles.isAll()) {
                quad(vertexConsumer, 0, 0, width, height);
                return;
            }
            for (int row = 0; row < tiles.rows(); row++) {
                int column = 0;
                while (column < tiles.columns()) {
                    if (!tiles.isMarked(column, row)) {
                        column++;
                        continue;
                    }
                    int start = column;
                    while (column < tiles.columns() && tiles.isMarked(column, row)) {
                        column++;
                    }
                    quad(vertexConsumer,
                            start * TileMask.TILE_SIZE,
                            row * TileMask.TILE_SIZE,
                            Math.min(column * TileMask.TILE_SIZE, width),
                            Math.min((row + 1) * TileMask.TILE_SIZE, height));
                }
            }
        }

        private void quad(VertexConsumer vertexConsumer, float x0, float y0, float x1, float y1) {
            float u0 = x0 / width;
            float u1 = x1 / width;
            float v0 = 1 - y0 / height;
            float v1 = 1 - y1 / height;
            vertexConsumer.addVertex(x0, y0, 0).setUv(u0, v0);
            vertexConsumer.addVertex(x0, y1, 0).setUv(u0, v1);
            vertexConsumer.addVertex(x1, y1, 0).setUv(u1, v1);
            vertexConsumer.addVertex(x1, y0, 0).setUv(u1, v0);
        }

        @Override
        public RenderPipeline pipeline() {
            return Gnetum.config.fastFboBlit.get() ? QUAD_FAST_BLIT : QUAD_BLIT;
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