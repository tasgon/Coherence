package org.tasgoon.coherence.common;

import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Tasgo on 1/17/2016.
 */
public class Version {
    public static final char ALPHA = 'a';
    public static final char BETA = 'b';
    public static final char RELEASE = 'r';

    private String mcVersion;
    private char versionType;
    private int build;


    public Version(char type, int buildNumber) {
        mcVersion = MinecraftForge.MC_VERSION.substring(0, 2);
        versionType = type;
        build = buildNumber;
    }

    public Version(String mcVer, char type, int buildNumber) {
        mcVersion = mcVer;
        versionType = type;
        build = buildNumber;
    }

    public static Version fromString(String ver) {
        String mcVersion = ver.substring(0, 2);
        char versionType = ver.charAt(3);
        int build = Integer.valueOf(ver.substring(4));
        return new Version(mcVersion, versionType, build);
    }

    public int getBuild() {
        return build;
    }

    public char getVersionType() {
        return versionType;
    }

    public String getMCVersion() {
        return mcVersion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mcVersion);
        sb.append(versionType);
        sb.append(String.format("%02d", build));
        return sb.toString();
    }
}
