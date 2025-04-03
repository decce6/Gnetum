package me.decce.gnetum;

public class Passes {
    public final static int FORGE_PRE = 0;
    public final static int HOTBAR = 1;
    public final static int HUD_TEXT = 2;
    public final static int MISC = 3;

    public static int current = 0;

    public static void step() {
        if (++current == 4)
        {
            current = 0;

            FramebufferManager.getInstance().swapFramebuffers();
        }
    }
}
