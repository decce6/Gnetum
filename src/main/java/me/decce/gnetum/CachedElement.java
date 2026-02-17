package me.decce.gnetum;

import me.decce.gnetum.util.AnyBooleanValue;
import me.decce.gnetum.util.TriStateBoolean;

public class CachedElement {
	public static final int RECORDED_TIME = 5;
    public TriStateBoolean enabled;
	transient int pass = 1;
	transient final double[] time = new double[RECORDED_TIME];
	private transient double timeBegin;
	private transient int timeIndex;
    public transient boolean hidden;

    public CachedElement() {
        this(new TriStateBoolean(AnyBooleanValue.AUTO));
    }

    public CachedElement(TriStateBoolean enabled) {
        this.enabled = enabled;
    }

	public void next() {
		if (++timeIndex >= RECORDED_TIME) {
			timeIndex = 0;
		}
	}

	public void begin() {
		timeBegin = Gnetum.time().get();
	}

	public void end() {
		var timeEnd = Gnetum.time().get();
		time[timeIndex] = timeEnd - timeBegin;
	}

	public boolean shouldRender() {
		return (enabled.get() && Gnetum.pass == pass) ||
				(!enabled.get());
	}

	public boolean shouldRender(boolean enableOnAuto) {
		var enabled = this.enabled.value == AnyBooleanValue.AUTO ? enableOnAuto : this.enabled.get();
		return !enabled || Gnetum.pass == pass;
	}

	public boolean isUncached(boolean enableOnAuto) {
		var enabled = this.enabled.value == AnyBooleanValue.AUTO ? enableOnAuto : this.enabled.get();
		return !enabled;
	}

	public boolean isUncached() {
		return !enabled.get();
	}
}
