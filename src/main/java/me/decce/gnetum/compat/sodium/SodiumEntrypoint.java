package me.decce.gnetum.compat.sodium;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import me.decce.gnetum.gui.ConfigScreen;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SodiumEntrypoint implements ConfigEntryPoint {
    private final StorageEventHandler handler = () -> {
        Gnetum.getConfig().save();
    };

    private final ResourceLocation enabledOption = ResourceLocation.parse("gnetum:enabled");

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        var optionsBuilder =
                builder.registerOwnModOptions().setColorTheme(builder.createColorTheme().setBaseThemeRGB(2991601)).setName("Gnetum")
                        .setNonTintedIcon(ResourceLocation.parse("gnetum:gnetum.png"));
        var page = builder.createOptionPage().setName(Component.literal("Gnetum"));
        var groupGeneral = builder.createOptionGroup();
        groupGeneral.addOption(builder.createBooleanOption(enabledOption)
                        .setName(Component.translatable("gnetum.config.enabled"))
                        .setTooltip(Component.translatable("gnetum.config.enabled.tooltip"))
                        .setStorageHandler(this.handler)
                        .setBinding((value) -> {
                            if (Gnetum.getConfig().enabled.get() != value) {
                                Gnetum.getConfig().enabled.next();
                            }
                        }, () -> Gnetum.getConfig().enabled.get())
                        .setDefaultValue(true)
                        .setImpact(OptionImpact.HIGH)
                )
                .addOption(builder.createBooleanOption(ResourceLocation.parse("gnetum:show_fps"))
                        .setName(Component.translatable("gnetum.config.showFps"))
                        .setTooltip(Component.translatable("gnetum.config.showFps.tooltip"))
                        .setStorageHandler(this.handler)
                        .setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
                        .setBinding((value) -> {
                            if (Gnetum.getConfig().showHudFps.get() != value) {
                                Gnetum.getConfig().showHudFps.next();
                            }
                        }, () -> Gnetum.getConfig().showHudFps.get())
                        .setDefaultValue(true)
                )
                .addOption(builder.createBooleanOption(ResourceLocation.parse("gnetum:downscale"))
                        .setName(Component.translatable("gnetum.config.downscale"))
                        .setTooltip(Component.translatable("gnetum.config.downscale.tooltip"))
                        .setStorageHandler(this.handler)
                        .setImpact(OptionImpact.LOW)
                        .setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
                        .setBinding((value) -> {
                            if (Gnetum.getConfig().downscale.get() != value) {
                                Gnetum.getConfig().downscale.next();
                            }
                        }, () -> Gnetum.getConfig().downscale.get())
                        .setDefaultValue(false)
                )
                .addOption(builder.createIntegerOption(ResourceLocation.parse("gnetum:pass"))
                        .setName(Component.translatable("gnetum.config.numberOfPasses"))
                        .setTooltip(Component.translatable("gnetum.config.numberOfPasses.tooltip"))
                        .setStorageHandler(this.handler)
                        .setValueFormatter(v -> Component.literal(v + " passes"))
                        .setRange(2, 10, 1)
                        .setDefaultValue(3)
                        .setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
                        .setBinding((i) -> Gnetum.getConfig().numberOfPasses = i, () -> Gnetum.getConfig().numberOfPasses)
                        .setImpact(OptionImpact.MEDIUM)
                )
                .addOption(builder.createIntegerOption(ResourceLocation.parse("gnetum:max_fps"))
                        .setName(Component.translatable("gnetum.config.maxFps"))
                        .setTooltip(Component.translatable("gnetum.config.maxFps.tooltip"))
                        .setStorageHandler(this.handler)
                        .setRange(5, GnetumConfig.UNLIMITED_FPS, 5)
                        .setValueFormatter(v -> v == GnetumConfig.UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", v))
                        .setDefaultValue(60)
                        .setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
                        .setBinding((i) -> Gnetum.getConfig().setMaxFps(i), () -> Gnetum.getConfig().getRawMaxFps())
                        .setImpact(OptionImpact.MEDIUM)
                )
                .addOption(builder.createIntegerOption(ResourceLocation.parse("gnetum:screen_max_fps"))
                        .setName(Component.translatable("gnetum.config.screenMaxFps"))
                        .setTooltip(Component.translatable("gnetum.config.screenMaxFps.tooltip"))
                        .setStorageHandler(this.handler)
                        .setRange(5, GnetumConfig.SCREEN_UNLIMITED_FPS, 5)
                        .setValueFormatter(v -> v == GnetumConfig.SCREEN_UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", v))
                        .setDefaultValue(20)
                        .setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
                        .setBinding((i) -> Gnetum.getConfig().screenMaxFps = i, () -> Gnetum.getConfig().screenMaxFps)
                        .setImpact(OptionImpact.MEDIUM)
                );
        page.addOptionGroup(groupGeneral);
        var groupExternal = builder.createOptionGroup();
        groupExternal.addOption(builder.createExternalButtonOption(ResourceLocation.parse("gnetum:external"))
                .setScreenConsumer(parent -> Minecraft.getInstance().setScreen(new ConfigScreen(parent)))
                .setName(Component.translatable("gnetum.config.external"))
                .setTooltip(Component.translatable("gnetum.config.external.tooltip")));
        page.addOptionGroup(groupExternal);
        optionsBuilder.addPage(page);
    }
}
