package org.tasgo.coherence.client.synchronizer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.client.MCRelauncher;
import org.tasgo.coherence.client.POSTGetter;
import org.tasgo.coherence.client.ui.*;
import org.tasgo.coherence.ziputils.UnzipUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 * The main class that synchronizes the client with the server
 */
@SideOnly(Side.CLIENT)
public class Synchronizer extends Task {
	public String url, address;
	private File localhost = new File("coherence", "localhost");
	private String[] currentMods;
	private boolean updateConfigs;
	private Client client;
    private List<String> neededmods;

    public Synchronizer(Client parent) {
		super(parent);

		this.url = parent.url;
		this.neededmods = cli.neededmods;
    }

	public void run() {
		FMLClientHandler.instance().showGuiScreen(uiProgress);
		try {
			downloadNeededMods();

			uiProgress.info("Restarting Minecraft");
			MCRelauncher.restartMinecraft();
		}
		catch (Exception e) {
			client.crash(e);
        }
	}
	
	private void downloadNeededMods() throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HashMap<String, String> map = new HashMap<String, String>();
		final File modDir = new File(cohereDir, "mods");
		if (!modDir.isDirectory())
			modDir.mkdirs();
		
		int size = neededmods.size();
		for (int i = 0; i < size; i++) {
			stream.reset();
			String mod = neededmods.get(i);
			uiProgress.info(String.format("Downloading mod %s (%d/%d)", mod, i + 1, size));
			map.clear(); map.put("mod", mod);
			POSTGetter.get(url + "/mod", map, stream);
			
			File modFile = new File(modDir, mod);
			//new File(modFile.getAbsolutePath().substring(0, modFile.getAbsolutePath().lastIndexOf(File.separator))).mkdirs(); //Old and stupid
			new File(FilenameUtils.getFullPath(modFile.getAbsolutePath())).mkdirs();
			FileOutputStream fstream = new FileOutputStream(modFile);
			fstream.write(stream.toByteArray());
			fstream.close();
		}
	}
}
