package org.dezord.coherence.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Restrictor {
	String os = System.getProperty("os.name").toLowerCase();
	
	public Restrictor() throws IOException {
		if (os.contains("win")) { //Windows code
			setupWindowsRestrictor();
		}
		else if (os.contains("posix")) { //Linux code
			setupLinuxRestrictor();
		}
	}
	
	private void setupWindowsRestrictor() throws IOException {
		URL url = new URL("http://live.sysinternals.com/psexec.exe");
		File file = new File("bin", "psexec.exe");
		Library.downloadFile(url, file);
	}
	
	private void setupLinuxRestrictor() {
		
	}
	
	public Process runRestricted(String cmd) throws IOException {
		return Runtime.getRuntime().exec(cmd);
	}
}
