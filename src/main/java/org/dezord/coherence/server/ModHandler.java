package org.dezord.coherence.server;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModHandler implements HttpHandler {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
			Map<String, String> params = (Map)exchange.getAttribute("parameters");
			String originalMod = params.get("mod");
			exchange.sendResponseHeaders(200, 0);
			OutputStream responseBody = exchange.getResponseBody();
			
			if (originalMod == null) {
				replyIllegal(responseBody, exchange);
				return;
			}

			String modName = originalMod.replace('/', ' ').replace('\\', ' ');
			File modFile = new File("mods" + File.separator + modName);
			
			logger.info(exchange.getRemoteAddress().getHostName() + " sent a request for mod " + modName);
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
