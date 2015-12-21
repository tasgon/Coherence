package org.tasgo.coherence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.launchwrapper.Launch;

public class Library {
	private static final Logger logger = LogManager.getLogger("Coherence");
	
	public static void downloadFile(URL url, File file) throws IOException {
		FileUtils.deleteQuietly(file);
		logger.info("Downloading " + url.toString() + " to " + file.getPath());
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
	/*public static boolean getYesNo(String query) {
		System.out.println("Init show yes no.");
		GuiScreen yesNo = new GuiYesNo(new GuiYesNoCallback() {
			@Override
			public void confirmClicked(boolean res, int val) {
				System.out.println(res);
			}
		}, query, "", 0);
		FMLClientHandler.instance().showGuiScreen(yesNo);
		while (true) {
			Minecraft.getMinecraft().currentScreen.updateScreen();
		}
		//return false;
	}*/
}
