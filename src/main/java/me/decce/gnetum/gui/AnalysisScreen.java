package me.decce.gnetum.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.PerformanceAnalyzer;
import me.decce.gnetum.gui.widgets.MultiLineTextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;

public class AnalysisScreen extends BaseScreen {
    public final PerformanceAnalyzer.Result analysis;
    private MultiLineTextWidget txt;

    public AnalysisScreen(PerformanceAnalyzer.Result analysis) {
        this.analysis = analysis;
    }

    @Override
    public void close() {
        // do not call super here because there is no need to save config
        Minecraft.getInstance().setScreen(super.parent);
    }

    @Override
    protected void rebuild() {
        super.rebuild();

        String body;
        if (analysis.getMessages().isEmpty()) {
            body = I18n.get("gnetum.config.analysis.good");
        }
        else {
            body = String.join("\n\n", analysis.getMessages());
        }

        StringBuilder data = new StringBuilder();
        long[] durations = analysis.getDurations();
        if (durations == null) {
            data.append(I18n.get("gnetum.config.analysis.failedDuration"));
        }
        else {
            data.append(I18n.get("gnetum.config.analysis.detailDuration"));
            for (int i = 1; i < durations.length; i++) {
                data.append("\n");
                data.append("Pass [").append(i).append("] - ").append(String.format("%.2f", durations[i] / 1000d)).append("μs");
            }
        }

        String str = body + "\n\n" + data.toString();

        int w = 300;
        int h = 150;
        txt = new MultiLineTextWidget( width / 2, height / 2, w, h, str);
        // txt.setMaxWidth(w);
        this.addRenderableWidget(txt);

        super.addDoneButton();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        String title = I18n.get(analysis.getIcon() == PerformanceAnalyzer.ResultIcon.TICK ? "gnetum.config.analysis.title.good" : "gnetum.config.analysis.title.suboptimal");
        int titleWidth = font.width(title);
        int xfont = width / 2 - titleWidth / 2 + 8 + 2;
        int xicon = xfont - 4 - 16;
        int y = height / 2 - 90;

        RenderSystem.setShaderTexture(0, analysis.getIcon().icon());
        GuiComponent.blit(poseStack, xicon, y - 8, 16, 16, 0f, 0f, 16, 16, 16, 16);

        GuiComponent.drawString(poseStack, font, title, xfont, y, 0xFFFFFF);
    }
}
