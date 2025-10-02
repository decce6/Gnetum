package me.decce.gnetum;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraftforge.eventbus.ASMEventHandler;

public class ASMEventHandlerHelper {
    private static final ReferenceSet<ASMEventHandler> invalid = new ReferenceOpenHashSet<>(0);
    private static final Reference2ObjectMap<ASMEventHandler, String> mapModId = new Reference2ObjectOpenHashMap<>();

    public static String tryGetModId(ASMEventHandler asm) {
        if (mapModId.containsKey(asm)) {
            return mapModId.get(asm);
        }
        if (invalid.contains(asm)) {
            return null;
        }
        Class<?> clazz = tryGetClass(asm);
        if (clazz == null) {
            invalid.add(asm);
            return null;
        }
        var module = clazz.getModule();
        if (module.isNamed()) {
            var modid = module.getName();
            mapModId.put(asm, modid);
            return modid;
        }
        else {
            invalid.add(asm);
            return null;
        }
    }

    // Find out which class the given event handler will call
    private static Class<?> tryGetClass(ASMEventHandler asm) {
        String name = asm.toString().substring(5);
        if (name.startsWith("class ")) {
            name = name.substring(6);
            name = name.substring(0, name.indexOf(' '));
            try {
                Class<?> clazz = Class.forName(name);
                return clazz;
            } catch (Throwable ignored) {
                return null;
            }
        } else if (name.contains("@")) {
            name = name.substring(0, name.indexOf('@'));
            //Gnetum.LOGGER.info("name2="+name);
            try {
                Class<?> clazz = Class.forName(name);
                return clazz;
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }
}
