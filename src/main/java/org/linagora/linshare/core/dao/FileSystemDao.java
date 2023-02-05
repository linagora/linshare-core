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
