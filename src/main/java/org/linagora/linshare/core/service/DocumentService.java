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
package org.linagora.linshare.core.service;

import java.io.InputStream;

import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;


/**
 * Service to deal with the documents
 * 
 * @author ncharles
 *
 */
public interface DocumentService {

	
	/**
	 * update file content (put new file in jackrabbit and change uuid of the file in database)
	 * usefull method to replace file content (it is used for example by encrypted file or update action from ihm)
	 * @param currentFileUUID file we want to update the content
	 * @param file inputstream of the content to put in jackrabbit
	 * @param size of the data
	 * @param fileName the name of the file which permits to identify it.
	 * @param mimeType the mimeType of the file.
	 * @param boolean encrypted if the file is encrypted
	 * @param owner : the user who uploads the document
	 * @return
	 * @throws BusinessException
	 */
	public Document updateFileContent(String currentFileUUID, InputStream file, long size,
			String fileName, String mimeType, boolean encrypted, User owner) throws BusinessException;
	
	
	

	/**
	 * Return a file stream by its uuid and actor
	 * if the DocumentVo isn't a share, we enforce that actor is the document owner
	 * @param doc the DocumentVo we want to download, or a SharedDocumentVo 
	 * @param actor the actor of the action
	 * @return the stream
	 * @throws BusinessException 
	 */
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor) throws BusinessException;
	
}
