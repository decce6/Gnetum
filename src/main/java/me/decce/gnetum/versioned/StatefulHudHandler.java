package me.decce.gnetum.versioned;

//? >=1.21.10 {
import me.decce.gnetum.mixins.GuiGraphicsAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.ScreenArea;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

import java.util.LinkedList;
import java.util.Queue;

public class StatefulHudHandler {
	public static final Queue<ScreenArea> deferredSubmissions = new LinkedList<>();

	public static void submitLater(ScreenArea state) {
		deferredSubmissions.add(state);
	}

	public static void dropDeferredSubmission() {
		deferredSubmissions.clear();
	}

	public static void performDeferredSubmission(GuiGraphics guiGraphics) {
		var state = ((GuiGraphicsAccessor)guiGraphics).getGuiRenderState();
		deferredSubmissions.forEach(submission -> {
			//TODO: optimize ?
			switch (submission) {
				case GuiElementRenderState gui -> state.submitGuiElement(gui);
				case GuiTextRenderState text -> state.submitText(text);
				case PictureInPictureRenderState pip -> state.submitPicturesInPictureState(pip);
				case GuiItemRenderState item -> state.submitItem(item);
				case null ->
						throw new NullPointerException("Submission");
				default ->
						throw new IllegalStateException("Unknown submission type " + submission.getClass().getName());
			}
		});
		deferredSubmissions.clear();
	}
}
//?}
