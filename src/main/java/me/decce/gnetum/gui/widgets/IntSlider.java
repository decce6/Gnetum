package me.decce.gnetum.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.widget.ForgeSlider;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class IntSlider extends ForgeSlider {
    private Supplier<String> text;
    private Supplier<String> specialText;
    private Consumer<Integer> setter;
    private Predicate<Integer> special;
    private Component tooltip;

    public IntSlider(int x, int y, int width, int height, Supplier<String> text, int minValue, int maxValue, int currentValue, boolean drawString, Consumer<Integer> setter) {
        this(x, y, width, height, text, minValue, maxValue, currentValue, 1, drawString, setter);
    }

    public IntSlider(int x, int y, int width, int height, Supplier<String> text, int minValue, int maxValue, int currentValue, int step, boolean drawString, Consumer<Integer> setter) {
        this(x, y, width, height, text, minValue, maxValue, currentValue, step, drawString, setter, null, null);
    }

    public IntSlider(int x, int y, int width, int height, Supplier<String> text, int minValue, int maxValue, int currentValue, int step, boolean drawString, Consumer<Integer> setter, Predicate<Integer> special, Supplier<String> specialText) {
        super(x, y, width, height, TextComponent.EMPTY, TextComponent.EMPTY, minValue, maxValue, currentValue, step, 0, drawString);
        this.setter = setter;
        this.text = text;
        this.special = special;
        this.specialText = specialText;
        this.updateMessage();
    }

    @Override
    protected void applyValue() {
        super.applyValue();

        this.setter.accept(this.getValueInt());
    }

    @Override
    protected void updateMessage() {
        if (this.special == null || !special.test(this.getValueInt())) {
            if (this.text != null) {
                this.setMessage((new TextComponent(String.format(text.get(), this.getValueString()))));
            }
        }
        else {
            this.setMessage((new TextComponent(String.format(specialText.get(), this.getValueString()))));
        }
    }

    @Override
    public void render(PoseStack poseStack, int x, int y, float f) {
        super.render(poseStack, x, y, f);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, x, y);
        }
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int x, int y) {
        var screen = Minecraft.getInstance().screen;
        if (this.tooltip != null && screen != null) {
            screen.renderComponentTooltip(poseStack, List.of(this.tooltip), x, y);
        }
    }

    public void setTooltip(Component component) {
        this.tooltip = component;
    }
}
