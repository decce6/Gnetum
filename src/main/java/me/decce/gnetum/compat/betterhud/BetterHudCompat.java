package me.decce.gnetum.compat.betterhud;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.events.RenderEvents;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.Passes;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;

import static jobicade.betterhud.element.HudElement.*;

public class BetterHudCompat {
    public static final boolean modInstalled;

    public static Reference2IntMap<HudElement> element2PassMap = new Reference2IntOpenHashMap<>();

    static {
        modInstalled = Loader.isModLoaded("betterhud");
        if (modInstalled) {
            List<HudElement> uncached = Lists.newArrayList(HudElement.CROSSHAIR, HudElement.VIGNETTE, HudElement.HELMET_OVERLAY);
            List<HudElement> pass0 = new ArrayList<>();
            List<HudElement> pass3 = Lists.newArrayList(AIR_BAR, ARMOR_BAR, EXPERIENCE, EXPERIENCE_INFO, FOOD_BAR, HEALTH, HOTBAR, JUMP_BAR, OFFHAND, PORTAL, POTION_BAR, SIDEBAR);
            HudElement.ELEMENTS.forEach(element -> {
                if (!uncached.contains(element) && !pass3.contains(element)) {
                    pass0.add(element);
                }
            });
            uncached.forEach(element -> element2PassMap.put(element, -1));
            pass0.forEach(element -> element2PassMap.put(element, 0));
            pass3.forEach(element -> element2PassMap.put(element, 3));
        }
    }

    public static void onRenderGameOverlays(RenderGameOverlayEvent.Pre event) {
        BetterHud.MANAGER.reset(event.getResolution());
        RenderEvents.beginOverlayState();
        int pass = Gnetum.rendering ? Passes.current : -1;
        for (HudElement element : HudElement.SORTER.getSortedData(HudElement.SortType.PRIORITY)) {
            if (BetterHudCompat.element2PassMap.getInt(element) == pass) {
                element.tryRender(event);
            }
        }
        RenderEvents.endOverlayState();
        GlStateManager.enableBlend();
    }
}
