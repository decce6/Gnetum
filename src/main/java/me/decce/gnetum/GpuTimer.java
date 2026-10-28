package me.decce.gnetum;

//? >=26.2 {
/*import com.mojang.blaze3d.systems.RenderSystem;
//? >=26.3 {
/^import com.mojang.renderpearl.api.commands.GpuQueryPool;
^///? } else {
import com.mojang.blaze3d.systems.GpuQueryPool;
//? }
*///? } else {
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
//? }
//? >=1.21.10
import com.mojang.blaze3d.systems.RenderPass;

public class GpuTimer {
	public static final int PAIRS = 128;
	private static boolean initialized;
	private static boolean supported;
	private static boolean warned;
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
				*///? } else {
				var caps = GL.getCapabilities();
				if (!caps.OpenGL33 && !caps.GL_ARB_timer_query) {
					Gnetum.LOGGER.info("GPU timestamps are not supported, measuring CPU time only");
					return false;
				}
				GL15.glGenQueries(queries);
				//? }
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
			elapsed[firstPair + i] = Math.max(0, (long) ((values[i * 2 + 1].getAsLong() - values[i * 2].getAsLong()) * period));
		}
		*///? } else {
		for (int i = 0; i < count; i++) {
			int pair = firstPair + i;
			if (GL15.glGetQueryObjecti(queries[pair * 2 + 1], GL15.GL_QUERY_RESULT_AVAILABLE) == 0) {
				return i;
			}
			elapsed[pair] = Math.max(0, GL33.glGetQueryObjecti64(queries[pair * 2 + 1], GL15.GL_QUERY_RESULT)
					- GL33.glGetQueryObjecti64(queries[pair * 2], GL15.GL_QUERY_RESULT));
		}
		//? }
		return count;
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
