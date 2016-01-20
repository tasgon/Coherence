package org.tasgo.coherence.client.synchronizer;

import net.minecraft.client.multiplayer.ServerData;

import java.io.File;
import java.util.List;

/**
 * Created by Tasgo on 1/19/2016.
 */
public class SynchronizationData {
    public File cohereDir;
    public ServerData serverData;
    public String address, url;
    public List<String> modlist;
    public List<String> neededmods;
    public boolean updateConfigs;

    public void printData() {
        System.out.println("Coherence directory: " + (cohereDir == null ? "null" : cohereDir.getAbsolutePath()));
        System.out.println("Server ip: " + (serverData == null ? "null" : serverData.serverIP));
        System.out.println("Address: " + (address == null ? "null" : address));
        System.out.println("Url: " + (url == null ? "null" : url));
        System.out.println("Modlist size: " + (modlist == null ? "null" : modlist.size()));
        System.out.println("Neededmods size: " + (neededmods == null ? "null" : modlist.size()));
        System.out.println("updateConfigs: " + updateConfigs);
    }
}
