package org.tasgo.coherence.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class CohereUndoer {
	public static final Logger logger = LogManager.getLogger("Coherence");
	public static Collection<File> modsToKeep;
	public static File cohereDir, modDir, cohereFolder;
	public static final String command = getCommand();
	public static String pid;
	public static String[] arguments;
	public static boolean crashed = false;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		logger.info("Starting coherence undoer");
		pid = args[0];
		
		logger.info(String.format("Pid is %s\nGetting mods list", pid));
		try {
			modsToKeep = FileUtils.listFiles(new File("coherence", "localhost"), null, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("Using command: " + command);
		logger.info("Starting process kill detector");
		waitForProcessEnd();
		undo();
	}
	
	public static String getCommand() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0) ? System.getenv("windir") +"\\system32\\"+"tasklist.exe /V" : "ps -ef";
	}
	
	public static void waitForProcessEnd() throws IOException, InterruptedException {
		boolean detected = true;
		String line;
		while (detected) {
			detected = false;
			int count = 0;
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.toLowerCase().contains(pid)) {
					detected = true;
				}
			}
		}
	}

	public static void undo() throws IOException {
		logger.info("Moving files back.");
		modDir = new File("mods");
		
		for (File mod : modDir.listFiles()) {
			logger.info("Checking mod " + mod.getName() + " for removal.");
			if (!modsToKeep.contains(mod)) {
				logger.info(mod.getName() + " was cohered. Deleting...");
				if (mod.isDirectory())
					FileUtils.deleteQuietly(mod);
				else mod.delete();
			}
		}
		
		logger.info("Deleting config folder");
		FileUtils.deleteQuietly(new File("config"));
		logger.info("Moving old configs back to main config folder");
		new File("oldConfig").renameTo(new File("config"));
		
		FileUtils.deleteQuietly(new File("coherence", "localhost")); //Contains mods that were just moved back
		
		if (crashed)
			startMC();
	}
	
	public static String join(List<String> list, String conjunction)
	{
	   StringBuilder sb = new StringBuilder();
	   boolean first = true;
	   for (String item : list)
	   {
	      if (first)
	         first = false;
	      else
	         sb.append(conjunction);
	      sb.append(item);
	   }
	   return sb.toString();
	}
	
	public static void startMC() {
		List<String> args = Arrays.asList(arguments);
		args.remove(0);
		String cmd = join(args, " ");
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {}
	}
}

