package org.tasgo.coherence.client.synchronizer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.client.CoherenceData;
import org.tasgo.coherence.client.ui.UiError;
import org.tasgo.coherence.client.ui.UiProgress;
import org.tasgo.coherence.client.ui.UiYesNo;
import org.tasgo.coherence.client.ui.UiYesNoCallback;

import java.io.File;

/**
 * Client class that manages the synchronization with the server.
 */
public class Client implements UiYesNoCallback {
    private enum Phase {INITIATION, SYNCHRONIZATION, COMPLETION}
    private GuiScreen parent;
    private UiProgress uiProgress;
    private Phase phase = Phase.INITIATION;

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

        new Initiator(this).start();
    }

    public void advance() {
        switch (phase) {
            case INITIATION:
                phase = Phase.SYNCHRONIZATION;
                StringBuilder sb = new StringBuilder(String.format("The server %s would like to load %d mods.",
                        synchronizationData.address, synchronizationData.neededmods.size()));
                sb.append("\nAre you okay with this?");
                FMLClientHandler.instance().showGuiScreen(new UiYesNo(this, sb.toString()));
                break;
            case SYNCHRONIZATION:
                phase = Phase.COMPLETION;
                uiProgress.reset();
                new Completioner(this).start();
                break;
        }
    }

    @Override
    public void onClick(boolean result) {
        switch (phase) {
            case SYNCHRONIZATION:
                if (result) {
                    uiProgress.reset();
                    new Synchronizer(this).start();
                }
                else {
                    FMLClientHandler.instance().showGuiScreen(parent);
                }
                break;
            case COMPLETION:
                synchronizationData.updateConfigs = result;
                new Completioner(this).start();
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
