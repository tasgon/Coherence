package org.tasgoon.coherence.client.ui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import org.tasgoon.coherence.Coherence;

/**
 * Created by Tasgo on 1/21/2016.
 */


public class UiConfig extends GuiConfig {
    public UiConfig(GuiScreen parent) {
        super(parent,
                new ConfigElement(
                    Coherence.instance.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                    Coherence.MODID,
                    false,
                    false,
                    "Coherence Configuration");
        titleLine2 = "General Configuration";
    }

    @Override
    public void initGui() {
        // You can add buttons and initialize fields here
        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // You can process any additional buttons you may have added here
        super.actionPerformed(button);
    }
}

