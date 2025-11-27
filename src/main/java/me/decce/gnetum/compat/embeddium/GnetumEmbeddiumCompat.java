package me.decce.gnetum.compat.embeddium;

import me.decce.gnetum.Gnetum;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;

@Mod.EventBusSubscriber(modid = Gnetum.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GnetumEmbeddiumCompat {
    @SubscribeEvent
    public static void onSodiumPagesRegister(OptionGUIConstructionEvent e) {
        var pages = e.getPages();

        pages.add(new GnetumEmbeddiumPage());
    }
}
