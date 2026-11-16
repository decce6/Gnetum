package me.decce.gnetum;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
//? >=26.2 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import me.decce.gnetum.mixins.VulkanQueryPoolAccessor;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
//? >=26.3 {
/^import com.mojang.renderpearl.api.commands.GpuQueryPool;
import com.mojang.renderpearl.backend.opengl.GlQueryPool;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanQueryPool;
^///? } else {
import com.mojang.blaze3d.opengl.GlQueryPool;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanQueryPool;
//? }
*///? } else {
import org.lwjgl.opengl.GL;
//? }
//? >=1.21.10
import com.mojang.blaze3d.systems.RenderPass;

public class GpuTimer {
	public static final int PAIRS = 128;
	private static final long MAX_ELAPSED_NANOS = 250_000_000L;
	private static boolean initialized;
	private static boolean supported;
	private static boolean warned;
	private static boolean warnedInvalid;
	private static long counterMask = -1L;
	private static final long[] elapsed = new long[PAIRS];
	//? >=26.2 {
	/*private static GpuQueryPool pool;
	private static float period;
	*///? } else {
	private static final int[] queries = new int[PAIRS * 2];
	//? }

	public static boolean supported() {
		if (!initialized) {
			initialized = true;
			try {
				//? >=26.2 {
				/*pool = RenderSystem.getDevice().createTimestampQueryPool(PAIRS * 2);
				period = RenderSystem.getDevice().getDeviceInfo().timestampPeriod();
				int bits = pool instanceof VulkanQueryPool ? vulkanCounterBits(((VulkanQueryPoolAccessor) pool).getDevice())
						: pool instanceof GlQueryPool ? glCounterBits() : 64;
				*///? } else {
				var caps = GL.getCapabilities();
				if (!caps.OpenGL33 && !caps.GL_ARB_timer_query) {
					Gnetum.LOGGER.info("GPU timestamps are not supported, measuring CPU time only");
					return false;
				}
				int bits = glCounterBits();
				//? }
				if (bits <= 0) {
					Gnetum.LOGGER.info("GPU timestamp counter has no bits, measuring CPU time only");
					//? >=26.2
					/*pool.close();*/
					return false;
				}
				if (bits < 64) {
					counterMask = (1L << bits) - 1;
				}
				//? <26.2
				GL15.glGenQueries(queries);
				supported = true;
			} catch (RuntimeException e) {
				Gnetum.LOGGER.warn("GPU timestamps are unavailable, measuring CPU time only", e);
			}
		}
		return supported;
	}

	public static boolean write(int index) {
		try {
			//? >=26.2 {
			/*RenderSystem.getDevice().createCommandEncoder().writeTimestamp(pool, index);
			*///? } else {
			GL33.glQueryCounter(queries[index], GL33.GL_TIMESTAMP);
			//? }
			return true;
		} catch (RuntimeException e) {
			warn(e);
			return false;
		}
	}

	//? >=1.21.10 {
	public static boolean write(RenderPass pass, int index) {
		//? >=26.2 {
		/*try {
			pass.writeTimestamp(pool, index);
			return true;
		} catch (RuntimeException e) {
			warn(e);
			return false;
		}
		*///? } else {
		return write(index);
		//? }
	}
	//? }

	public static int read(int firstPair, int count) {
		//? >=26.2 {
		/*var values = pool.getValues(firstPair * 2, count * 2);
		for (int i = 0; i < count; i++) {
			if (values[i * 2].isEmpty() || values[i * 2 + 1].isEmpty()) {
				return i;
			}
			store(firstPair + i, (long) (((values[i * 2 + 1].getAsLong() - values[i * 2].getAsLong()) & counterMask) * period));
		}
		*///? } else {
		for (int i = 0; i < count; i++) {
			int pair = firstPair + i;
			if (GL15.glGetQueryObjecti(queries[pair * 2 + 1], GL15.GL_QUERY_RESULT_AVAILABLE) == 0) {
				return i;
			}
			store(pair, (GL33.glGetQueryObjecti64(queries[pair * 2 + 1], GL15.GL_QUERY_RESULT)
					- GL33.glGetQueryObjecti64(queries[pair * 2], GL15.GL_QUERY_RESULT)) & counterMask);
		}
		//? }
		return count;
	}

	private static int glCounterBits() {
		return GL15.glGetQueryi(GL33.GL_TIMESTAMP, GL15.GL_QUERY_COUNTER_BITS);
	}

	//? >=26.2 {
	/*// Minecraft writes timestamps on the graphics queue
	private static int vulkanCounterBits(VulkanDevice device) {
		VkDevice vk = device.vkDevice();
		int[] count = new int[1];
		VK10.vkGetPhysicalDeviceQueueFamilyProperties(vk.getPhysicalDevice(), count, null);
		try (var families = VkQueueFamilyProperties.calloc(count[0])) {
			VK10.vkGetPhysicalDeviceQueueFamilyProperties(vk.getPhysicalDevice(), count, families);
			return families.get(device.graphicsQueue().queueFamilyIndex()).timestampValidBits();
		}
	}
	*///? }

	private static void store(int pair, long nanos) {
		if (nanos >= 0 && nanos <= MAX_ELAPSED_NANOS) {
			elapsed[pair] = nanos;
			return;
		}
		elapsed[pair] = -1;
		if (!warnedInvalid) {
			warnedInvalid = true;
			Gnetum.LOGGER.warn("GPU timer returned {} ns for one span, ignoring invalid GPU samples", nanos);
		}
	}

	public static void disable() {
		supported = false;
	}

	public static long elapsed(int pair) {
		return elapsed[pair];
	}

	public static boolean available(int index) {
		//? >=26.2 {
		/*return pool.getValues(index, 1)[0].isPresent();
		*///? } else {
		return GL15.glGetQueryObjecti(queries[index], GL15.GL_QUERY_RESULT_AVAILABLE) != 0;
		//? }
	}

	private static void warn(RuntimeException e) {
		if (!warned) {
			warned = true;
			Gnetum.LOGGER.warn("Failed to write GPU timestamp, measuring CPU time only for that span", e);
		}
	}
}
