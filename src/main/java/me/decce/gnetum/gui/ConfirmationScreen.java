package me.decce.gnetum.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class ConfirmationScreen extends BaseScreen {
    private Runnable action;
    private Supplier<Screen> parentYes;
    private Supplier<Screen> parentNo;

    public ConfirmationScreen(Supplier<Screen> parentYes, Supplier<Screen> parentNo, Runnable action) {
        super();
        this.action = action;
        this.parentYes = parentYes;
        this.parentNo = parentNo;
    }

    @Override
    protected void rebuild() {
        super.rebuild();

        int w = 100;
        int h = 20;
        int m = 20;

        String txt = I18n.get("gnetum.config.confirmReset");
        MultiLineTextWidget stringWidget = new MultiLineTextWidget(width / 2 - 150, height / 2 - font.lineHeight * 4, Component.literal(txt), font);
        stringWidget.setMaxWidth(300);
        Button btnYes = Button.builder(Component.translatable("gui.yes"), b -> onYes()).pos(width / 2 - w - m, height / 2 + 90).size(w, h).build();
        Button btnNo = Button.builder(Component.translatable("gui.no"), b -> onNo()).pos(width / 2 + m, height / 2 + 90).size(w, h).build();

        this.addRenderableWidget(stringWidget);
        this.addRenderableWidget(btnYes);
        this.addRenderableWidget(btnNo);
    }

    protected void onYes() {
        this.action.run();
        Minecraft.getInstance().setScreen(parentYes.get());
    }

    protected void onNo() {
        Minecraft.getInstance().setScreen(parentNo.get());
    }
}
