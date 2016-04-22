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
package org.linagora.linshare.core.dao;

import java.io.InputStream;

import org.linagora.linshare.core.domain.objects.FileInfo;
/**
 * This interface permits to stock file into a fileSystem. 
 * @author ngapaillard
 *
 */
public interface FileSystemDao {

	
	
	/**
	 * Remove a file by its uuid.
	 * @param uuid the uuid of the file to remove.
	 */
	public void removeFileByUUID(String uuid);
	
	
	/**
	 * Insert a file in the path identifiable by its filename.
	 * @param path the path inside the repository.
	 * @param file the stream content file.
	 * @param fileName the name of the file which permits to identify it.
	 * @param mimeType the mimeType of the file.
	 * @return uuid the uuid of the inserted file.
	 */
	public String insertFile(String path,InputStream file,long size,String fileName,String mimeType);
	
	/**
	 * Return information about file by his uuid
	 * @param uuid
	 * @return FileInfo information about the file.
	 */
	public FileInfo getFileInfoByUUID(String uuid);
	
	/**
	 * Return the file content associated with the specified uuid.
	 * @param uuid
	 * @return the file content associated with the specified uuid.
	 */
	public InputStream getFileContentByUUID(String uuid);

}
