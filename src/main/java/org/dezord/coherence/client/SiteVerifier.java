package org.dezord.coherence.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dezord.coherence.Coherence;




public class SiteVerifier {
	private final String googleKey = "REDACTED";
	private final String address;
	private static final Logger logger = LogManager.getLogger("Coherence");
	
	public SiteVerifier(String ip) {
		address = ip;
	}
	
	private String get(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		int responseCode = con.getResponseCode();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();

		return response.toString();
	}
	
	public boolean verify() throws IOException {
		logger.info("Verifying " + address);
		
		if (!checkGoogle())
			return false;
		
		return true;
	}
	
	private boolean checkGoogle() throws IOException {
		StringBuilder request = new StringBuilder();
		request.append("https://sb-ssl.google.com/safebrowsing/api/lookup?");
		request.append("client=coherence").append("&key=" + googleKey).append("&ver=" + Coherence.VERSION).append("&pver=3.1");
		request.append("&url=" + address);
		logger.info("Request URL: " + request.toString());
		
		String response = get(request.toString());
		logger.info("Response from Google: " + response);
		if (response.contains("malware"))
			return false;
		return true;
	}
}
