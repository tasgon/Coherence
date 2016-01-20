package org.tasgo.coherence.common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Library {
	private static final Logger logger = LogManager.getLogger("Coherence");
	
	/**Download a URL to a file
	 * 
	 * @param url URL from which to get the data
	 * @param file File to save to
	 * @throws IOException
	 */
	public static void downloadFile(URL url, File file) throws IOException {
		FileUtils.deleteQuietly(file);
		logger.info("Downloading " + url.toString() + " to " + file.getPath());
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
	/**Construct a File from an arbitrary list of paths, compared to File's max of 2
	 * 
	 * @param paths The paths to concatenate
	 * @return A file from those paths
	 */
	public static File getFile(String... paths) {
		StringBuilder sb = new StringBuilder();
		for (String file : paths) {
			sb.append(file + File.separator);
		}
		return new File(sb.toString());
	}
	
	/**Delete mod and prent folder if it exists
	 * 
	 * @param mod Mod file
	 */
	public static void deleteMod(File mod) {
		mod.delete();
		File parentFolder = new File(FilenameUtils.getFullPath(mod.getAbsolutePath()));
		if (parentFolder.isDirectory() && parentFolder.list().length == 0) {
			System.out.print(String.format("Parent folder %s is empty, removing", parentFolder.getAbsolutePath()));
			parentFolder.delete();
		}
	}
	
	/**Get a filename from a reference point
	 * 
	 * @param file The file to reference
	 * @param reference The reference point of the file
	 * @return The name of the file from that reference point
	 */
	public static String getFileFromReference(File file, File reference) {
		return file.getAbsolutePath().substring(reference.getAbsolutePath().length() + 1);
	}
	
	/**List all filenames of an object, compares to FileUtils' File objects
	 * 
	 * @param directory Parent directory
	 * @param recursive Indicates whether the listing should be recursive
	 * @param standardized Indicates whether the separaters should be standardized into unix form
	 * @return A list of all filenames in that directory
	 */
	public static List<String> listFilenames(File directory, boolean recursive, boolean standardized) {
		Collection<File> files = FileUtils.listFiles(directory, null, recursive);
		List<String> filenames = new ArrayList<String>();
		for (File file : files) {
			String name = getFileFromReference(file, directory);
			filenames.add(standardized ? name.replace("\\", "/") : name);
		}
		return filenames;
	}
	
	public static <T> T urlToJson(URL url, Type tType) throws JsonSyntaxException, IOException {
		return new Gson().fromJson(IOUtils.toString(url), tType);
	}
}
