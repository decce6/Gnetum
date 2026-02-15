package me.decce.gnetum.platform.fabric;

//? fabric {

import me.decce.gnetum.Gnetum;
import net.fabricmc.api.ClientModInitializer;

public class FabricEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Gnetum.init();
	}

}
//?}
