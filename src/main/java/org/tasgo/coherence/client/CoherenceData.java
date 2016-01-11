package org.tasgo.coherence.client;

import java.io.File;

public class CoherenceData {
	public File cohereDir;
	
	public CoherenceData(String ip) {
		cohereDir = new File("coherence", ip);
	}
	
	public static CoherenceData getLocalData() {
		return new CoherenceData("localhost");
	}
	
	public File getMods() {
		return new File(cohereDir, "mods");
	}
	
}
