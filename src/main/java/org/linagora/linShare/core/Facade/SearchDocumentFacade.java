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
package org.linagora.linShare.core.Facade;

import java.util.List;

import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;

/**
 * This service can retrieve documents for an user with using SearchDocumentCriterion.
 * @author ngapaillard
 *
 */
public interface SearchDocumentFacade {

	
	/**
	 * Retrieve all document for an user (owned or received).
	 * @param user
	 * @return all document for an user.
	 */
	public List<DocumentVo> retrieveDocument(UserVo user);
	
	/**
	 * Retrieve all document corresponding to criterion using matching (contains).
	 * Warning the user matches always exactly. 
	 * Only string values in the vo will use the matching (not the size).
	 * @return all document for criterion.
	 */
	public List<DocumentVo> retrieveDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion);
	

	/**
	 * same function as retrieveDocumentContainsCriterion but we limit search to shared document
	 * @param searchDocumentCriterion
	 * @return
	 */
	public List<ShareDocumentVo> retrieveShareDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion);

	
}

