package me.decce.gnetum.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >=26.2 {
/*import org.spongepowered.asm.mixin.gen.Accessor;
//? >=26.3 {
/^import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanQueryPool;
^///? } else {
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanQueryPool;
//? }

@Mixin(VulkanQueryPool.class)
public interface VulkanQueryPoolAccessor {
	@Accessor
	VulkanDevice getDevice();
}
*///? } else {
@Mixin(targets = {})
public interface VulkanQueryPoolAccessor {}
//? }
