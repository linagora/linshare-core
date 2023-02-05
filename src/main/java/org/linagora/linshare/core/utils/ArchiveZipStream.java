/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

public class ArchiveZipStream extends InputStream {
	
	private static final Logger logger = LoggerFactory.getLogger(ArchiveZipStream.class);
	
	public static final String ARCHIVE_ZIP_DOWNLOAD_NAME = "allFiles.zip";
	
	private final int BUFFERSIZE = 40 *1024;
	private Map<String, ByteSource> allFilesTozip; 
	
	private BufferedOutputStream bzfout;
	private ZipOutputStream zout;
	
	private File tempFile;
	private FileInputStream fi;
	

	/**
	 * 
	 * @param allFilesTozip is a map with filename as a key and inputstream associated to this file
	 */
	public ArchiveZipStream(Map<String, ByteSource> allFilesTozip) {
			try {
				this.allFilesTozip =  allFilesTozip;
				tempFile = File.createTempFile("linshareZip", null);
				tempFile.deleteOnExit();
				zout = new ZipOutputStream(new FileOutputStream(tempFile));
				bzfout= new BufferedOutputStream(zout);
				writeprocess();
				fi = new FileInputStream(tempFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}


	private void releaseAll() {
		if (zout != null) {
			try {
				zout.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		if (bzfout != null) {
			try {
				bzfout.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		if (fi != null) {
			try {
				fi.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		if (tempFile != null && tempFile.exists()) {
			tempFile.delete();
		}
	}

	private void writeprocess() throws IOException {
		byte[] buffer;
		
		//*** all files in zip
		for (String filename : allFilesTozip.keySet()) {
			buffer = new byte[BUFFERSIZE];
			zout.putNextEntry(new ZipEntry(filename));
			
			InputStream is = allFilesTozip.get(filename).openBufferedStream();
			BufferedInputStream bif = new BufferedInputStream(is);
			
			int readed;
			
			while ((readed = bif.read(buffer)) >= 0) {
				bzfout.write(buffer, 0, readed);
			}
			
			bzfout.flush();
			zout.flush();
			zout.closeEntry();
			is.close();
			bif.close();
		}
		
		zout.close();
		bzfout.close();
	}
	
	public File getTempFile(){
		return tempFile;
	}
	

	@Override
	public int read() throws IOException {
		return fi.read();
	}
	@Override
	public int available() throws IOException {
		return fi.available();
	}
	@Override
	public void close() throws IOException {
		releaseAll();
		super.close();
	}
	
	
}
