package org.tasgoon.coherence.client.synchronizer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgoon.coherence.Coherence;
import org.tasgoon.coherence.client.CoherenceData;
import org.tasgoon.coherence.client.ui.UiError;
import org.tasgoon.coherence.client.ui.UiProgress;
import org.tasgoon.coherence.client.ui.UiYesNo;
import org.tasgoon.coherence.client.ui.UiYesNoCallback;

import java.io.File;

/**
 * Client class that manages the synchronization with the server.
 */
public class Client implements UiYesNoCallback {
    private enum Phase {INITIATION, SYNCHRONIZATION, COMPLETION}
    private GuiScreen parent;
    private UiProgress uiProgress;
    private Phase phase = Phase.INITIATION;
    private static final Logger logger = LogManager.getLogger("Coherence Loader");

    private SynchronizationData synchronizationData;

    public Client(GuiScreen parent, ServerData serverData) {
        if (Coherence.instance.postCohered) {
            FMLClientHandler.instance().connectToServer(parent, serverData);
            return;
        }
        this.parent = parent;
        this.uiProgress = new UiProgress(parent, 2, true);
        this.synchronizationData = new SynchronizationData();
        synchronizationData.serverData = serverData;
        synchronizationData.cohereDir = new File("coherence", serverData.serverIP);
        synchronizationData.url = CoherenceData.getCoherenceURL(serverData.serverIP);
        FMLClientHandler.instance().showGuiScreen(uiProgress);

        new InitiationStage(this).start();
    }

    public void advance() {
        logger.info("Advancing...");
        uiProgress.reset();
        logger.info("Reset uiProgress");
        switch (phase) {
            case INITIATION:

                if (synchronizationData.neededmods == null || synchronizationData.neededmods.isEmpty()) {
                    phase = Phase.COMPLETION;
                    synchronizationData.updateConfigs = false;
                    new CompletionStage(this).start();
                }
                else {
                    phase = Phase.SYNCHRONIZATION;
                    StringBuilder sb = new StringBuilder(String.format("The server %s would like to load %d mods.",
                            synchronizationData.serverData.serverIP, synchronizationData.neededmods.size()));
                    sb.append("\nAre you okay with this?");
                    FMLClientHandler.instance().showGuiScreen(new UiYesNo(this, sb.toString()));
                }
                break;
            case SYNCHRONIZATION:
                phase = Phase.COMPLETION;
                if (!synchronizationData.neededmods.isEmpty() && synchronizationData.neededmods == null) {
                    FMLClientHandler.instance().showGuiScreen(new UiYesNo(this, "Would you like to update the configuration files?"));
                }
                else {
                    new CompletionStage(this).start();
                }
                break;
        }
    }

    @Override
    public void onClick(boolean result) {
        switch (phase) {
            case SYNCHRONIZATION:
                if (result) {
                    uiProgress.totalSteps = synchronizationData.neededmods.size() + 1;
                    new SynchronizationStage(this).start();
                }
                else {
                    FMLClientHandler.instance().showGuiScreen(parent);
                }
                break;
            case COMPLETION:
                synchronizationData.updateConfigs = result;
                new CompletionStage(this).start();
                break;
        }

    }

    public UiProgress getProgressScreen() {
        return uiProgress;
    }

    public SynchronizationData getSynchronizationData() {
        return synchronizationData;
    }

    protected void crash(Exception e) {
        e.printStackTrace();
        UiError.crash(parent, e);
    }
}
