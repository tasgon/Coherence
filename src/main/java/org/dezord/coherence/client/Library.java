package org.dezord.coherence.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.launchwrapper.Launch;

public class Library {
	private static final Logger logger = LogManager.getLogger("Coherence");
	
	public static void downloadFile(URL url, File file) throws IOException {
		FileUtils.deleteQuietly(file);
		logger.info("Downloading " + url.toString() + " to " + file.getPath());
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
	public static void restartMinecraft() throws IOException, InterruptedException {
		logger.info("Restarting Minecraft");
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
        
        Process process = Runtime.getRuntime().exec(cmd.toString());
        
        /*byte[] b = new byte[1024]; //Debug code
        InputStream stream = process.getInputStream();
        while (true) {
        	stream.read(b);
        	logger.info(new String(b));
        }*/
        
        FMLCommonHandler.instance().exitJava(0, false);
    }
}
