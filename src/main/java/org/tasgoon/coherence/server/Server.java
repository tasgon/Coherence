package org.tasgoon.coherence.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class Server {
	private static final Logger logger = LogManager.getLogger("Coherence");
	public final int port;
	
	public Server(int p) throws IOException {
		port = p;
		InetSocketAddress socket = new InetSocketAddress(port);
		HttpServer server = HttpServer.create(socket, 0);
		
		logger.info("Setting up handlers");
		logger.debug("Setting info handler");
		server.createContext("/", new RootHandler());
		logger.debug("Setting up mod list handler");
		server.createContext("/modlist", new ModListHandler());
		logger.debug("Setting up config retriever handler");
		server.createContext("/config", new ConfigHandler());
		logger.debug("Setting up mod retriever handler");;
		HttpContext modContext = server.createContext("/mod", new ModHandler());
		modContext.getFilters().add(new ParameterFilter());
		logger.info("Starting server");
		server.start();
		logger.info("Done!");
	}
}
