# Changelog

## 1.0.0

- Initial release

## 1.1.0

- Slightly improved performance by removing a redundant method call
- Adjusted rendering order
- Besides the HUD, it is now possible to also buffer the hand, which should give an extra performance boost (currently disabled by default)
- I18n support

## 1.1.1

- Fixed blackscreen (caused by vignette rendering)

## 1.1.2

- Corrected rendering order
- Fixed Xaero's Minimap ingame waypoint rendering
- Fixed incompatibility with Quark's Better Nausea feature
- Fixed blackscreen when holding a bow/crossbow from the Tinker's Construct mod

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
- Fixed blackscreen when falling into the void