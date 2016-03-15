package org.tasgoon.coherence.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgoon.coherence.ziputils.ZipUtility;

public class ConfigHandler implements HttpHandler {
	public static final Logger logger = LogManager.getLogger("Coherence");
	public static final ByteArrayOutputStream configs = ZipUtility.getZippedFolder(new File("config"));
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		logger.debug(exchange.getRemoteAddress().getHostName() + " sent a request to get the config files.");
		Headers responseHeaders = exchange.getResponseHeaders();
		exchange.sendResponseHeaders(200, 0);
		
		OutputStream responseBody = exchange.getResponseBody();
		configs.writeTo(responseBody);
		responseBody.close();
	}

}
