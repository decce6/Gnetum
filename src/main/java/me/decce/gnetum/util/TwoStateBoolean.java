package me.decce.gnetum.util;

import net.minecraft.client.resources.language.I18n;

public class TwoStateBoolean implements AnyBoolean {
    public AnyBooleanValue value;

    public TwoStateBoolean() {
        this(AnyBooleanValue.OFF);
    }

    public TwoStateBoolean(AnyBooleanValue value) {
        if (value == AnyBooleanValue.AUTO) throw new RuntimeException("Unexpected value for TwoStateBoolean!");
        this.value = value;
    }

    @Override
    public boolean get() {
        return value == AnyBooleanValue.ON;
    }

    @Override
    public void next() {
        value = switch (value) {
            case ON -> AnyBooleanValue.OFF;
            case OFF -> AnyBooleanValue.ON;
            default -> throw new RuntimeException("Unexpected value for TwoStateBoolean!");
        };
    }

    @Override
    public String text() {
        return I18n.get((switch (this.value) {
            case OFF -> I18n.get("options.off");
            case ON -> I18n.get("options.on");
            default -> throw new RuntimeException("Unexpected value for TwoStateBoolean!");
        }));
    }
}
