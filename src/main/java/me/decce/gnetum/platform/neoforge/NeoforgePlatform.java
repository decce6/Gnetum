package me.decce.gnetum.platform.neoforge;

//? neoforge {

/*import me.decce.gnetum.Constants;
import me.decce.gnetum.platform.ElementGatherer;
import me.decce.gnetum.platform.Platform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoforgePlatform implements Platform {
	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public String getModName(String modId) {
		return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId).getMods().get(0).getDisplayName();
	}

	@Override
	public String getModId(Class<?> clazz) {
		var mod = clazz.getModule().getName();
		return mod == null ? Constants.UNKNOWN_ELEMENTS : mod;
	}

	@Override
	public ElementGatherer elementGatherer() {
		return new ElementGathererNeoForgeImpl();
	}
}
*///?}
