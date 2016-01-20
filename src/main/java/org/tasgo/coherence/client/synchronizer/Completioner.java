package org.tasgo.coherence.client.synchronizer;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.client.MCRelauncher;
import org.tasgo.coherence.client.POSTGetter;
import org.tasgo.coherence.ziputils.UnzipUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The class that completes the synchronization with the server.
 */
public class Completioner extends Task {
    private File localhost = new File("coherence", "localhost");

    public Completioner(Client client) {
        super(client);
    }
    
    public void run() {
        FMLClientHandler.instance().showGuiScreen(uiProgress);
        try {
            getConfigs();
            moveMods();
            writeConfigFile();

            uiProgress.info("Restarting Minecraft");
            MCRelauncher.restartMinecraft();
        }
        catch (Exception e) {
            client.crash(e);
        }
    }

    private void getConfigs() throws IOException {
        File configZip;
        File customConfig = new File(synchronizationData.cohereDir, "customConfig.zip");
        File localConfig = new File(localhost, "config");

        try {
            FileUtils.moveDirectory(new File("config"), localConfig); //Move config folder
        }
        catch (FileExistsException ignored) {}

        if (synchronizationData.updateConfigs || !customConfig.exists()) { //Download the new config if updateConfigs is true
            uiProgress.info("Downloading configs", 3);

            configZip = new File(synchronizationData.cohereDir, "config.zip");
            if (configZip.exists())
                configZip.delete();
            configZip.createNewFile();

            FileOutputStream fstream = new FileOutputStream(configZip);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            POSTGetter.get(synchronizationData.url + "/config", stream);
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
        File cohereMods = new File(synchronizationData.cohereDir, "mods");
        FileUtils.copyDirectory(cohereMods, modDir);
    }

    private void writeConfigFile() {
        uiProgress.info("Writing configuration file for persistence", 2);
        Configuration config = new Configuration(Coherence.instance.configFile);

        config.load();

        Property addressProperty = config.get(Configuration.CATEGORY_GENERAL, "connectToServer", "null");
        addressProperty.set(synchronizationData.serverData.serverIP);

        config.save();
    }
}
