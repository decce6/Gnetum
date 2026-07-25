package me.decce.gnetum.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class MultiLineTextWidget extends AbstractWidget {
    private final MultiLineLabel label;
    public MultiLineTextWidget(int x, int y, int width, int height, String string) {
        super(x, y, width, height, Component.nullToEmpty(""));
        this.label = MultiLineLabel.create(Minecraft.getInstance().font, Arrays.stream(string.split("\n")).map(Component::nullToEmpty).toList());
    }

    @Override
    public void updateNarration(NarrationElementOutput arg) {

    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.label.renderCentered(poseStack, x, y, Minecraft.getInstance().font.lineHeight, -1);
    }
}
