package org.dezord.coherence.server;

import org.dezord.coherence.Coherence;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(200, 0);
		
		OutputStream body = exchange.getResponseBody();
		body.write((Coherence.MODID + " " + Coherence.VERSION).getBytes());
		body.close();
	}

}
