package me.decce.gnetum.versioned;

//? <1.21.10 {

import me.decce.gnetum.CachedElement;
import me.decce.gnetum.VersionCompatUtil;
import net.minecraft.resources.Identifier;

/*public class HudHandler {
	//? fabric {
	public enum Element {
		ARMOR_LEVEL("armor_level"),
		CHAT("chat"),
		HOTBAR("hotbar");
		private final Identifier id;
		private final String name;
		Element(String id) {
			this.id = Identifier.parse(id);
			this.name = VersionCompatUtil.stringValueOf(this.id);
		}

		public String getName() {
			return this.name;
		}

		public CachedElement newElement() {
			return new CachedElement(this.name);
		}
	}
//? }
}
*///?}
