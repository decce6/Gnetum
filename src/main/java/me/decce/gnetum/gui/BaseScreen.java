package me.decce.gnetum.gui;

import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BaseScreen extends Screen {
    protected final Screen parent;

    public BaseScreen() {
        this(Minecraft.getInstance().screen);
    }

    protected BaseScreen(Screen parent) {
        this(Component.literal("Gnetum"), parent);
    }

    protected BaseScreen(Component p_96550_, Screen parent) {
        super(p_96550_);
        this.parent = parent;
    }

    public void close() { // called when "Done" is pressed
        Gnetum.config.save();
        Minecraft.getInstance().setScreen(parent);
    }

    public void onClose() { // TODO: this is called when ESC is pressed: ideally we should have a dialog asking the user whether to save or discard changes
        super.onClose();
        Gnetum.config.save();
        //or: GnetumConfig.reload();
    }

    @Override
    public void init() {
        this.rebuild();
    }

    protected void rebuild() {
        this.clearWidgets();
        Button btnClose = Button
                .builder(Component.translatable("gui.done"), btn -> this.close())
                .pos(width / 2 - 60, height / 2 + 90)
                .size(120, 20)
                .build();
        this.addRenderableWidget(btnClose);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(Minecraft.getInstance().font, "Gnetum", width / 2, 10, 0xFFFFFF);
    }
}
