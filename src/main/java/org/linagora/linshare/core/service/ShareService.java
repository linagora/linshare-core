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

import java.util.List;

import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface ShareService {

	
	
	public void deleteAllShareEntriesWithDocumentEntry(String docEntryUuid, User actor, MailContainer mailContainer);
	

	
	
	
	// to be remove ?
	/**
	 * refresh the attribute share of a document to false if it isn't shared 
	 * @param doc
	 */
	public void refreshShareAttributeOfDoc(Document doc);

	/** Clean all outdated shares. */
	public void cleanOutdatedShares();

	/**
	 * find secured url linked to the given doc
	 * @param doc
	 * @return
	 * @throws BusinessException
	 */
	public List<SecuredUrl> getSecureUrlLinkedToDocument(Document doc) throws BusinessException;

	public List<Share> getSharesLinkedToDocument(Document doc);

	public void notifyUpcomingOutdatedShares();

}
