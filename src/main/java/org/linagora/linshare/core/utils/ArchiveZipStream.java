/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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

public class ArchiveZipStream extends InputStream {
	
	private static final Logger logger = LoggerFactory.getLogger(ArchiveZipStream.class);
	
	public static final String ARCHIVE_ZIP_DOWNLOAD_NAME = "allFiles.zip";
	
	private final int BUFFERSIZE = 40 *1024;
	private Map<String,InputStream> allFilesTozip; 
	
	private BufferedOutputStream bzfout;
	private ZipOutputStream zout;
	
	private File tempFile;
	private FileInputStream fi;
	

	/**
	 * 
	 * @param allFilesTozip is a map with filename as a key and inputstream associated to this file
	 */
	public ArchiveZipStream(Map<String,InputStream> allFilesTozip) {
			try {
				this.allFilesTozip =  allFilesTozip;
				tempFile = File.createTempFile("linshareZip", null);
				
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
			
			InputStream is = allFilesTozip.get(filename);
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
