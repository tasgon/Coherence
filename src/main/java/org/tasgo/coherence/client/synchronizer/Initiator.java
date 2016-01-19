package org.tasgo.coherence.client.synchronizer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.client.CoherenceData;
import org.tasgo.coherence.client.POSTGetter;
import org.tasgo.coherence.client.ui.UiError;
import org.tasgo.coherence.client.ui.UiProgress;
import org.tasgo.coherence.client.ui.UiYesNo;
import org.tasgo.coherence.common.Library;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class Initiator extends Thread {
	
	private static final Logger logger = LogManager.getLogger("Coherence");
    private UiProgress uiProgress;
	public GuiScreen parent;
    public ServerData serverData;
    public String address, url;
    public File cohereDir;
    public List<String> modlist;
    public List<String> neededmods;
    public boolean updateConfigs;

    public Initiator(GuiScreen parent, ServerData serverData) {
        if (Coherence.instance.postCohered)
            return;

        this.parent = parent;
        this.serverData = serverData;
        this.address = serverData.serverIP;
        this.url = CoherenceData.getCoherenceURL(address);
        this.uiProgress = new UiProgress(parent, 2);
    }

    public void run() {
        if (Coherence.instance.postCohered) {
            FMLClientHandler.instance().connectToServer(parent, serverData);
            return;
        }

        try {
            cohereDir = new File("coherence", this.address);
            modlist = getModList();
            neededmods = getNeededModsList();
        }
        catch (Exception e) {
            e.printStackTrace();
            UiError.crash(parent, e);
            return;
        }

        StringBuilder sb = new StringBuilder(String.format("The server %s would like to load %d mods.", this.address, neededmods.size()));
        sb.append("\nAre you okay with this?");
        FMLClientHandler.instance().showGuiScreen(new UiYesNo(new Transitioner(this), sb.toString()));
	}

    private List<String> getModList() throws ClientProtocolException, IOException {
        uiProgress.info("Getting mod list");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        POSTGetter.get(url + "/modlist", stream);
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> modlist = new Gson().fromJson(stream.toString(), listType);
        return modlist;
    }

    private List<String> getNeededModsList() {
        uiProgress.info("Getting needed mods");
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

        /*if (neededMods.size() > 0) {
            updateConfigs = Request.getYesNo("There are mods to be updated."
                    + "\nWould you like to update the configuration files?");
        }*/

        return neededMods;
    }
}
