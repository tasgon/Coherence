package org.tasgo.coherence.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.client.ui.UiError;
import org.tasgo.coherence.client.ui.UiBasicCallback;
import org.tasgo.coherence.common.Version;

import java.io.ByteArrayOutputStream;

@SideOnly(Side.CLIENT)
public class Client {
	
	private static final Logger logger = LogManager.getLogger("Coherence");
	private GuiScreen parent;
    public ServerData serverData;
    public String coherenceURL;

	public Client(GuiScreen par, ServerData server) {
		logger.info("Initializing Coherence");
        this.parent = par;
        this.serverData = server;
        this.coherenceURL = getCoherenceURL(server.serverIP);
        new Synchronizer(this).start();
	}

    public void crash(Exception e) {
        FMLClientHandler.instance().showGuiScreen(new UiError(new UiBasicCallback() {
            @Override
            public void onClick() {
                FMLClientHandler.instance().showGuiScreen(parent);
            }
        }, e.getMessage()));
    }

    public static String getCoherenceURL(String ip) {
        return "http://" + ip + ":25566";
    }

	public static String getRemoteVersion(String ip) {
        String address = getCoherenceURL(ip);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            POSTGetter.get(address, outputStream);
            String[] remoteData = outputStream.toString().split(" ");
            return remoteData[1];
        } catch (Exception e) {
            return null;
        }

    }

    public static boolean guaranteedCompatible(String ip) {
        return getRemoteVersion(ip) == Coherence.VERSION_STRING;
    }

    public static boolean maybeCompatible(String ip) {
        return Version.fromString(getRemoteVersion(ip)).getMCVersion() == Coherence.VERSION.getMCVersion();
    }
}
