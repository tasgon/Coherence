package org.tasgo.coherence.client.synchronizer;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;
import org.tasgo.coherence.client.MCRelauncher;
import org.tasgo.coherence.client.POSTGetter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


/**
 * The main class that synchronizes the client with the server
 */
@SideOnly(Side.CLIENT)
public class Synchronizer extends Task {

    public Synchronizer(Client parent) {
		super(parent);
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
		final File modDir = new File(synchronizationData.cohereDir, "mods");
		if (!modDir.isDirectory())
			modDir.mkdirs();
		
		int size = synchronizationData.neededmods.size();
		for (int i = 0; i < size; i++) {
			stream.reset();
			String mod = synchronizationData.neededmods.get(i);
			uiProgress.info(String.format("Downloading mod %s (%d/%d)", mod, i + 1, size));
			map.clear(); map.put("mod", mod);
			POSTGetter.get(synchronizationData.url + "/mod", map, stream);
			
			File modFile = new File(modDir, mod);
			logger.debug("Saving mod " + mod + " to file " + modFile.getAbsolutePath());
			new File(FilenameUtils.getFullPath(modFile.getAbsolutePath())).mkdirs();
			FileOutputStream fstream = new FileOutputStream(modFile);
			fstream.write(stream.toByteArray());
			fstream.close();
		}
	}
}
