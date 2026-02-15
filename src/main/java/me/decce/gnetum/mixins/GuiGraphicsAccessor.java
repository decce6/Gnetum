package me.decce.gnetum.mixins;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >=1.21.10 {
import net.minecraft.client.gui.render.state.GuiRenderState;
//?}
@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {
	//? >=1.21.10 {
	@Accessor
	GuiRenderState getGuiRenderState();
	//?}
}
