package org.dezord.coherence.client;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dezord.coherence.Coherence;

public class PostCohere { //This class undoes everything Cohering did to revert it to a normal state
	private final Logger logger = LogManager.getLogger("Coherence");
	private final File cohereDir;
	
	public PostCohere(String address) {
		cohereDir = new File("coherence", address);
		logger.info("Coherence used on last launch. Making sure mods don't launch on next launch.");
		addRemovalHook();
		
		logger.info("Removing persistent config setting");
    	Configuration config = new Configuration(Coherence.instance.configName);
    	config.load();
    	
    	Property addressProperty = config.get(config.CATEGORY_GENERAL, "connectToServer", "null");
    	addressProperty.set("null");
    	
    	config.save();
	}
	
	private void addRemovalHook() {
		logger.info("Adding hook to move files back");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Moving files back");
					File modDir = new File("mods"); List<File> cohereMods = Arrays.asList(new File(cohereDir, "mods").listFiles());
					for (File mod : modDir.listFiles()) {
						System.out.println("Checking mod " + mod.getName() + " for removal.");
						if (cohereMods.contains(mod))
							FileUtils.deleteQuietly(mod);
					}
										
					FileUtils.deleteDirectory(new File("config"));
					FileUtils.moveDirectory(new File("oldConfig"), new File("config"));
				} catch (IOException e) { e.printStackTrace(); }
			}
		});
	}
	
}
