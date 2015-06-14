package org.dezord.coherence.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class Server {
	private static final Logger logger = LogManager.getLogger();
	public final int port = 25566;
	
	public Server() throws IOException {
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
