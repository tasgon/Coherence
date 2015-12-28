package org.tasgo.coherence.ziputils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZipUtility {
	public static final Logger logger = LogManager.getLogger("ZipUtility");
	public static boolean javaPrint = false; //Set to true if System.out should be used instead of logger
	
	public ZipUtility (boolean jP) {
		javaPrint = jP;
	}

	public static boolean compressFolder(File dir, File out) {
		try {
			FileOutputStream fos = new FileOutputStream(out);
			ByteArrayOutputStream bas = getZippedFolder(dir);
			bas.writeTo(fos);
			bas.close(); fos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static ByteArrayOutputStream getZippedFolder(File dir) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(stream);
			compressDirectory(dir, zos);
			zos.close();
			return stream;
		}
		catch (IOException e) {
			LogManager.getLogger().warn(e.getMessage());
			return new ByteArrayOutputStream();
		}
	}

	private static void compressDirectory(File dir, ZipOutputStream zos) throws IOException {
		Collection<File> fileList = FileUtils.listFiles(dir, null, true);
		for (File file : fileList) {
			String filePath = file.getPath();
			if (javaPrint)
				System.out.println("Compressing: " + filePath);
			else
				logger.debug("Compressing: " + filePath);
			
			ZipEntry zipEntry = new ZipEntry(filePath);
			zos.putNextEntry(zipEntry);

			//Read file content and write to zip output stream.
			FileInputStream fis = new FileInputStream(filePath);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}

			//Close the zip entry and the file input stream.
			zos.closeEntry();
			fis.close();
		}
		// Close zip output stream and file output stream. This will complete the compression process.
		zos.close();
	}
}