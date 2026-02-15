package me.decce.gnetum.util;

import com.google.gson.annotations.SerializedName;
import net.minecraft.network.chat.Component;

public enum AnyBooleanValue {
    @SerializedName("AUTO")
    AUTO,
    @SerializedName("OFF")
    OFF,
    @SerializedName("ON")
    ON;

	public Component text() {
		return Component.translatable(switch (this) {
			case AUTO -> "gnetum.config.auto";
			case OFF -> "options.off";
			case ON -> "options.on";
		});
	}
}
