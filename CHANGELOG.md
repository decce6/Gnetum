# Changelog

## 4.5.5

- Fixed game crash due to scissors when using downscaled HUD framebuffers (Thanks @Wyvest!)
- Fixed game crash when the config file is corrupted

## 4.5.4

- Fixed HUD flickering when opening chat screen on 1.21.1
- Fixed duplicate modded HUD rendering on 1.21.1
- Fixed armor bar / air bar height on NeoForge

## 4.5.3

- Fixed "Resource reload failed" error during launch

## 4.5.2

- Caching for "Unknown Elements" is now disabled by default to prevent rendering issues
  - These elements are rendered at arbitrary points, and there is no way for Gnetum to cache them reliably without causing issues. For example, some mods mixin into specific methods to render their HUDs. Mods that use this approach are encouraged to switch to the appropriate APIs (`HudElementRegistry` from Fabric API, and `RegisterGuiLayersEvent`/`RenderGuiEvent`/`RenderGuiLayerEvent` on NeoForge) instead.
- Added support for the new NeoForge mods list screen

## 4.5.1

- Fixed a crash when opening config screen with specific mods

## 4.5.0

- Added support for 1.21.4 and 1.21.1 Fabric
- Mods that use mixins to render HUDs can now be cached (controlled by the "Unknown Elements" option)

## 4.4.1

- Added 26.2 NeoForge support
- Fixed subtitles HUD flickering

## 4.4.0

- Added 26.2 support
- Fixed minor HUD offset when the window width / height is not a multiple of the GUI scale
- Slightly improved performance when Downscale HUD Framebuffer is not enabled
- Reimplemented Fast Framebuffer Blitting
- [Fabric] Marked Fabric API as required
- Added zh_cn.json (Thanks @AlanChrisa!)

## 4.3.2

- Fixed player locator bar

## 4.3.1

- Fixed screenshot breakage
- Fixed Xaero's Minimap compatibility on 26.1+

## 4.3.0

- Fixed flickering when opening/closing chat screen
- Improved compatibility with Xaero's Minimap
  - In-game waypoints are no longer cached
  - The minimap now can be cached on Fabric (previously it was only possible on NeoForge)
  - Fixed flickering with bossbar / status effects
- Fixed performance regressions in 4.2.1
- Fixed crosshair flickering when cache is force-enabled
- Added 26.1 support

## 4.2.1

- Improved transition when opening/closing chat screen
- Fixed vignette being rendered above HUD instead of behind it
- Fixed crash at launch with F3 menu open
- Fixed crash when reducing the number of passes

## 4.2.0

- Further improved performance
- Added a new "Fast Framebuffer Blitting" option that improves the GPU performance by discarding pixels that are fully transparent
- Fixed delta tracker (animation interpolation)

## 4.1.1

- Fixed F3 menu appearing more "dense" with caching enabled
- [Fabric] Fixed HUDs sometimes rendering in wrong orders

## 4.1.0

Three options have been ported from prior versions of Gnetum:

- **Downscale HUD Framebuffer**: uses a smaller resolution for the HUD framebuffer, which may improve performance in GPU-bound scenarios
- **Max HUD Framerate (GUI)**: allows using a lower HUD framerate limit when a GUI screen is open, improving performance in GUIs
- **Hand Caching**: allows caching the hand (currently disabled by default)

Additionally, these changes have been made:

- **Debug Overlay** caching has been added (currently disabled by default)
- Fixed HUDs not rendering with Very Many Players mod

## 4.0.0

This is a major update to Gnetum, rewritten for multi-version and loader support.

### Highlights

- Migrated to stonecutter for easier maintenance of support for multiple versions and loaders
- Updated to Minecraft 1.21.11
- Fabric is now supported

### HUD Ordering & Distribution

Previously, Gnetum had an extensive configuration system that allowed arbitrary change of rendering orders. This, while being flexible, was error-prone in that rendering order was not guaranteed, resulting in potential rendering artifacts.

Starting from this version, the original rendering order of elements is strictly unchanged; it is still possible to disable caching of individual elements. Additionally, Gnetum can now automatically distribute elements evenly over passes based on their rendering times, improving FPS stability.

### Config

This versions makes use of Sodium's Config API, meaning you can now configure Gnetum from the Video Settings screen, as long as you have Sodium installed.
