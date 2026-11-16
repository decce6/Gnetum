package me.decce.gnetum.mixins;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >=1.21.10 {
import net.minecraft.client.gui.render.state.GuiRenderState;
//?}
@Mixin(GuiGraphics.class)
public interface Gui_Graphics_Accessor // Named with _ because we use stonecutter to rename GuiGraphics to GuiGraphics for 26.1+; this line must not be renamed as the class name needs to be identical as the file name
{
	//? >=1.21.10 {
	@Accessor
	GuiRenderState getGuiRenderState();
	//?}
}
