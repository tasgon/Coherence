package org.tasgo.coherence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JOptionPane;

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
	
	public static boolean getYesNo(String query, String title) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
        JOptionPane.showConfirmDialog (null, query, title, dialogButton);
        
        if (dialogButton == JOptionPane.YES_OPTION)
        	return true;
        else
        	return false;
	}
	
	public static boolean getYesNo(String query) {
		return getYesNo(query, "Warning");
	}
	
	public static File getFile(String... paths) {
		StringBuilder sb = new StringBuilder();
		for (String file : paths) {
			sb.append(file + File.separator);
		}
		return new File(sb.toString());
	}
}
