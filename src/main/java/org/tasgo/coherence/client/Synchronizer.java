package org.tasgo.coherence.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import org.tasgo.coherence.client.ui.Request;
import org.tasgo.coherence.client.ui.UiProgress;
import org.tasgo.coherence.client.ui.UiYesNo;
import org.tasgo.coherence.client.ui.UiYesNoCallback;
import org.tasgo.coherence.common.Library;
import org.tasgo.coherence.ziputils.UnzipUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * The main class that synchronizes the client with the server
 */
@SideOnly(Side.CLIENT)
public class Synchronizer extends Thread implements UiYesNoCallback {
	private static final Logger logger = LogManager.getLogger("Coherence");
	public String url, address;
	public List<String> modlist;
	private List<String> neededmods;
	private File cohereDir;
	private File localhost = new File("coherence", "localhost");
	private String[] currentMods;
	private boolean updateConfigs;
    private Client client;
    public UiProgress uiProgress;

    public Synchronizer(Client cli) {
        url = cli.coherenceURL;
        address = cli.serverData.serverIP;
        client = cli;
    }

	public void run() {
		if (!Coherence.instance.postCohered) {
            try {
                cohereDir = new File("coherence", this.address);
                modlist = getModList();
                neededmods = getNeededModsList();
            }
            catch (Exception e) {
                e.printStackTrace();
                client.crash(e);
                return;
			}

			StringBuilder sb = new StringBuilder(String.format("The server %s would like to load %d mods.", this.address, neededmods.size()));
            sb.append("\nAre you okay with this?");
			FMLClientHandler.instance().showGuiScreen(new UiYesNo(this, sb.toString()));
		}
	}

	public void onClick(boolean input) {
        if (input)
            return;

        uiProgress = new UiProgress(client.parent, neededmods.size() + 13, true);
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
            client.crash(e);
            return;
        }
	}
	
	private List<String> getModList() throws ClientProtocolException, IOException {
		logger.info("Getting mod list");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		POSTGetter.get(url + "/modlist", stream);
		Type listType = new TypeToken<List<String>>() {}.getType();
		List<String> modlist = new Gson().fromJson(stream.toString(), listType);
		return modlist;
	}
	
	private List<String> getNeededModsList() {
		File modDir = new File(cohereDir, "mods");
		
		StringBuilder logList = new StringBuilder();
		logList.append("Needed mods: ");
		
		if ((!modDir.isDirectory()) || modDir.list().length == 0) { //If folder doesn't exist or is empty, return entire mod list
			modDir.mkdirs();
			for (String mod : modlist) {
				logList.append(mod);
				logList.append(", ");
			}
			logger.info(logList.toString());
			updateConfigs = true;
			return modlist;
		}
			
		List<String> currentMods = Library.listFilenames(modDir, true, true);
		List<String> neededMods = new ArrayList<String>();
		
		for (String mod : modlist) { //Get list of mods that need to be downloaded from the server
			if (!currentMods.contains(mod))
				neededMods.add(mod);
		}
		
		for (String mod : neededMods) { //List all needed mods
			logList.append(mod);
			logList.append(", ");
		}
		
		if (neededMods.isEmpty())
			logList.append("None");
		
		for (String mod : currentMods) { //Delete all mods on client that are not on the server
			if (!modlist.contains(mod)) {
				Library.deleteMod(new File(modDir, mod));
				logger.info("Deleting " + mod + " from local storage");
			}
		}
		logger.info(logList.toString());
		
		if (neededMods.size() > 0) {
			updateConfigs = Request.getYesNo("There are mods to be updated."
					+ "\nWould you like to update the configuration files?");
		}
		
		return neededMods;
	}
	
	private void downloadNeededMods() throws ClientProtocolException, IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HashMap<String, String> map = new HashMap<String, String>();
		final File modDir = new File(cohereDir, "mods");
		if (!modDir.isDirectory())
			modDir.mkdirs();
		
		int size = neededmods.size();
		for (int i = 0; i < size; i++) {
			stream.reset();
			String mod = neededmods.get(i);
			uiProgress.info(String.format("Downloading mod %s (%i/%i)", mod, i + 1, size));
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
		catch (FileExistsException e) {}
		
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
		new UnzipUtility().unzip(configZip.getPath(), new File(".").getPath());
		
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

		Property addressProperty = config.get(config.CATEGORY_GENERAL, "connectToServer", "null");
		addressProperty.set(address);
			
		config.save();
	}
}
