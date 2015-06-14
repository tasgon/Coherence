package org.dezord.coherence.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModListHandler implements HttpHandler {
	public static String modstring = ModListHandler.generateList();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		LogManager.getLogger().info(exchange.getRemoteAddress().getHostName() + " sent a request to get the mod list.");
		Headers responseHeaders = exchange.getResponseHeaders();
		exchange.sendResponseHeaders(200, 0);
		
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(modstring.getBytes());
		responseBody.close();
	}
	
	public static String generateList() {
		File[] mods = new File("mods").listFiles();
		List modlist = new ArrayList();
		for (File file : mods) {
			if(!file.isDirectory() && !file.getName().endsWith(".disabled") /*&& !file.getName().contains("coherence")*/)
				modlist.add(file.getName());
		}
		String output = new Gson().toJson(modlist);
		return output;
	}
}
