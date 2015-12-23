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
 
public class ZipUtility {
    
	public static boolean compressFolder(File dir, File out) {
		try {
			FileOutputStream fos = new FileOutputStream(dir);
			ByteArrayOutputStream bas = getZippedFolder(out);
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
			ZipOutputStream zstream = new ZipOutputStream(stream);
			compressDirectory(dir, zstream);
			zstream.close();
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
            System.out.println("Compressing: " + filePath);
 
                //
            // Creates a zip entry.
            //
                String name = filePath.substring(dir.getAbsolutePath().length() + 1, 
                        filePath.length());
                ZipEntry zipEntry = new ZipEntry(name);
                zos.putNextEntry(zipEntry);
 
                //
            // Read file content and write to zip output stream.
            //
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
 
                //
            // Close the zip entry and the file input stream.
            //
                zos.closeEntry();
                fis.close();
            }
 
            //
        // Close zip output stream and file output stream. This will
        // complete the compression process.
        //
        zos.close();
    }
}