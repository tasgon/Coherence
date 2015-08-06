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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dezord.coherence.Coherence;
import org.dezord.coherence.Library;

public class PostCohere { //This class undoes everything Cohering did to revert it to a normal state
	private final Logger logger = LogManager.getLogger("Coherence");
	
	public PostCohere() {
	}
	
	public static String getJarFilename() {
		String codeSource = Coherence.class.getProtectionDomain().getCodeSource().getLocation().toString();
    	codeSource = codeSource.replace("jar:file:/", "");
    	return codeSource.substring(0, codeSource.indexOf("!"));
    	
	}
	
	public static Process startCohereUndoer() throws IOException {
		JavaCommandBuilder cmdBuilder = new JavaCommandBuilder();
        String[] approvedClassFiles = {"commons-io", "logging"};
        for (String classFile : ManagementFactory.getRuntimeMXBean().getClassPath().split(";")) { //Load commons-io and logging
        	for (String approvedClassFile : approvedClassFiles) {
        		if (classFile.contains(approvedClassFile))
        			cmdBuilder.classPath.add(classFile);
        	}
        }
        cmdBuilder.classPath.add(getJarFilename());
        
        cmdBuilder.mainClass = "org.dezord.coherence.client.CohereUndoer";
        
        return cmdBuilder.launch();
	}
}
