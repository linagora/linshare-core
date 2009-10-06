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
package org.linagora.linShare.core.repository;

import java.util.List;

import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;

public interface ShareRepository extends AbstractRepository<Share> {
	
	/**
	 * Get a share between an user to other about a document.
	 * @param shareDocument the Document concerned by the share.
	 * @param sender the send of the share.
	 * @param recipient the recipient of the share.
	 * @return true if the share exists, else false.
	 */
	public Share getShare(Document shareDocument,User sender,User recipient);
	
	/**
	 * Get all the shares linked to a document
	 * @param doc
	 * @return
	 */
	public List<Share> getSharesLinkedToDocument(Document doc);

    public List<Share> getOutdatedShares();
	
}
