package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.10 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import me.decce.gnetum.AutoDisable;
import me.decce.gnetum.FramebufferBlitter;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? }

//? >=1.21.10 {
@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void gnetum$beginHud(CallbackInfo ci) {
		if (!Gnetum.flushing) {
			AutoDisable.begin(AutoDisable.HUD);
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void gnetum$endHud(CallbackInfo ci) {
		if (!Gnetum.flushing) {
			AutoDisable.end(AutoDisable.HUD);
		}
	}

	@Unique
	private boolean gnetum$drawingBlit;

	//? >=26.3 {
	/*@ModifyArg(method = "executeDraw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getCompiledPipeline(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)Lcom/mojang/renderpearl/api/pipeline/CompiledRenderPipeline;"))
	*///? } else {
	@ModifyArg(method = "executeDraw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V"))
	//? }
	private RenderPipeline gnetum$checkBlit(RenderPipeline pipeline) {
		gnetum$drawingBlit = FramebufferBlitter.isBlitPipeline(pipeline);
		return pipeline;
	}

	//? >=26.2 {
	/*//? >=26.3 {
	/^@WrapOperation(method = "executeDraw", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/commands/RenderPass;drawIndexed(IIIII)V"))
	^///? } else {
	@WrapOperation(method = "executeDraw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIIII)V"))
	//? }
	private void gnetum$timeBlit(RenderPass pass, int indexCount, int instanceCount, int firstIndex, int vertexOffset, int firstInstance, Operation<Void> original) {
		boolean blit = gnetum$drawingBlit;
		if (blit) {
			AutoDisable.begin(AutoDisable.BLIT, pass);
		}
		original.call(pass, indexCount, instanceCount, firstIndex, vertexOffset, firstInstance);
		if (blit) {
			AutoDisable.end(AutoDisable.BLIT, pass);
		}
	}
	*///? } else {
	@WrapOperation(method = "executeDraw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"))
	private void gnetum$timeBlit(RenderPass pass, int baseVertex, int firstIndex, int indexCount, int instanceCount, Operation<Void> original) {
		boolean blit = gnetum$drawingBlit;
		if (blit) {
			AutoDisable.begin(AutoDisable.BLIT, pass);
		}
		original.call(pass, baseVertex, firstIndex, indexCount, instanceCount);
		if (blit) {
			AutoDisable.end(AutoDisable.BLIT, pass);
		}
	}
	//? }
}
//? } else {
/*@Mixin(targets = {})
public class GuiRendererMixin {}
*///? }
