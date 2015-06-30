package org.dezord.coherence.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dezord.coherence.Coherence;

public class PostCohere { //This class undoes everything Cohering did to revert it to a normal state
	private final Logger logger = LogManager.getLogger("Coherence");
	
	public PostCohere() {
		logger.info("Coherence used on last launch. Making sure mods don't launch on next launch.");
		
		logger.info("Removing persistent config setting");
    	Configuration config = new Configuration(Coherence.instance.configName);
    	config.load();
    	
    	Property addressProperty = config.get(config.CATEGORY_GENERAL, "connectToServer", "null");
    	addressProperty.set("null");
    	
    	config.save();
    	
    	
    	try {
			downloadCohereUndoer();
			startCohereUndoer();
		} catch (IOException e) {
			logger.warn("Error starting Coherence undoer");
			logger.warn(e.getMessage());
			logger.warn("The additional mods added will NOT be deleted when Minecraft closes. You will have to manually do so yourself.");
		}
	}
	
	private void downloadCohereUndoer() throws IOException {
		new File("bin").mkdir();
		File file = new File("bin", "CohereUndoer.class");
		URL url = new URL("https://github.com/dezord/Coherence/raw/master/src/main/java/bin/CohereUndoer.class");
		Library.downloadFile(url, file);
	}
	
	private void startCohereUndoer() throws IOException {
		StringBuilder cmd = new StringBuilder();
        cmd.append("\"").append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\" ");
        cmd.append("bin.CohereUndoer ");
        
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        cmd.append(processName.split("@")[0]).append(" "); //Get process id
        cmd.append(new File("").getAbsolutePath());
        logger.info("Executing " + cmd.toString());
        Runtime.getRuntime().exec(cmd.toString());
	}
}
