package org.dezord.coherence.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public class JavaCommandBuilder {
	//A modular java command builder and launcher
	public static String jvmArgs = "";
	public static String mainClass = "";
	public static HashMap<String, String> jvmVars = new HashMap<String, String>();
	public static ArrayList<String> classPath = new ArrayList<String>();
	public static ArrayList<String> programArgs = new ArrayList<String>();
	
	public static String constructCommand() {
		StringBuilder SB = new StringBuilder();
		SB.append("\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\" ");
		
		StringBuilder jvmSB = new StringBuilder(jvmArgs);
		for (String var : jvmVars.keySet()) {
			jvmSB.append(" -D");
			if (jvmVars.get(var) == "") {
				jvmSB.append(var + "");
			}
			else {
				jvmSB.append(var + "=" + jvmVars.get(var) + "");
			}
		}
		SB.append(jvmSB.toString());
		
		StringBuilder cpSB = new StringBuilder(" -cp ");
		for (String clazz : classPath) {
			cpSB.append(clazz + ";");
		}
		String cpString = cpSB.toString();
		SB.append(cpString.substring(0, cpString.length() - 1) + " ");
		
		SB.append(mainClass + " ");
		
		SB.append(StringUtils.join(programArgs, " "));
		
		return SB.toString();
	}

	public static Process launch() throws IOException {
		return Runtime.getRuntime().exec(constructCommand());
	}
}
