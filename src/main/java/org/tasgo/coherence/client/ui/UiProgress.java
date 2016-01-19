package org.tasgo.coherence.client.ui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.client.ProgressBar;

import java.text.DecimalFormat;

/**
 * Progress screen rendered as a ui
 */
public class UiProgress extends GuiScreen implements ProgressBar {
    private static final Logger logger = LogManager.getLogger("Coherence Loader");

    GuiScreen parent;
    private int totalSteps;
    private boolean autoLog = false;
    private int steps = 0;
    private String message = "";

    public UiProgress(GuiScreen parent, int totalSteps, boolean autoLog) {
        this.parent = parent;
        this.totalSteps = totalSteps;
        this.autoLog = autoLog;
    }

    public UiProgress(GuiScreen parent, int totalSteps) {
        this.parent = parent;
        this.totalSteps = totalSteps;
    }

    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        GuiButton cancelButton = new GuiButton(0, this.width / 2 - 100, (int) (this.height / 1.25), I18n.format("gui.cancel", new Object[0]));
        cancelButton.enabled = false;
        this.buttonList.add(cancelButton);
    }

    public void drawScreen(int x, int y, float renderPartialTicks) {
        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRendererObj, message, width / 2, height / 4, 16777215);

        this.drawGradientRect(10, height / 3, width - 10, height / 3 + 10, -10000000, -10000000);
        this.drawGradientRect(10, height / 3, (int) (10 + (width - 20) * getPercentage()), height / 3 + 10, -99000000, -99000000);
        this.drawCenteredString(this.fontRendererObj, getFormattedPercentage("#.0") + "%", width / 2, height / 3 + 1, 16777215);

        super.drawScreen(x, y, renderPartialTicks);
    }

    public void updateScreen() {
        super.updateScreen();
    }

    public void info(String message) {
        info(message, 1);
    }

    public void info(String message, int steps) {
        increment(steps);
        this.message = message;
        if (autoLog)
            logger.info(this.message);
    }

    @Override
    public void increment(int stepsToIncrement) {
        steps += stepsToIncrement;
    }

    @Override
    public int getTotalSteps() {
        return totalSteps;
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public double getPercentage() {
        return (double) steps / (double) totalSteps;
    }

    @Override
    public String getFormattedPercentage(String formatting) {
        return new DecimalFormat(formatting).format(getPercentage() * 100);
    }

}
