package me.decce.gnetum.compat.betterhud;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.events.RenderEvents;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.hud.Hud;
import me.decce.gnetum.hud.VanillaHuds;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;

import static jobicade.betterhud.element.HudElement.*;

public class BetterHudCompat {
    public static final String MODID = "betterhud";
    public static final boolean modInstalled;
    public static final Hud OTHER_HUD = Hud.builder()
            .id(MODID, "other_huds")
            .defaultBlendFunc()
            .build();
    public static Reference2ReferenceMap<HudElement, Hud> mapTranslation = new Reference2ReferenceOpenHashMap<>();

    static {
        modInstalled = Loader.isModLoaded(MODID);
        if (modInstalled) {
            mapTranslation.put(HEALTH, VanillaHuds.PLAYER_LEFT);
            mapTranslation.put(ARMOR_BAR, VanillaHuds.PLAYER_LEFT);
            mapTranslation.put(AIR_BAR, VanillaHuds.PLAYER_RIGHT);
            mapTranslation.put(FOOD_BAR, VanillaHuds.PLAYER_RIGHT);
            mapTranslation.put(MOUNT, VanillaHuds.PLAYER_RIGHT);
            mapTranslation.put(CROSSHAIR, VanillaHuds.CROSSHAIRS);
            mapTranslation.put(VIGNETTE, VanillaHuds.VIGNETTE);
            mapTranslation.put(EXPERIENCE, VanillaHuds.EXPERIENCE);
            mapTranslation.put(EXPERIENCE_INFO, VanillaHuds.EXPERIENCE);
            mapTranslation.put(HELMET_OVERLAY, VanillaHuds.HELMET);
            mapTranslation.put(HOTBAR, VanillaHuds.HOTBAR);
            mapTranslation.put(JUMP_BAR, VanillaHuds.JUMP_BAR);
            mapTranslation.put(PORTAL, VanillaHuds.PORTAL);
            mapTranslation.put(POTION_BAR, VanillaHuds.POTION_ICONS);
            mapTranslation.put(SIDEBAR, VanillaHuds.SCOREBOARD);
        }
    }

    public static boolean isEnabled() {
        return modInstalled && BetterHud.getProxy().isModEnabled();
    }

    public static void onRenderGameOverlays(RenderGameOverlayEvent.Pre event) {
        BetterHud.MANAGER.reset(event.getResolution());
        RenderEvents.beginOverlayState();
        int pass = Gnetum.rendering ? Gnetum.passManager.current : 0;
        for (HudElement element : HudElement.SORTER.getSortedData(HudElement.SortType.PRIORITY)) {
            if (passOf(element) == pass) {
                element.tryRender(event);
            }
        }
        RenderEvents.endOverlayState();
        GlStateManager.enableBlend();
    }

    public static int passOf(HudElement element) {
        if (mapTranslation.containsKey(element)) {
            return passOf(mapTranslation.get(element));
        }
        return passOf(OTHER_HUD);
    }

    public static int passOf(Hud hud) {
        var cache = Gnetum.getCacheSetting(hud.id().toString());
        return cache.enabled.get() ? cache.pass : 0;
    }
}
