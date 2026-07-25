package me.decce.gnetum.gui;

import me.decce.gnetum.gui.widgets.MultiLineTextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

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
        MultiLineTextWidget stringWidget = new MultiLineTextWidget(width / 2, height / 2, 300, font.lineHeight * 4, txt);
        Button btnYes = new Button(width / 2 - w - m, height / 2 + 90, w, h, new TranslatableComponent("gui.yes"), b -> onYes());
        Button btnNo = new Button(width / 2 + m, height / 2 + 90, w, h, new TranslatableComponent("gui.no"), b -> onNo());

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
