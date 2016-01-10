package org.tasgo.coherence.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.tasgo.coherence.Coherence;

public class JavaCommandBuilder {
	//A modular java command builder and launcher
	public String jvmArgs = "";
	public String mainClass = "";
	public HashMap<String, String> jvmVars = new HashMap<String, String>();
	public ArrayList<String> classPath = new ArrayList<String>();
	public ArrayList<String> programArgs = new ArrayList<String>();
	
	public String constructCommand() {
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

	public Process launch() throws IOException {
		return Runtime.getRuntime().exec(constructCommand());
	}

	public static File getCurrentJar() {
		String codeSource = Coherence.class.getProtectionDomain().getCodeSource().getLocation().toString();
		codeSource = codeSource.replace("jar:file:/", "");
		return new File(codeSource.substring(0, codeSource.indexOf("!")));
		
	}
}
