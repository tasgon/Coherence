package org.tasgoon.coherence.client.handlers;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.tasgoon.coherence.client.ui.UiMultiplayer;

/**
 * Created by Tasgo on 1/17/16.
 */
public class MultiplayerHandler {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void checkMultiplayer(GuiOpenEvent event) {
        System.out.println("Checking for multiplayer.");
        if (event.getGui() instanceof GuiMultiplayer)
            event.setGui(new UiMultiplayer(new GuiMainMenu()));
    }
}
