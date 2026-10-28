package me.decce.gnetum;

import net.minecraft.client.Minecraft;
//? >=1.21.10
import com.mojang.blaze3d.systems.RenderPass;

public class AutoDisable {
	public static final int PASS = 0;
	public static final int BLIT = 1;
	public static final int HUD = 2;

	private static final long WINDOW_NANOS = 1_000_000_000L;
	private static final int MIN_CYCLES = 5;
	private static final int AGREEING_WINDOWS = 3;
	private static final long MARGIN_NANOS = 20_000L;

	private static final int WRITING = -1;
	private static final int ABANDONED = -2;
	private static final int SLOTS = GpuTimer.PAIRS;
	private static final int[] slotKind = new int[SLOTS];
	private static final int[] slotEpoch = new int[SLOTS];
	private static final long[] slotCpu = new long[SLOTS];
	private static final int[] slotWritten = new int[SLOTS];
	private static int head;
	private static int tail;

	private static final long[] spanStart = new long[3];
	private static final int[] spanSlot = new int[3];
	private static final boolean[] spanActive = new boolean[3];
	private static long passExcluded;

	private static final long[] cost = new long[3];
	private static int epoch;
	private static long windowStart = -1;
	private static int frames;
	private static int cycles;
	private static int votes;
	private static boolean windowDirty;
	private static boolean warmingUp;

	private static boolean suppressed;
	private static boolean calibrating;
	private static double cycleCost;
	private static double blitCost;
	private static double hudOffset;

	public static boolean suppressed() {
		return suppressed;
	}

	public static void reset() {
		for (int kind = 0; kind < spanActive.length; kind++) {
			abandon(kind);
			spanActive[kind] = false;
		}
		windowStart = -1;
		epoch++;
		votes = 0;
		setSuppressed(false);
	}

	public static boolean begin(int kind) {
		if (!shouldMeasure(kind)) {
			return false;
		}
		int slot = allocateSlot();
		if (slot >= 0 && !GpuTimer.write(slot * 2)) {
			slotCpu[slot] = ABANDONED;
			slot = -1;
		}
		start(kind, slot);
		return true;
	}

	private static void abandon(int kind) {
		if (spanActive[kind] && spanSlot[kind] >= 0) {
			slotCpu[spanSlot[kind]] = ABANDONED;
		}
	}

	public static void end(int kind) {
		if (!spanActive[kind]) return;
		int slot = spanSlot[kind];
		finish(kind, slot >= 0 && GpuTimer.write(slot * 2 + 1));
	}

	//? >=1.21.10 {
	public static void begin(int kind, RenderPass pass) {
		if (!shouldMeasure(kind)) {
			return;
		}
		int slot = allocateSlot();
		if (slot >= 0 && !GpuTimer.write(pass, slot * 2)) {
			slotCpu[slot] = ABANDONED;
			slot = -1;
		}
		start(kind, slot);
	}

	public static void end(int kind, RenderPass pass) {
		if (!spanActive[kind]) return;
		int slot = spanSlot[kind];
		finish(kind, slot >= 0 && GpuTimer.write(pass, slot * 2 + 1));
	}
	//? }

	public static void excludeFromPass(long nanos) {
		if (spanActive[PASS]) {
			passExcluded += nanos;
		}
	}

	public static void onCycle() {
		cycles++;
	}

	public static void tick() {
		if (suppressed && !Gnetum.config.autoDisable.get()) {
			setSuppressed(false);
		}
		var mc = Minecraft.getInstance();
		if (!Gnetum.config.enabled.get() || !Gnetum.config.autoDisable.get() || mc.level == null
				|| VersionCompatUtil.isInScreen() || VersionCompatUtil.isHudHidden()) {
			windowStart = -1;
			votes = 0;
			return;
		}
		long now = Gnetum.time().nanos();
		if (windowStart < 0) {
			startWindow(now);
			epoch++;
			warmingUp = true;
			return;
		}
		frames++;
		if (VersionCompatUtil.hasVisibleToasts()) {
			windowDirty = true;
		}
		if (now - windowStart >= WINDOW_NANOS) {
			poll();
			evaluate(now - windowStart);
			startWindow(now);
		}
	}

	private static boolean shouldMeasure(int kind) {
		if (windowStart < 0 || suppressed != (kind == HUD)) {
			return false;
		}
		abandon(kind);
		return true;
	}

	private static void start(int kind, int slot) {
		if (slot >= 0) {
			slotWritten[slot] = 1;
		}
		spanActive[kind] = true;
		spanSlot[kind] = slot;
		if (kind == PASS) {
			passExcluded = 0;
		}
		spanStart[kind] = Gnetum.time().nanos();
	}

