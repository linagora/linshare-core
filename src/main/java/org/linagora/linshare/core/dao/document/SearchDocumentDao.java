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
package org.linagora.linshare.core.dao.document;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;

public interface SearchDocumentDao {

	/**
	 * Retrieve all document corresponding to the login.
	 * @param login
	 * @param userRepository
	 * @return all document for a login.
	 */
	//public List<Document> retrieveDocument(String login,UserRepository<User> userRepository);
	

	/**
	 * Retrieve all document corresponding to criterion.
	 * @return all document for criterion.
	 */
//	public List<Document> retrieveDocumentWithCriterion(SearchDocumentCriterion searchDocumentCriterion);
	
	/**
	 * Retrieve all document corresponding to criterion using regexp matching.
	 * use retrieveUserDocumentWithMatchCriterion or retrieveUserReceivedSharedDocWithMatchCriterion instead
	 * @Deprecated
	 * @return all document for criterion.
	 */
	public List<Document> retrieveDocumentWithMatchCriterion(SearchDocumentCriterion searchDocumentCriterion,int match);
	
	/**
	 * Retrieve the list of all the OWNED document corresponding to criterion.
	 * @param searchDocumentCriterion
	 * @param matcher
	 * @return all document for criterion.
	 */
	public List<Document> retrieveUserDocumentWithMatchCriterion(SearchDocumentCriterion searchDocumentCriterion,int match);
	
	
	/**
	 * Retrieve the list of all the RECEIVED document
	 * @param searchDocumentCriterion
	 * @param matcher
	 * @return share list for criterion.
	 */
	public List<Share> retrieveUserReceivedSharedDocWithMatchCriterion(SearchDocumentCriterion searchDocumentCriterion,int match);
	
	
	/**
	 * return the value for beginWith
	 * @return beginWith the identifier for beginWith matching
	 */
	public int getBeginWith();
	
	/**
	 * return the value for endWith
	 * @return endWith the identifier for endWith matching
	 */
	public int getEndWith();
	
	/**
	 * return the value for anywhere
	 * @return anywhere the identifier for anywhere matching
	 */
	public int getAnyWhere();
}
