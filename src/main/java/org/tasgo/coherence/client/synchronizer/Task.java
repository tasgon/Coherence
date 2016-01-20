package org.tasgo.coherence.client.synchronizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.client.ui.UiProgress;

/**
 * Created by Tasgo on 1/19/16.
 */
public class Task extends Thread {
    protected static final Logger logger = LogManager.getLogger("Coherence Synchronizer");
    protected UiProgress uiProgress;
    protected Client client;
    protected SynchronizationData synchronizationData;

    public Task(Client parent) {
        this.client = parent;
        this.synchronizationData = parent.getSynchronizationData();
        this.uiProgress = parent.getProgressScreen();
        //FMLClientHandler.instance().showGuiScreen(this.uiProgress);
    }
}
