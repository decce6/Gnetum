# Changelog

## 2.1.3

- Fixed the issue where, when the hotbar contains enchanted items, caching for the hotbar gets disabled automatically. This fix means better performance when such items exist in the hotbar.
- Fixed entity icons breakage on Xaero's Minimap
- Improved compatibility with mods that use special blending functions

## 2.1.2

- Fixed HUD flickering when resizing framebuffer
- Fixed some depth issues
- Fixed delta tracker for HUDs
- Improved mod compatibility

## 2.1.1

- Improved compatibility with mods that directly inject into ```ForgeGui.render```
- Fixed delta frame time for HUDs

## 2.1.0

This release brings massive improvements to the compatibility department.

### Highlights

- Fixed compatibility with a lot of mods, including but not limited to [Cobblemon](https://www.curseforge.com/minecraft/mc-mods/cobblemon), [Overflowing Bars](https://www.curseforge.com/minecraft/mc-mods/overflowing-bars), and [Quark](https://www.curseforge.com/minecraft/mc-mods/quark)
- Implemented the mechanism to automatically detect incompatible blending functions and disable caching for the corresponding element (can be force-enabled by changing the setting from ```AUTO``` to ```ON```)

### Other Improvements

- The pumpkin head overlay is now cached to improved performance
- Improved handling of some vanilla elements
- The category "Vanilla Elements" was renamed to "Named Elements" to avoid confusion
- Fixed the issue where the hand sometimes disappears with hand caching enabled
- Raised the default value of max HUD FPS to 60. If you prefer better performance over smoother HUD animations you can manually set this lower in the config
- Added a button to reset configurations to default

## 2.0.1

- Fixed some modded elements being rendered twice (which resulted in Jade looking like it lost transparency)
- Fixed max FPS option not working
- Implemented hand caching that further improves performance when enabled (experimental, currently must be manually enabled at Vanilla Elements -> Hand by changing ```AUTO``` to ```ON```)

## 2.0.0

### Highlights

- Minecraft 1.20.1 support!
- A brand new configuration screen that allows advanced players and modpack makers to fine-tune Gnetum for optimal performance, accessible via keybind (bound to ```End`` by default)

### New in this version

- Rewrote the mod for Minecraft 1.20.1
- You can now configure caching settings for individual vanilla elements and modded elements
- Added option to show HUD FPS in F3
- Added Number of Passes option
- Added HUD Framerate Limit option