package me.decce.gnetum;

import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.mixins.PoseStackAccessor;

import java.util.NoSuchElementException;

public class PoseStackHelper {
    private static int initialSize;
    private static PoseStack.Pose bottomPose; // PoseStack.Pose has no public constructor

    public static void beginHudRendering(PoseStack poseStack) {
        var accessor = (PoseStackAccessor) poseStack;
        var internalStack = accessor.getPoseStack();
        initialSize = internalStack.size();
        bottomPose = internalStack.getFirst();
    }

    public static void endHudRendering(PoseStack poseStack) {
        var accessor = (PoseStackAccessor) poseStack;
        var internalStack = accessor.getPoseStack();
        while (internalStack.size() > initialSize) {
            poseStack.popPose();
        }
    }

    // Some mods may push in one layer, then pop in another (https://github.com/Exopandora/ShoulderSurfing/blob/3edec3f67e5790e0b5c1a7f781770ba3d251cf38/common/src/main/java/com/github/exopandora/shouldersurfing/client/CrosshairRenderer.java#L49-L79)
    // We need to disable cache for both layers to prevent crashes
    public static void checked(PoseStack poseStack, Runnable runnable) {
        if (!checkedImpl(poseStack, runnable)) {
            if (Gnetum.rendering) {
                Gnetum.disableCachingForCurrentElement("Pose Stack");
            }
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
                safePushPose(poseStack);
            }
            return false;
        }
        if (internalStack.size() == previousSize) {
            return true;
        }
        // push the stack to the original size; if current size is greater than previousSize, do not pop to prevent NoSuchElementException's
        while (internalStack.size() < previousSize) {
            safePushPose(poseStack);
        }
        return false;
    }

    private static void safePushPose(PoseStack poseStack) {
        var accessor = (PoseStackAccessor) poseStack;
        var internalStack = accessor.getPoseStack();
        if (internalStack.isEmpty()) {
            internalStack.addLast(bottomPose);
        }
        else {
            poseStack.pushPose();
        }
    }
}
