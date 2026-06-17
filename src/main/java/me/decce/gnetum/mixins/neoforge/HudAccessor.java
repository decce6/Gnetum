package me.decce.gnetum.mixins.neoforge;

//? neoforge {
/*import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >=26.2 {
/^import net.minecraft.client.gui.Hud;
@Mixin(Hud.class)
^///? } else {
import net.minecraft.client.gui.Gui;
@Mixin(Gui.class)
//? }
@SuppressWarnings("UnstableApiUsage")
public interface HudAccessor {
	@Accessor(remap = false)
	GuiLayerManager getLayerManager();
}
*///?}
