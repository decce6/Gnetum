package me.decce.gnetum.gui.widgets;

import me.decce.gnetum.util.AnyBoolean;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.util.function.Supplier;

public class ToggleButton extends Button {
    private Supplier<String> text;
    private Supplier<String> tooltip;
    private AnyBoolean value;

    public ToggleButton(int left, int top, int width, int height, AnyBoolean value, Supplier<String> text) {
        super(left, top, width, height, Component.empty(), btn -> {}, Button.DEFAULT_NARRATION);
        this.text = text;
        this.value = value;
        this.updateMessage();
    }

    public void setTooltip(Supplier<String> tooltip) {
        this.tooltip = tooltip;
        this.updateTooltip();
    }

    @Override
    public void onPress() {
        super.onPress();
        this.next();
        this.updateMessage();
    }

    protected void next() {
        value.next();
    }

    private void updateMessage() {
        this.setMessage(Component.literal(String.format(text.get(), value.text())));
        this.updateTooltip();
    }

    private void updateTooltip() {
        if (tooltip == null) {
            super.setTooltip(null);
            return;
        }
        String str = tooltip.get();
        if (str != null && !str.isEmpty()) {
            super.setTooltip(Tooltip.create(Component.literal(str)));
            super.setTooltipDelay(Duration.ZERO);
        }
        else super.setTooltip(null);
    }
}
