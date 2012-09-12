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

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

/**
 * Services Encipherment.
 */
public interface EnciphermentService {

	/**
	 * encrypt the content of a document, and change the content in jackrabbit
	 * @param doc
	 * @param user
	 * @param password based encryption
	 * @return
	 * @throws BusinessException
	 */
	public DocumentEntry encryptDocument(Account actor, DocumentEntry documentEntry, Account owner, String password) throws BusinessException;
	public DocumentEntry encryptDocument(Account actor, String documentEntryUuid, Account owner, String password) throws BusinessException;

	/**
	 * decrypt the content of a document, and change the content in jackrabbit
	 * @param doc
	 * @param user
	 * @param password based encryption
	 * @return
	 * @throws BusinessException
	 */
	public DocumentEntry decryptDocument(Account actor, DocumentEntry documentEntry, Account owner, String password) throws BusinessException;
	public DocumentEntry decryptDocument(Account actor, String documentEntryUuid, Account owner, String password) throws BusinessException;
	
}
