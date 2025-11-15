# Changelog

## 3.2.1

- Added compatibility with Ping Wheel mod
- Fixed depth buffer

## 3.2.0

**This version now allows all modded HUDs to be cached, greatly improving performance. If this is not desired, go to "Modded Elements" in the config and set "Other Mods" to "OFF".**

- Caching of unknown modded HUDs is now allowed (shows up as "Other Mods" in "Modded Elements")
- Fixed partial ticks
- Fixed Xaero's Minimap flickering

## 3.1.2

- Fixed PoseStack crashes

## 3.1.1

- Fixed HUD flickering in certain scenarios

## 3.1.0

- Slightly improved HUD testing performance
- Fixed modid lookup
- Improved mod compatibility
- Changed the default number of passes to 3 (was 4)

## 3.0.3

- Fixed sky glitches in certain daytimes

## 3.0.2

- Fixed the issue where, when the hotbar contains enchanted items, caching for the hotbar gets disabled automatically. This fix means better performance when such items exist in the hotbar.
- Fixed entity icons breakage on Xaero's Minimap
- Fixed the issue where some settings cannot be saved
- Improved compatibility with mods that use special blending functions

## 3.0.1

- Implemented caching for chat screen
- Fixed HUD not hiding immediately when pressing F1
- Fixed settings not persisting across game starts in some cases
- Fixed some depth issues
- Fixed delta tracker for HUDs
- Fixed HUD not rendering when ImmediatelyFast is not installed
- Improved mod compatibility

## 3.0.0

- Updated to 1.21.1
- Fixed HUD flickering when resizing framebuffer