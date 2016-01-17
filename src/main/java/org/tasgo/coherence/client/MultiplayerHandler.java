package org.tasgo.coherence.client;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.tasgo.coherence.client.ui.UiMultiplayer;

/**
 * Created by Tasgo on 1/17/16.
 */
public class MultiplayerHandler {
    @EventHandler
    //@SideOnly(Side.CLIENT)
    public void checkMultiplayer(GuiOpenEvent event) {
        System.out.println("Checking for multiplayer.");
        event.gui = new UiMultiplayer(new GuiMainMenu());
    }
}
