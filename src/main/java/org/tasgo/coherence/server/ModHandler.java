package org.tasgo.coherence.server;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModHandler implements HttpHandler {
	private static final Logger logger = LogManager.getLogger();
	private static final HashMap<String, String> modMap = ModListHandler.generateMap();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
			Map<String, String> params = (Map)exchange.getAttribute("parameters");
			String modName = params.get("mod");
			exchange.sendResponseHeaders(200, 0);
			OutputStream responseBody = exchange.getResponseBody();
			
			if (modName == null) {
				replyIllegal(responseBody, exchange);
				return;
			}
			logger.info(exchange.getRemoteAddress().getHostName() + " sent a request for mod " + modName);

			if (!modMap.keySet().contains(modName)) {
				logger.info(modName + " is not in the mod list.");
				replyIllegal(responseBody, exchange);
				return;
			}
			
			File modFile = new File(modMap.get(modName));
			
			if (!modFile.exists()) {
				replyIllegal(responseBody, exchange);
				return;
			}
			
			byte[] mod = Files.readAllBytes(Paths.get(modFile.getAbsolutePath()));
			responseBody.write(mod);
			responseBody.close();
			
		}
		else {
			exchange.sendResponseHeaders(200, 0);
			logger.info(exchange.getRemoteAddress().getHostName() + " sent an illegal mod request!");
			OutputStream responseBody = exchange.getResponseBody();
			responseBody.write("INVALID REQUEST".getBytes());
			responseBody.close();
			return;
		}
	}
	
	private void replyIllegal(OutputStream stream, HttpExchange exchange) throws IOException {
		logger.info(exchange.getRemoteAddress().getHostName() + " sent an illegal mod request!");
		stream.write("INVALID REQUEST".getBytes());
		stream.close();
	}
}
