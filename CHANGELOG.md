# Changelog

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
