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

import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;

/**
 * This service ables to retrieve documents for an user with using SearchDocumentCriterion.
 * @author ngapaillard
 *
 */
public interface SearchDocumentService {

			
	/**
	 * Retrieve all document for a login.
	 * @param user
	 * @return all document for a login.
	 */
	public Set<Document> retrieveDocument(String login);
	
	
	/**
	 * 
	 * Retrieve all document (owned or received) for an user.
	 * TODO : only owned for now
	 * @deprecated
	 * @param user
	 * @return all document for an user.
	 */
	public Set<Document> retrieveDocument(User user);
	
	/**
	 * Retrieve all document (owned or received) for an user.
	 * @param user : the user we wish to return docs from
	 * @return a list of DocumentVo (which may contains SharedDocumentVo)
	 */
	public List<DocumentVo> retrieveDocuments(User user);
	
	/**
	 * Retrieve all document corresponding to criterion using matching (contains).
	 * Warning the user matches always exactly. 
	 * Only string values in the vo will use the matching (not the size).
	 * @return all document for criterion
	 * before in the List it can be mixed with DocumentVo and ShareDocumentVo.
	 * now we limit to DocumentVo
	 */
	public List<DocumentVo> retrieveDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion);
	
	/**
	 * same function as retrieveDocumentContainsCriterion
	 * but limit to share only
	 * @param searchDocumentCriterion
	 * @return list of ShareDocumentVo
	 */
	public List<ShareDocumentVo> retrieveShareDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion);
	
}

