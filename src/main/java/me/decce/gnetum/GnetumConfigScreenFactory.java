package me.decce.gnetum;

import me.decce.gnetum.gui.ConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class GnetumConfigScreenFactory implements IConfigScreenFactory {
    @Override
    public Screen createScreen(ModContainer modContainer, Screen parent) {
        return new ConfigScreen(parent);
    }
}
