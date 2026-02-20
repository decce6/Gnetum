package me.decce.gnetum;

import me.decce.gnetum.gl.GLSM;
import me.decce.gnetum.platform.Platform;

import me.decce.gnetum.time.GlfwTimeSource;
import me.decce.gnetum.time.TimeSource;
import me.decce.gnetum.util.AnyBooleanValue;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

//? fabric {
import me.decce.gnetum.platform.fabric.FabricPlatform;
//?} neoforge {
/*import me.decce.gnetum.platform.neoforge.NeoforgePlatform;
*///?}

public class Gnetum {
	public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);
	public static final FpsCounter FPS_COUNTER = new FpsCounter();

	private static final TimeSource time = new GlfwTimeSource();
	private static Framebuffers framebuffers;
	public static int pass = 1;
	public static boolean rendering;
	public static boolean catchingUp;
	public static GnetumConfig config;
	public static CachedElement currentElement;

	private static final Platform PLATFORM = createPlatformInstance();

	public static void init() {
		GLSM.set(new GlImpl());
		GnetumConfig.reload();
		config.save();
	}

	public static void nextPass() {
		if (pass == 0 && FPS_COUNTER.belowMax()) {
			pass++;
			finishAllPasses();
		}
		else if (pass > 0) {
			pass++;
		}
		if (pass > config.numberOfPasses) {
			if (FPS_COUNTER.belowMax()) {
				pass = 1;
				finishAllPasses();
			}
			else {
				pass = 0;
			}
		}
	}

	private static void finishAllPasses() {
		Distributor.resolve();
		framebuffers().swapFramebuffers();
		HudDeltaTracker.reset();
	}

	public static String getFpsString() {
		return String.format(Locale.ROOT, "HUD: %d fps T: %s (%d passes)", Gnetum.FPS_COUNTER.getFps(), Gnetum.config.getMaxFps() == Constants.UNLIMITED_FPS ? "inf" : Gnetum.config.getMaxFps(), Gnetum.config.numberOfPasses);
	}

	public static void disableCachingForCurrentElement(String reason) {
		if (currentElement == null) {
			LOGGER.error("No current element to disable");
			return;
		}
		disableCachingForElement(currentElement, reason);
	}

	public static void disableCachingForElement(CachedElement element, String reason) {
		if (element == null) return;
		if (element.enabled.get() && element.enabled.value == AnyBooleanValue.AUTO) {
			LOGGER.info("Disabling caching for element {}. Reason: {}", element.name, reason);
			element.enabled.defaultValue = false;
			framebuffers().dropCurrentFrame();
		}
	}

	public static CachedElement getElement(Identifier name) {
		return getElement(VersionCompatUtil.stringValueOf(name));
	}

	public static CachedElement getElement(String name) {
		var map = config.map;
		var element = map.get(name);
		if (element == null) {
			return map.get(Constants.UNKNOWN_ELEMENTS);
		}
		return element;
	}

	public static boolean shouldRender(String id) {
		return getElement(id).shouldRender();
	}

	public static boolean isCurrentElementUncached() {
		if (currentElement == null) {
			return true;
		}
		return currentElement.isUncached();
	}

	public static TimeSource time() {
		return time;
	}

	public static Framebuffers framebuffers() {
		if (framebuffers == null) {
			framebuffers = new Framebuffers();
		}
		return framebuffers;
	}

	public static Platform platform() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		*///?}
	}

	public static void reset() {
		FPS_COUNTER.reset();
		framebuffers().resize();
		framebuffers().markForCatchUp();
	}
}
