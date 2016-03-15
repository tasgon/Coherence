package org.tasgoon.coherence.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.tasgoon.coherence.Coherence;

import java.io.IOException;
import java.io.OutputStream;

public class RootHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(200, 0);
		
		OutputStream body = exchange.getResponseBody();
		body.write((Coherence.MODID + " " + Coherence.VERSION_STRING).getBytes());
		body.close();
	}

}
