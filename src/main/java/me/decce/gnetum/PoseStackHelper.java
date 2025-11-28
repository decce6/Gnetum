package me.decce.gnetum;

import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.mixins.PoseStackAccessor;

import java.util.NoSuchElementException;

public class PoseStackHelper {
    // Some mods may push in one layer, then pop in another (https://github.com/Exopandora/ShoulderSurfing/blob/3edec3f67e5790e0b5c1a7f781770ba3d251cf38/common/src/main/java/com/github/exopandora/shouldersurfing/client/CrosshairRenderer.java#L49-L79)
    // We need to disable cache for both layers to prevent crashes
    public static void checkIf(boolean bl, PoseStack poseStack, Runnable runnable) {
        if (bl) {
            checked(poseStack, runnable);
        }
        else {
            runnable.run();
        }
    }

    public static void checked(PoseStack poseStack, Runnable runnable) {
        if (!checkedImpl(poseStack, runnable)) {
            Gnetum.disableCachingForCurrentElement("Pose Stack");
        }
    }

    private static boolean checkedImpl(PoseStack poseStack, Runnable runnable) {
        var accessor = (PoseStackAccessor) poseStack;
        var internalStack = accessor.getPoseStack();
        int previousSize = internalStack.size();
        try {
            runnable.run();
        } catch (NoSuchElementException e) {
            while (internalStack.size() < previousSize) {
                poseStack.pushPose();
            }
            return false;
        }
        if (internalStack.size() == previousSize) {
            return true;
        }
        while (internalStack.size() > previousSize) {
            poseStack.popPose();
        }
        while (internalStack.size() < previousSize) {
            poseStack.pushPose();
        }
        return false;
    }
}
