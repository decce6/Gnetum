package me.decce.gnetum.util;

import net.minecraft.client.resources.language.I18n;

public class TriStateBoolean implements AnyBoolean {
    public AnyBooleanValue value;

    public transient boolean defaultValue;

    public TriStateBoolean() {
        this(AnyBooleanValue.AUTO);
    }

    public TriStateBoolean(AnyBooleanValue value) {
        this(value, true);
    }

    public TriStateBoolean(AnyBooleanValue value, boolean defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public boolean get() {
        return value == AnyBooleanValue.AUTO ? defaultValue : value == AnyBooleanValue.ON;
    }

    @Override
    public void next() {
        this.value = switch (this.value) {
            case AUTO -> AnyBooleanValue.ON;
            case ON -> AnyBooleanValue.OFF;
            case OFF -> AnyBooleanValue.AUTO;
        };
    }

    @Override
    public String text() {
        return value.text().getString();
    }

    public String effectiveText() {
        return I18n.get(this.get() ? "options.on" : "options.off");
    }

    public String tooltip() {
        return this.tooltip(this.value);
    }

    public String tooltip(AnyBooleanValue value) {
        return switch (value) {
            case AUTO -> I18n.get( "gnetum.config.tooltip.auto", effectiveText());
            case OFF -> I18n.get("gnetum.config.tooltip.off");
            case ON -> I18n.get("gnetum.config.tooltip.on");
        };
    }
}
