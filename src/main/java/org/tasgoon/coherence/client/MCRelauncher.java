package org.tasgoon.coherence.client;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class MCRelauncher {
	private static final Logger logger = LogManager.getLogger("Coherence");
	
	public static String getLaunchString() {
		StringBuilder cmd = new StringBuilder();
		cmd.append("\"").append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\" ");
		for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			cmd.append(jvmArg + " ");
		}
		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
		
		String mcCommand = System.getProperty("sun.java.command"); //Get commands passed to Minecraft jar
		if (!mcCommand.contains("GradleStart")) //Only run the LaunchWrapper if not in a development environment
			cmd.append(Launch.class.getName()).append(" ");

		logger.info("Command half-string: " + cmd.toString());
		cmd.append(mcCommand);
		return cmd.toString();
	}
	
	public static void restartMinecraft() throws IOException, InterruptedException {
		logger.info("Restarting Minecraft");
        String command = getLaunchString();
        Process process = Runtime.getRuntime().exec(command);
        
        FMLCommonHandler.instance().exitJava(0, false);
    }
}
