package me.decce.gnetum.compat.sodium;

import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
//? sodium_legacy {
/*import net.caffeinemc.mods.sodium.client.SodiumClientMod;
*///? }

public class SodiumCompat {
    public static boolean INSTALLED = Gnetum.platform().isModLoaded("sodium");

    public static boolean isVignetteEnabled() {
        //? >=1.21.1 {
        throw new RuntimeException("Should not reach here");
        //? } else {
        /*//? sodium_legacy {
        /^return SodiumClientMod.options().quality.enableVignette;
        ^///? } else {
        return Minecraft.useFancyGraphics();
        //? }
        *///? }
    }
}
