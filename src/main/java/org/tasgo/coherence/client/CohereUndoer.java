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
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.Library;
import org.tasgo.coherence.ziputils.ZipUtility;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;



public class CohereUndoer {
	//public final Logger logger = LogManager.getLogger("Coherence");
	public Collection<File> modsToKeep;
	public File cohereDir, modDir, cohereFolder;
	public String pid, ip, minecraftCmd;
	public boolean crashed = false;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		OptionParser parser = new OptionParser("i:p:c::");
		/*OptionSpec<String> ip = parser.accepts("ip", "The previous IP Minecraft was connected to").withRequiredArg();
		OptionSpec<String> pid = parser.accepts("pid", "PID of the minecraft process").withRequiredArg();
		OptionSpec<String> cmd = parser.accepts("cmd", "The minecraft command, for crash recovery").withOptionalArg();*/
		new CohereUndoer(parser.parse(args));
	}
	
	public CohereUndoer(OptionSet options) throws IOException, InterruptedException {
		System.out.println("Starting coherence undoer");
		pid = (String) options.valueOf("p");
		ip = (String) options.valueOf("i");
		if (options.has("c")) {
			crashed = true;
			minecraftCmd = (String) options.valueOf("c");
		}
		
		System.out.println(String.format("Pid is %s\nGetting mods list", pid));
		try {
			modsToKeep = FileUtils.listFiles(new File("coherence", "localhost"), null, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Using command: " + getCommand());
		System.out.println("Starting process kill detector");
		waitForProcessEnd();
		undo();
	}
	
	public String getCommand() {
		String command;
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0)
			command = System.getenv("windir") + "\\system32\\" + "tasklist.exe /FI \"PID eq %s\"";
		else
			command = "ps -p %s";
		return String.format(command, pid);
	}
	
	public void waitForProcessEnd() throws IOException, InterruptedException {
		boolean detected = true;
		String line;
		while (detected) {
			detected = false;
			int count = 0;
			Process process = Runtime.getRuntime().exec(getCommand());
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.contains(pid))
					detected = true;
			}
		}
	}

	public void undo() throws IOException {
		System.out.println("Moving files back.");
		modDir = new File("mods");
		
		for (File mod : modDir.listFiles()) {
			System.out.println("Checking mod " + mod.getName() + " for removal.");
			if (!modsToKeep.contains(mod)) {
				System.out.println(mod.getName() + " was cohered. Deleting...");
				if (mod.isDirectory()) {
					try {
						FileUtils.forceDelete(mod);
					}
					catch (Exception e) {
						System.out.println(String.format("Could not delete %m (%e)", mod.getName(), e.getMessage()));
					}
				}
				else {
					Library.deleteMod(mod);
				}
			}
		}
		
		File config = new File("config");
		File customConfig = Library.getFile("coherence", ip, "customConfig.zip");
		System.out.println("Saving current configs to " + customConfig.getAbsolutePath());
		new ZipUtility(true).compressFolder(config, customConfig);
		System.out.println("Deleting configs from config folder");
		FileUtils.forceDelete(config);
		System.out.println("Moving old configs back to main config folder");
		new File("oldConfig").renameTo(config);
		
		FileUtils.deleteQuietly(new File("coherence", "localhost")); //Contains mods that were just moved back
		
		if (crashed)
			startMC();
	}
	
	public void startMC() {
		try {
			Runtime.getRuntime().exec(minecraftCmd);
		} catch (IOException e) {}
	}
}

