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
package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;

public interface SecuredUrlRepository extends AbstractRepository<SecuredUrl> {

	SecuredUrl find(String shareId, String url) throws LinShareNotSuchElementException;
	List<SecuredUrl> findBySender(User sender);
	List<SecuredUrl> getOutdatedSecuredUrl();
	List<SecuredUrl> getSecureUrlLinkedToDocument(Document doc) throws LinShareNotSuchElementException;
	List<SecuredUrl> getUpcomingOutdatedSecuredUrl(Integer date);
}
