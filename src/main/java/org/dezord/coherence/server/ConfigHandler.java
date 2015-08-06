package org.dezord.coherence.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dezord.coherence.ziputils.ZipUtility;

public class ConfigHandler implements HttpHandler {
	public static final ByteArrayOutputStream configs = new ZipUtility().zipFolder("config");
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		LogManager.getLogger().info(exchange.getRemoteAddress().getHostName() + " sent a request to get the config files.");
		Headers responseHeaders = exchange.getResponseHeaders();
		exchange.sendResponseHeaders(200, 0);
		
		OutputStream responseBody = exchange.getResponseBody();
		configs.writeTo(responseBody);
		responseBody.close();
	}

}
