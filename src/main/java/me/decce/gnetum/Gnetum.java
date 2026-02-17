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

	public static CachedElement HAND_ELEMENT = new CachedElement();
	public static CachedElement UNKNOWN_ELEMENT = new CachedElement();
	private static final TimeSource time = new GlfwTimeSource();
	private static Framebuffers framebuffers;
	public static int pass = 1;
	public static boolean rendering;
	public static boolean catchingUp;
	public static GnetumConfig config;
	public static String currentElement;

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

	public static void disableCachingForElement(String id, String reason) {
		if (id == null) return;
		var element = getElement(id);
		if (element.enabled.get() && element.enabled.value == AnyBooleanValue.AUTO) {
			LOGGER.info("Disabling caching for element {}. Reason: {}", id, reason);
			element.enabled.defaultValue = false;
			framebuffers().dropCurrentFrame();
		}
	}

	public static CachedElement getElement() {
		return getElement(Gnetum.currentElement);
	}

	public static CachedElement getElement(Identifier element) {
		return getElement(VersionCompatUtil.stringValueOf(element));
	}

	public static CachedElement getElement(String element) {
		var map = config.map;
		if (element == null || !map.containsKey(element)) {
			return UNKNOWN_ELEMENT;
		}
		return map.get(element);
	}

	public static boolean shouldRender(String id) {
		return getElement(id).shouldRender();
	}

	public static boolean isCurrentElementUncached() {
		if (currentElement == null) {
			return true;
		}
		return getElement(currentElement).isUncached();
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

	public static void beginElement(String name) {
		currentElement = name;
		getElement(name).begin();
	}

	public static void endElement() {
		endElement(currentElement);
	}

	public static void endElement(String name) {
		getElement(name).end();
		currentElement = null;
	}

	public static void reset() {
		FPS_COUNTER.reset();
		framebuffers().resize();
		framebuffers().markForCatchUp();
	}
}
