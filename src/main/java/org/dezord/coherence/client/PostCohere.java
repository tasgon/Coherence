package org.dezord.coherence.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	String address;
	
	public PostCohere(String ip) {
		address = ip;
		logger.info("Coherence used on last launch. Making sure mods don't launch on next launch.");
		
		logger.info("Removing persistent config setting");
    	Configuration config = new Configuration(Coherence.instance.configName);
    	config.load();
    	
    	Property addressProperty = config.get(config.CATEGORY_GENERAL, "connectToServer", "null");
    	addressProperty.set("null");
    	
    	config.save();
	}
	
	private void startCohereUndoer() {
		
	}
}
