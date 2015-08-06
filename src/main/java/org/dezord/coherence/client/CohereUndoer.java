package org.dezord.coherence.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



public class CohereUndoer {
	public static List<String> modsToKeep;
	public static File cohereDir, modDir, cohereFolder;
	public static final String command = getCommand();
	public static String[] arguments;
	public static boolean crashed;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Thread.sleep(5000);
		
		System.out.println("Starting coherence undoer");
		
		modsToKeep = Arrays.asList(new File("coherence", "localhost").list());
		
		System.out.println("Using command: " + command);
		System.out.println("Starting process kill detector");
		detectProcessKill();
		undo();
	}
	
	public static String getCommand() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0) ? System.getenv("windir") +"\\system32\\"+"tasklist.exe /V" : "ps -ef";
	}
	
	public static void detectProcessKill() throws IOException {
		boolean processAlive = true;
		boolean detected;
		String line;
		while (processAlive) {
			detected = false;
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.toLowerCase().contains("minecraft"))
					detected = true;
			}
			if (!detected)
				processAlive = false;
		}
	}
	
	public static String getModList() {
		StringBuilder builder = new StringBuilder();
		builder.append("Files to keep: ");
		for (String mod : modsToKeep) {
			builder.append(mod + ", ");  
		}
		return builder.toString();
	}
	
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}

	public static void undo() throws IOException {
		System.out.println("Moving files back.");
		modDir = new File("mods");
		
		System.out.println(getModList());
		
		for (File mod : modDir.listFiles()) {
			System.out.println("Checking mod " + mod.getName() + " for removal.");
			if (!modsToKeep.contains(mod.getName())) {
				System.out.println(mod.getName() + " was cohered. Deleting...");
				if (mod.isDirectory())
					deleteDirectory(mod);
				else mod.delete();
			}
		}
		
		System.out.println("Deleting config folder");
		deleteDirectory(new File("config"));
		System.out.println("Moving old configs back to main config folder");
		new File("oldConfig").renameTo(new File("config"));
		
		deleteDirectory(new File("coherence", "localhost")); //Contains mods that were just moved back
		
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

