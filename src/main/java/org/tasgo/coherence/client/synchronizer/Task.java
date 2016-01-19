package org.tasgo.coherence.client.synchronizer;

import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.client.ui.UiProgress;

import java.io.File;

/**
 * Created by Tasgo on 1/19/16.
 */
public class Task extends Thread {
    protected static final Logger logger = LogManager.getLogger("Coherence Synchronizer");
    protected File cohereDir;
    protected UiProgress uiProgress;
    protected Client client;
    protected ServerData serverData;
    protected String address, url;

    public Task(Client parent) {
        this.client = parent;
        this.serverData = parent.getServerData();
    }
}
