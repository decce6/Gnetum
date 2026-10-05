package me.decce.gnetum;

public class Constants {
	public static final String MOD_ID = "gnetum";
	public static final String MOD_VERSION_SHORT = /*$ mod_version_short*/"4.6.1";
	public static final String HAND_ELEMENT = "hand";
	public static final String UNKNOWN_ELEMENTS = "_unknown_"; // indicates an unknown element, e.g. the HUDs which render through Mixins - not cached by default
	public static final String UNKNOWN_LISTENERS = "_unknown_listeners_"; // indicates an unknown HUD listener, e.g. a RenderGuiEvent listener on NeoForge which Gnetum cannot resolve its modid - cached by default
	public static final String DEBUG_OVERLAY = "debug_overlay";
	public static final int UNLIMITED_FPS = 125; // when maxFps is set to this value it means unlimited
	public static final int SCREEN_UNLIMITED_FPS = 65; // when screenMaxFps is set to this value it means unlimited
}
