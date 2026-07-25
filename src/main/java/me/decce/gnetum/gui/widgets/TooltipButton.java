package me.decce.gnetum.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TooltipButton extends Button {
    private Component tooltip;
    public TooltipButton(int i, int j, int k, int l, Component arg, OnPress arg2) {
        super(i, j, k, l, arg, arg2);
    }

    public TooltipButton(int i, int j, int k, int l, Component text, OnPress arg2, Component tooltip) {
        super(i, j, k, l, text, arg2);
        this.tooltip = tooltip;
    }

    public void removeTooltip() {
        this.tooltip = null;
    }

    public void setTooltip(Component component) {
        this.tooltip = component;
    }

    public void setTooltip(String string) {
        this.tooltip = Component.nullToEmpty(string);
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, i, j);
        }
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int x, int y) {
        var screen = Minecraft.getInstance().screen;
        if (this.tooltip != null && screen != null) {
            screen.renderComponentTooltip(poseStack, List.of(this.tooltip), x, y);
        }
    }
}
