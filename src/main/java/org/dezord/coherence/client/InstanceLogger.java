package org.dezord.coherence.client;

import java.io.IOException;
import java.io.InputStream;

public class InstanceLogger extends Thread {
	String cmd;
	
	public InstanceLogger (String command) {
		cmd = command;
	}
	
	public void run() {
		Process process;
		try {
			process = Runtime.getRuntime().exec(cmd.toString());
			byte[] b = new byte[1024];
			InputStream stream = process.getInputStream();
			while (stream.read(b) > 0)
				System.out.println(new String(b));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
