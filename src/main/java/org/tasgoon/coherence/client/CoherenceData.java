package org.tasgoon.coherence.client;

import org.tasgoon.coherence.Coherence;
import org.tasgoon.coherence.common.Version;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CoherenceData {
	public File cohereDir;
	
	public CoherenceData(String ip) {
		cohereDir = new File("coherence", ip);
	}
	
	public static CoherenceData getLocalData() {
		return new CoherenceData("localhost");
	}

    public static String getCoherenceURL(String ip) {
        return "http://" + ip + ":2" + Coherence.clientPort;
    }

    public static String getRemoteVersion(String ip) {
        String address = getCoherenceURL(ip);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            POSTGetter.get(address, outputStream);
            String[] remoteData = outputStream.toString().split(" ");
            return remoteData[1];
        } catch (Exception e) {
            return null;
        }

    }

    public static boolean guaranteedCompatible(String ip) {
        return getRemoteVersion(ip) == Coherence.VERSION_STRING;
    }

    public static boolean maybeCompatible(String ip) {
        return Version.fromString(getRemoteVersion(ip)).getMCVersion() == Coherence.VERSION.getMCVersion();
    }

    public File getMods() {
		return new File(cohereDir, "mods");
	}
	
}