	private static void finish(int kind, boolean gpuWritten) {
		long cpu = Gnetum.time().nanos() - spanStart[kind];
		if (kind == PASS) {
			cpu = Math.max(0, cpu - passExcluded);
		}
		spanActive[kind] = false;
		int slot = spanSlot[kind];
		if (gpuWritten) {
			slotWritten[slot] = 2;
			slotKind[slot] = kind;
			slotEpoch[slot] = epoch;
			slotCpu[slot] = cpu;
		}
		else {
			if (slot >= 0) {
				slotCpu[slot] = ABANDONED;
			}
			cost[kind] += cpu;
		}
	}

	private static int allocateSlot() {
		if (!GpuTimer.supported()) {
			return -1;
		}
		if ((head + 1) % SLOTS == tail) {
			poll();
			if ((head + 1) % SLOTS == tail) {
				return -1;
			}
		}
		int slot = head;
		head = (head + 1) % SLOTS;
		slotCpu[slot] = WRITING;
		slotWritten[slot] = 0;
		return slot;
	}

	private static void poll() {
		while (tail != head) {
			long cpu = slotCpu[tail];
			if (cpu == WRITING) {
				return;
			}
			if (cpu == ABANDONED) {
				int written = slotWritten[tail];
				if (written > 0 && !GpuTimer.available(tail * 2 + written - 1)) {
					return;
				}
				tail = (tail + 1) % SLOTS;
				continue;
			}
			int limit = head > tail ? head : SLOTS;
			int run = 1;
			while (tail + run < limit && slotCpu[tail + run] >= 0) {
				run++;
			}
			int ready = GpuTimer.read(tail, run);
			for (int slot = tail; slot < tail + ready; slot++) {
				if (slotEpoch[slot] == epoch) {
					cost[slotKind[slot]] += Math.max(slotCpu[slot], GpuTimer.elapsed(slot));
				}
			}
			tail = (tail + ready) % SLOTS;
			if (ready < run) {
				return;
			}
		}
	}

	private static void startWindow(long now) {
		windowStart = now;
		frames = 0;
		cycles = 0;
		windowDirty = false;
		cost[PASS] = cost[BLIT] = cost[HUD] = 0;
	}

	private static void evaluate(long elapsedNanos) {
		if (warmingUp) {
			warmingUp = false;
			return;
		}
		if (frames == 0 || windowDirty) return;
		if (!suppressed) {
			if (cycles < MIN_CYCLES) {
				votes = 0;
				return;
			}
			cycleCost = cost[PASS] / (double) cycles;
			double cycleRatio = cycles / (double) frames;
			blitCost = cost[BLIT] / (double) frames;
			double saving = cycleCost * (1 - cycleRatio) - blitCost;
			if (vote(saving < -MARGIN_NANOS)) {
				Gnetum.LOGGER.info("Disabling Gnetum: HUD costs {} us per frame without it, {} us with it",
						micros(cycleCost), micros(cycleCost * cycleRatio + blitCost));
				calibrating = true;
				switchMode(true);
			}
		}
		else {
			double hud = cost[HUD] / (double) frames;
			if (calibrating) {
				calibrating = false;
				hudOffset = cycleCost - hud;
				return;
			}
			double expected = Math.max(0, hud + hudOffset);
			double cycleRatio = estimateCycleRatio(elapsedNanos);
			double saving = expected * (1 - cycleRatio) - blitCost;
			if (vote(saving > MARGIN_NANOS)) {
				Gnetum.LOGGER.info("Enabling Gnetum: HUD costs {} us per frame without it, about {} us with it",
						micros(expected), micros(expected * cycleRatio + blitCost));
				switchMode(false);
			}
		}
	}

	private static double estimateCycleRatio(long elapsedNanos) {
		double ratio = 1.0 / Gnetum.config.getNumberOfPasses();
		int maxFps = Gnetum.config.getMaxFps();
		if (maxFps != Constants.UNLIMITED_FPS) {
			ratio = Math.min(ratio, maxFps * (elapsedNanos / 1e9) / frames);
		}
		return ratio;
	}

	private static boolean vote(boolean wantsSwitch) {
		votes = wantsSwitch ? votes + 1 : 0;
		return votes >= AGREEING_WINDOWS;
	}

	private static void switchMode(boolean suppress) {
		votes = 0;
		epoch++;
		warmingUp = true;
		setSuppressed(suppress);
	}

	private static long micros(double nanos) {
		return Math.round(nanos / 1000);
	}

	private static void setSuppressed(boolean value) {
		if (suppressed && !value) {
			Gnetum.framebuffers().markForCatchUp();
		}
		suppressed = value;
	}
}
