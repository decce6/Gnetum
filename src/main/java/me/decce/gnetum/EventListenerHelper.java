package me.decce.gnetum;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.neoforged.bus.ConsumerEventHandler;
import net.neoforged.bus.SubscribeEventListener;
import net.neoforged.bus.api.EventListener;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;

import java.net.URL;
import java.security.CodeSource;

public class EventListenerHelper {
    private static final ReferenceSet<EventListener> invalid = new ReferenceOpenHashSet<>(0);
    private static final Reference2ObjectMap<EventListener, String> mapModId = new Reference2ObjectOpenHashMap<>();

    public static String tryGetModId(EventListener sub) {
        if (mapModId.containsKey(sub)) {
            return mapModId.get(sub);
        }
        if (invalid.contains(sub)) {
            return null;
        }
        Class<?> clazz = tryGetClass(sub);
        if (clazz == null) {
            invalid.add(sub);
            return null;
        }
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            invalid.add(sub);
            return null;
        }
        URL url = codeSource.getLocation();
        var files = ModList.get().getModFiles();
        for (int i = 0; i < files.size(); i++) {
            IModFile file = files.get(i).getFile();
            if (url.getPath().contains(file.getFileName())) { // this check is not accurate, but shouldn't be dangerous
                String modid = file.getModInfos().get(0).getModId();
                mapModId.put(sub, modid);
                return modid;
            }
        }
        invalid.add(sub);
        return null;
    }

    // Find out which class the given event handler will call
    private static Class<?> tryGetClass(EventListener listener) {
        if (listener instanceof SubscribeEventListener) {
            String name = listener.toString().substring("@SubscribeEvent: ".length());
            if (name.startsWith("class ")) {
                name = name.substring("class ".length());
                int index = name.indexOf(' ');
                if (index == -1) return null;
                name = name.substring(0, index);
                return tryClassForName(name);
            } else if (name.contains("@")) {
                name = name.substring(0, name.indexOf('@'));
                return tryClassForName(name);
            }
        }
        else if (listener instanceof ConsumerEventHandler) {
            String name = listener.toString();
            int index = name.indexOf("$$");
            if (index == -1) return null;
            name = name.substring(0, index);
            return tryClassForName(name);
        }
        return null;
    }

    private static Class<?> tryClassForName(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
