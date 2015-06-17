package org.dezord.coherence.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModListHandler implements HttpHandler {
	private static final Logger logger = LogManager.getLogger("Coherence");
	public static final String[] extensions = {"jar", "zip"};
	public static String modstring = new Gson().toJson(ModListHandler.generateList());

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		logger.info(exchange.getRemoteAddress().getHostName() + " sent a request to get the mod list.");
		Headers responseHeaders = exchange.getResponseHeaders();
		exchange.sendResponseHeaders(200, 0);
		
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(modstring.getBytes());
		responseBody.close();
	}
	
	public static List<String> generateList() {
		File mods = new File("mods");
		List<File> fileList = (List) FileUtils.listFiles(mods, extensions, true);
		List<String> modlist = new ArrayList<String>();
		
		for (File modFile : fileList) {
			String mod = modFile.getName();
			logger.debug("Adding " + mod + " to the mod list.");
			modlist.add(mod);
		}
		
		return modlist;
	}
	
	public static HashMap<String, String> generateMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		File mods = new File("mods");
		List<File> fileList = (List) FileUtils.listFiles(mods, extensions, true);
		for (File modFile : fileList) {
			map.put(modFile.getName(), modFile.getPath());
		}
		return map;
	}
}
