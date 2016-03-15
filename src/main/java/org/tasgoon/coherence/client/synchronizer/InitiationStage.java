package org.tasgoon.coherence.client.synchronizer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.http.client.ClientProtocolException;
import org.tasgoon.coherence.client.POSTGetter;
import org.tasgoon.coherence.common.Library;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * The class that performs the 'handshake' with the server.
 */
@SideOnly(Side.CLIENT)
public class InitiationStage extends Task {

    public InitiationStage(Client parent) {
        super(parent);
    }

    public void run() {
        try {
            getModList();
            getNeededModsList();
        }
        catch (Exception e) {
            synchronizationData.printData();
            client.crash(e);
        }
        client.advance();
	}

    private void getModList() throws ClientProtocolException, IOException {
        uiProgress.info("Getting mod list");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        POSTGetter.get(synchronizationData.url + "/modlist", stream);
        Type listType = new TypeToken<List<String>>() {}.getType();
        synchronizationData.modlist = new Gson().fromJson(stream.toString(), listType);
    }

    private void getNeededModsList() {
        uiProgress.info("Getting needed mods");
        File modDir = new File(synchronizationData.cohereDir, "mods");

        StringBuilder logList = new StringBuilder();
        logList.append("Needed mods: ");

        if ((!modDir.isDirectory()) || modDir.list().length == 0) { //If folder doesn't exist or is empty, return entire mod list
            modDir.mkdirs();
            for (String mod : synchronizationData.modlist) {
                logList.append(mod);
                logList.append(", ");
            }
            logger.info(logList.toString());
            synchronizationData.updateConfigs = true;
            synchronizationData.neededmods = synchronizationData.modlist;
            return;
        }

        List<String> currentMods = Library.listFilenames(modDir, true, true);
        List<String> neededMods = new ArrayList<String>();

        for (String mod : synchronizationData.modlist) { //Get list of mods that need to be downloaded from the server
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
            if (!synchronizationData.modlist.contains(mod)) {
                Library.deleteMod(new File(modDir, mod));
                logger.info("Deleting " + mod + " from local storage");
            }
        }
        logger.info(logList.toString());
        logger.info(Integer.toString(neededMods.size()) + " mods needed.");
    }
}
