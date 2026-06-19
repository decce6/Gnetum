package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? <1.21.10 && >=1.21.1 {
/*import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LayeredDraw.class)
public interface LayeredDrawAccessor {
    @Accessor
    List<LayeredDraw.Layer> getLayers();
}
*///?} else {
@Mixin(targets = {})
public interface LayeredDrawAccessor {}
//?}
