package org.tasgo.coherence.client;

import org.tasgo.coherence.client.ui.UpdateGui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;

public class ClientEventHandler {
	
	//@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void checkMainMenu(GuiOpenEvent event) {
		System.out.println("Gui opened: " + event.gui.getClass().getName());
		if (event.gui instanceof GuiMainMenu) {
			event.gui = new UpdateGui(event.gui, "1.7b02");
		}
	}
	
}
