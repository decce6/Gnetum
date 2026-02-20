package me.decce.gnetum.platform;

import me.decce.gnetum.CachedElement;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ElementGatherer {
	private static boolean gathered;

	public void gather() {
		if (gathered) {
			return;
		}
		gathered = true;

		var newMap = new LinkedHashMap<String, CachedElement>(Gnetum.config.map.size());

		newMap.put(Constants.HAND_ELEMENT, new CachedElement(Constants.HAND_ELEMENT));

		gatherImpl(newMap);

		newMap.put(Constants.UNKNOWN_ELEMENTS, new CachedElement(Constants.UNKNOWN_ELEMENTS));
		newMap.put(Constants.DEBUG_OVERLAY, new CachedElement(Constants.DEBUG_OVERLAY));

		for (var entry : newMap.entrySet()) {
			if (Gnetum.config.map.containsKey(entry.getKey())) {
				var original = Gnetum.config.map.get(entry.getKey());
				entry.getValue().enabled = original.enabled;
			}
		}

		newMap.get(Constants.DEBUG_OVERLAY).enabled.defaultValue = false;
		newMap.get(Constants.HAND_ELEMENT).enabled.defaultValue = false;

		Gnetum.config.map = newMap;
	}

	protected abstract void gatherImpl(Map<String, CachedElement> map);
}
