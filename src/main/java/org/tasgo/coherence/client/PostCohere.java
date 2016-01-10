package org.tasgo.coherence.client;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasgo.coherence.Coherence;
import org.tasgo.coherence.client.ui.Request;

import cpw.mods.fml.common.FMLCommonHandler;

/**Undo everything Cohering did to revert it to a normal state*/
public class PostCohere {
	private static final Logger logger = LogManager.getLogger("Coherence");
	
	public PostCohere() {
		try {
			startCohereUndoer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static JavaCommandBuilder getCohereUndoer() throws IOException {
		JavaCommandBuilder cmdBuilder = new JavaCommandBuilder();
        String[] approvedClassFiles = {"commons-io", "log4j", "jopt"};
        for (String classFile : ManagementFactory.getRuntimeMXBean().getClassPath().split(";")) { //Load commons-io and log4j
        	for (String approvedClassFile : approvedClassFiles) {
        		if (classFile.contains(approvedClassFile))
        			cmdBuilder.classPath.add(classFile);
        	}
        }
        cmdBuilder.classPath.add(JavaCommandBuilder.getCurrentJar().getAbsolutePath());
        
        cmdBuilder.mainClass = CohereUndoer.class.getName();
        cmdBuilder.programArgs.add("-p " + ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        cmdBuilder.programArgs.add("-i " + Coherence.instance.address);
        
        logger.info("Coherence undoer: " + cmdBuilder.constructCommand());
        
        return cmdBuilder;
	}
	
	public static Process startCohereUndoer() throws IOException {
		if (Coherence.instance.debug) { //Only works on Windows
			String cmd = getCohereUndoer().constructCommand();
			File debugFile = new File("debug.bat");
			FileUtils.deleteQuietly(debugFile);
			FileWriter fw = new FileWriter(debugFile);
			fw.write(cmd); fw.write("\npause");
			fw.close();
			return Runtime.getRuntime().exec("cmd /C \"start cmd.exe /C debug.bat\"");
		}
		else
			return getCohereUndoer().launch();
	}
	
	public static void setupTestEnvironment() throws IOException {
		String cmd = getCohereUndoer().constructCommand();
		System.out.println(cmd);
		StringSelection selection = new StringSelection(cmd);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}

	public static void detectCrash() throws IOException, InterruptedException {
		File curMods = new File("coherence", "localhost");
		if (true) {
			if (curMods.isDirectory() && curMods.list().length > 0 && !Coherence.instance.postCohered) {
				new PostCohere();
				if (Request.getYesNo("Coherence has detected a possible crash on the last run."
							+ "\nWould you like to clear out the mods and restart Minecraft?"))
					FMLCommonHandler.instance().exitJava(0, false);
				else
					Coherence.instance.postCohered = true;
			}
		}
	}
}
