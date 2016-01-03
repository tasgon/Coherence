package org.tasgo.coherence.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.ServerAddress;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.client.ui.Request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
}
