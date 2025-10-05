# Changelog

## 1.3.1

Fixed configuration import.

## 1.3.0

### The Backport

All features from modern branches of Gnetum (2.x and 3.x) have been backported to 1.12.2! This includes:

- A brand-new config system (old config are automatically imported)
- Ability to control the refresh rate of HUDs
- Ability to disable caching for individual HUD element
- A performance analyzer to help you fine-tune Gnetum for optimal performance

### Bug Fixes

- Fixed crash on macOS
- Fixed perspective issue when player is on the rowboat from The Betweenlands mod
- Fixed Dynamic Surroundings compatibility

### Improvements

- Reworked compatibility with ScalingGUIs to be more efficient
- Reworked BetterHud compatibility
- Improved overall mod compatibility

Given the huge size of this release, some bugs and glitches are to be expected - don't hesitate to report issues!

## 1.2.6

- Fixed Fast Framebuffer Blits option 
- Fixed GL state breakage caused by some mods

## 1.2.5

- Fixed warnings in the log (Thanks [WaitingIdly](https://github.com/WaitingIdly))
- Fixed VoxelMap compatibility

## 1.2.4

- Fixed Custom Main Menu rendering

## 1.2.3

- Fixed FontRenderer NPE
- Fixed BetterHUD compatibility

## 1.2.2

- Fixed crash when framebuffer is not supported
- Fixed EventPriority listeners being incorrectly skipped
- Fixed HUD elements sometimes losing transparency (inspired by a similar fix in [Angelica](https://github.com/GTNewHorizons/Angelica/pull/232))
- Fixed crash when using RandomPatches

## 1.2.1

- Fixed crash when Fast Framebuffer Clear is disabled in config
- Fixed FancyMenu rendering
- Fixed visual glitches with FluxLoading mod
- Improved compatibility with InGame Info Reborn mod
- Improved performance when using OptiFine
- Improved performance when using Xaero's Minimap

## 1.2.0

This is a major update to Gnetum, coming with new performance improvements and greatly enhanced mod compatibility.

### Performance

- Added new optimization for framebuffer blitting
  - This was added because Gnetum is incompatible with OptiFine's Fast Render option, and can be considered as a less effective but much more compatible alternative to that option.
  - Unlike Fast Render, which makes the game completely skip the framebuffer copy process (and thus causing a lot of incompatibilities), this optimization makes the copy process much more efficient.
- Added an alternative method for framebuffer clear, which does not introduce additional bind/unbind operations and should be slightly faster

### Fixes

- Fixed HUD updates being delayed by one frame
- Fixed bufferHand option when using shaders
- Fixed compatibility with ScalingGUIs mod
- Fixed compatibility with Thaumcraft mod
- Fixed compatibility with Time is up mod
- Fixed crosshair flickering when it's behind other HUD elements
- Fixed debug pie background color being incorrect
- Fixed framebuffer sometimes not resized properly when switching fullscreen
- Fixed OptiFine's vignette option not respected
- Fixed rendering with pumpkin head
- Fixed blackscreen when falling into the void with shaders enabled

## 1.1.2

- Corrected rendering order
- Fixed Xaero's Minimap ingame waypoint rendering
- Fixed incompatibility with Quark's Better Nausea feature
- Fixed blackscreen when holding a bow/crossbow from the Tinker's Construct mod

## 1.1.1

- Fixed blackscreen (caused by vignette rendering)

## 1.1.0

- Slightly improved performance by removing a redundant method call
- Adjusted rendering order
- Besides the HUD, it is now possible to also buffer the hand, which should give an extra performance boost (currently disabled by default)
- I18n support

## 1.0.0

- Initial release
