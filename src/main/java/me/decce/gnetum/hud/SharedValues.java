package me.decce.gnetum.hud;

//? fabric && <=1.21.10 {
/*import me.decce.gnetum.mixins.GuiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
//$ import_delta_tracker
import net.minecraft.client.DeltaTracker;

public class SharedValues {
    public static GuiGraphics guiGraphics;
    public static DeltaTracker deltaTracker;

    public static Gui gui() {
        return Minecraft.getInstance().gui;
    }

    public static GuiAccessor guiAccessor() {
        return (GuiAccessor) gui();
    }

    public static GuiGraphics guiGraphics() {
        return guiGraphics;
    }

    public static DeltaTracker deltaTracker() {
        return deltaTracker;
    }
}
*///? }