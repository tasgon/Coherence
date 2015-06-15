package bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



public class CohereUndoer extends Thread {
	public static String pid, address;
	public static File MCDir, cohereDir, modDir, cohereFolder;
	public static final String command = getCommand();
	
	public static void main(String[] args) throws IOException {
		System.out.println("Starting coherence undoer");
		pid = args[0];
		MCDir = new File(args[1]);
		address = args[2];
		cohereDir = new File(new File(MCDir, "coherence"), address);
		
		System.out.println("Using command: " + command);
		System.out.println("Starting process kill detector");
		detectProcessKill();
		undo();
	}
	
	public static String getCommand() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0) ? System.getenv("windir") +"\\system32\\"+"tasklist.exe" : "ps -e";
	}
	
	public static void detectProcessKill() throws IOException {
		boolean processAlive = true;
		boolean detected;
		String line;
		//StringBuilder builder = new StringBuilder();
		while (processAlive) {
			detected = false;
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				//builder.append(line).append("\n");
				if (line.contains(pid))
					detected = true;
			}
			if (!detected)
				processAlive = false;
		}
	}
	
	public static String getModList() {
		StringBuilder builder = new StringBuilder();
		builder.append("Files to check against: ");
		for (String mod : cohereFolder.list()) {
			builder.append(mod + ", ");  
		}
		return builder.toString();
	}

	public static void undo() throws IOException {
		System.out.println("Moving files back.");
		modDir = new File(MCDir, "mods"); cohereFolder = new File(cohereDir, "mods");
		List<String> cohereMods = Arrays.asList(cohereFolder.list());
		System.out.println(cohereFolder.getAbsolutePath());
		
		System.out.println(getModList());
		
		for (File mod : modDir.listFiles()) {
			System.out.println("Checking mod " + mod.getName() + " for removal.");
			if (cohereMods.contains(mod.getName()) && !mod.getName().contains("coherence")) {
				System.out.println(mod.getName() + " was cohered. Deleting...");
				mod.delete();
			}
		}
		
		System.out.println("Deleting config folder");
		new File("config").delete();
		System.out.println("Moving old configs back to main config folder");
		new File("oldConfig").renameTo(new File("config"));
	}
}

