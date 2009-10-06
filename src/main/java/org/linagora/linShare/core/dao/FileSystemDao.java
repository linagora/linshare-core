/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.dao;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;

import org.linagora.linShare.core.domain.objects.FileInfo;
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
	 * Return the content of the file.
	 * @param path the path where is stock the file.
	 * @return the content of the file.
	 */
	public InputStream getContentFile(String path);
	
	/**
	 * Return all path in the repository.
	 * @return a list containing all paths.
	 */
	public List<String> getAllPath();
	
	/**
	 * Return all subpath under the specified path in the repository.
	 * @param path the path to start.
	 * @return a list that include all path.
	 */
	public List<String> getAllSubPath(String path);
	
	/**
	 * Return only file subpath under the specified path in the repository.
	 * @param path the path to start.
	 * @param type type of the resource to find.
	 * @return a list that include all file path.
	 */
	public List<FileInfo> getAllFilePathInSubPath(String path);
	
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
	/**
	 * execute severals XpathQuery.
	 * @param statements the statements. 
	 * @return a map which contains for keys all statements and for values all nodes.
	 */
	public Map<String,NodeIterator> executeXPathQuery(List<String> statements);
	
	/**
	 * execute severals SqlQuery.
	 * @param statements the statements.
	 * @return a map which contains for keys all statements and for values all nodes.
	 */
	public Map<String,NodeIterator> executeSqlQuery(List<String> statements);

    /**
     * Change file name.
     * @param uuid file uuid.
     * @param newName new name for the file.
     */
    void renameFile(String uuid, String newName);
}
