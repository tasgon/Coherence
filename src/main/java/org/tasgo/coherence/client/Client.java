package org.tasgo.coherence.client;

import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.common.Version;

import java.io.ByteArrayOutputStream;

@SideOnly(Side.CLIENT)
public class Client {
	
	private static final Logger logger = LogManager.getLogger("Coherence");
	
	public static void ClientInit(ServerAddress servaddr) {
		logger.info("Initializing Coherence");
		String ip = servaddr.getIP();
		logger.debug("IP of server: " + ip);
		
		String address = "http://" + ip + ":25566";
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		
		try {
			POSTGetter.get(address, ostream);
		}
		catch (Exception e) {
			logger.info("Server does not support Coherence. exiting");
			return;
		}
		if (!ostream.toString().contains("Coherence")) {
			logger.info("Server does not support Coherence. exiting");
			return;
		}
		logger.info("This server supports Coherence! Cohering with server...");
		
		try {
			new Cohere(address, ip);
		}
		catch (Exception e) {
			logger.error("Error occured while cohering:");
			e.printStackTrace();
			logger.error("Cohering cannot continue. Exiting Coherence");
		}
	}

	public static String getRemoteVersion(String ip) {
        String address = "http://" + ip + ":25566";
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
