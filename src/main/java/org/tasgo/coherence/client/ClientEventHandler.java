package org.tasgo.coherence.client;

import org.tasgo.coherence.client.ui.UpdateGui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientEventHandler {
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void checkMainMenu(GuiOpenEvent event) {
		if (event.gui instanceof GuiMainMenu) {
			event.gui = new UpdateGui(event.gui, "1.7b02");
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}
	
}
