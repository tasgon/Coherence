package org.tasgo.coherence.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.io.FileUtils;
import org.tasgo.coherence.common.Library;
import org.tasgo.coherence.ziputils.ZipUtility;

import joptsimple.OptionParser;
import joptsimple.OptionSet;



public class CohereUndoer {
	//public final Logger logger = LogManager.getLogger("Coherence");
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
			Process process = Runtime.getRuntime().exec(getCommand());
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.contains(pid))
					detected = true;
			}
		}
	}

	public void undo() {
		System.out.println("Moving files back.");
		File modsDir = new File("mods");
		File originalModsDir = Library.getFile("coherence", "localhost", "mods");
		
		/*//Because the reversal script is running from a file in the mods directory, I can't just remove the file.
		//So, I have to iterate through the folder and remove all the files save for Coherence.
		for (File file : cohereModsDir.listFiles()) 
			FileUtils.deleteQuietly(file);*/
		FileUtils.deleteQuietly(modsDir);
		for (File file : originalModsDir.listFiles()) {
			try {
				FileUtils.moveToDirectory(file, modsDir, false);
			} catch (IOException e) {
				System.err.println("Could not move file " + file.getAbsolutePath() + " back.");
				e.printStackTrace();
			}
		}
		
		
		File config = new File("config");
		File customConfig = Library.getFile("coherence", ip, "customConfig.zip");
		System.out.println("Saving current configs to " + customConfig.getAbsolutePath());
		ZipUtility.compressFolder(config, customConfig);
		System.out.println("Deleting configs from config folder");
		try {
			FileUtils.forceDelete(config);
		} catch (IOException e) {
			System.err.println("Could not delete synchronized config files.");
			e.printStackTrace();
		}
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

