## Gnetum

A Minecraft mod that improves performance by distributing HUD updates over multiple frames.

**Requires [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixin-booter)!**

### Overview

When Gnetum is installed, HUD rendering is divided into multiple "passes." Only one pass is rendered each frame, which should provide a decent FPS boost, especially when many HUD elements are present.

Currently the HUD rendering passes are configured as follows:

- **Pass 0** renders the parts of the HUD that are not included in the following passes, and, if enabled, the hand.
- **Pass 1** renders some of the texts on the HUD, including but not limited to the F3 debug info and the HUD added by The One Probe.
- **Pass 2** renders the hotbar. This is because the items in the hotbar are pretty slow to render, especially when they use custom models.
- **Pass 3** renders some of the modded HUD elements, e.g. the minimap provided by JourneyMap

### FAQs

#### How is this different from [StellarCore](https://www.curseforge.com/minecraft/mc-mods/stellarcore)'s HUD Caching feature? 

Both mods make the HUD render at a lower frame rate, but due to the way StellarCore's HUD Caching feature works, it does not improve the minimum FPS, and it is the minimum FPS that decides how smooth the game feels. This mod improves both max and min FPS.

StellarCore is still recommended; just make sure to disable their HUD Caching feature.

#### Can I limit the HUD FPS to a certain value (e.g. 20FPS)?

Due to the way this mod works, it is not possible to limit the HUD FPS to a certain value. In the future, however, you might be able to configure how often the HUD gets updated.

Currently the FPS of the HUD is equal to one quarter of the game FPS, which means for example if you have 120FPS, the HUD will render at 30FPS.

### Compatibility

⚠️ OptiFine: make sure "Fast Render" is disabled (this mod would do nothing otherwise.)

⚠️ StellarCore: make sure their HUD Caching feature is disabled.

### Credits

This mod is inspired by the [HUDCaching](https://github.com/Moulberry/MCHUDCaching) mod by [Moulberry](https://github.com/moulberry), and a small portion of code is used under the CC-BY 3.0 [license](https://github.com/Moulberry/MCHUDCaching/blob/master/LICENSE).