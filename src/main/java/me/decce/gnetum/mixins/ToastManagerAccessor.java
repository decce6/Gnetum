package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.10 {
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
//? }

//? >=1.21.10 {
@Mixin(ToastManager.class)
public interface ToastManagerAccessor {
    @Accessor
    List<?> getVisibleToasts();
}
//? } else {
/*@Mixin(targets = "")
public interface ToastManagerAccessor {}
*///? }
