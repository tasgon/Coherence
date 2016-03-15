package org.tasgoon.coherence.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class POSTGetter {
	public static void get(String url, OutputStream ostream) throws ClientProtocolException, IOException {
		get(url, new ArrayList<NameValuePair>(), ostream);
	}
	
	public static void get(String url, HashMap<String, String> requests, OutputStream ostream) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<String> iter = requests.keySet().iterator();
		
		while (iter.hasNext()) {
			String key = iter.next();
			params.add(new BasicNameValuePair(key, requests.get(key)));
		}
		get(url, params, ostream);
	}
	
	public static void get(String url, List<NameValuePair> params, OutputStream ostream) throws ClientProtocolException, IOException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
		    InputStream instream = entity.getContent();
		    try {
		    	IOUtils.copy(instream, ostream);
		    	instream.close();
		    	return;
		    } finally {
		        instream.close();
		    }
		}
	}
}
