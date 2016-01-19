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
public class Synchronizer extends Thread {
	private static final Logger logger = LogManager.getLogger("Coherence");
	public String url, address;
	private File localhost = new File("coherence", "localhost");
	private String[] currentMods;
	private boolean updateConfigs;
    private GuiScreen parent;
    public UiProgress uiProgress;
    private File cohereDir;
    public List<String> modlist;
    private List<String> neededmods;

    public Synchronizer(Initiator cli) {
        url = cli.url;
        address = cli.serverData.serverIP;
        cohereDir = cli.cohereDir;
        modlist = cli.modlist;
        neededmods = cli.neededmods;
        updateConfigs = cli.updateConfigs;
        parent = cli.parent;
    }

	public void run() {
		uiProgress = new UiProgress(parent, neededmods.size() + 13, true);
		FMLClientHandler.instance().showGuiScreen(uiProgress);
		try {
			downloadNeededMods();
			getConfigs();
			moveMods();
			writeConfigFile();

			uiProgress.info("Restarting Minecraft");
			MCRelauncher.restartMinecraft();
		}
		catch (Exception e) {
			e.printStackTrace();
			UiError.crash(parent, e);
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
	
	private void getConfigs() throws IOException {
		File configZip;
		File customConfig = new File(cohereDir, "customConfig.zip");
		File localConfig = new File(localhost, "config");
		
		try {
			FileUtils.moveDirectory(new File("config"), localConfig); //Move config folder
		}
		catch (FileExistsException ignored) {}
		
		if (updateConfigs || !customConfig.exists()) { //Download the new config if updateConfigs is true
			uiProgress.info("Downloading configs", 3);
			
			configZip = new File(cohereDir, "config.zip");
			if (configZip.exists())
				configZip.delete();
			configZip.createNewFile();
			
			FileOutputStream fstream = new FileOutputStream(configZip);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			POSTGetter.get(url + "/config", stream);
			stream.writeTo(fstream);
			fstream.close();
			stream.close();
		}
		else { //Otherwise, use the old configs.
			uiProgress.info("Using previous configs", 3);
			configZip = customConfig;
		}
		
		logger.info("Extracting configs", 2);
		UnzipUtility.unzip(configZip.getPath(), new File(".").getPath());
		
		FileUtils.deleteQuietly(Coherence.instance.configFile); //Make sure that Coherence config carries over
		FileUtils.copyFile(new File(localConfig, Coherence.instance.configFile.getName()), Coherence.instance.configFile);
	}
	
	private void moveMods() throws IOException {
        uiProgress.info("Moving mods", 5);
		File modDir = new File("mods"); File curMods = new File(localhost, "mods");
		FileUtils.copyDirectory(modDir, curMods);
		File cohereMods = new File(cohereDir, "mods");
		FileUtils.copyDirectory(cohereMods, modDir);
	}
	
	private void writeConfigFile() {
		uiProgress.info("Writing configuration file for persistence", 2);
		Configuration config = new Configuration(Coherence.instance.configFile);

		config.load();

		Property addressProperty = config.get(Configuration.CATEGORY_GENERAL, "connectToServer", "null");
		addressProperty.set(address);
			
		config.save();
	}
}
