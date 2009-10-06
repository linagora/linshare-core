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
package org.linagora.linShare.core.service;

import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

/**
 * Services Encipherment.
 */
public interface EnciphermentService {

	/**
	 * generate challenge for the Encipherment Key
	 * 
	 * @param user
	 * @param password
	 *            material to derive the key
	 * @throws BusinessException 
	 */
	public void generateEnciphermentKey(UserVo user, String password) throws BusinessException;

	
	/**
	 * check challenge for the Encipherment Key
	 * 
	 * @param user
	 * @param password
	 *            to check with the previous one in referential
	 * @return true if two password are the same
	 */
	public boolean checkEnciphermentKey(User user, String password);

	/**
	 * 
	 * @param user
	 * @return true if one key (one password) has been given.
	 */
	public boolean isUserEnciphermentKeyGenerated(User user);

	/**
	 * encrypt the content of a document, and change the content in jackrabbit
	 * @param doc
	 * @param user
	 * @param password based encryption
	 * @return
	 * @throws BusinessException
	 */
	public Document encryptDocument(DocumentVo doc,UserVo user,String password) throws BusinessException;

	/**
	 * decrypt the content of a document, and change the content in jackrabbit
	 * @param doc
	 * @param user
	 * @param password based encryption
	 * @return
	 * @throws BusinessException
	 */
	public Document decryptDocument(DocumentVo doc,UserVo user,String password) throws BusinessException;
	
	/**
	 * check the encrypted status of the document
	 * @param doc
	 * @return
	 */
	public boolean isDocumentEncrypted(DocumentVo doc);
}
