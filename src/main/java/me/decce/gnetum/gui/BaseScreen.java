package me.decce.gnetum.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class BaseScreen extends Screen {
    protected final Screen parent;

    public BaseScreen() {
        this(Minecraft.getInstance().screen);
    }

    protected BaseScreen(Screen parent) {
        this(new TextComponent("Gnetum"), parent);
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
    }

    protected void addDoneButton() {
        Button btnClose = new Button(width / 2 - 60, height / 2 + 90, 120, 20, new TranslatableComponent("gui.done"), btn -> this.close());
        this.addRenderableWidget(btnClose);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, partialTick);

        this.renderTitle(poseStack);
    }

    protected void renderTitle(PoseStack poseStack) {
        GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, "Gnetum", width / 2, 10, 0xFFFFFF);
    }
}
