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
package org.linagora.linShare.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.dao.document.SearchDocumentDao;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.transformers.impl.DocumentAdapter;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.SearchDocumentService;

public class SearchDocumentServiceImpl implements SearchDocumentService{

	private final SearchDocumentDao searchDocumentDao;
	private final UserRepository<User> userRepository;

	
	private final DocumentAdapter documentAdapter;
	
	
	public SearchDocumentServiceImpl(SearchDocumentDao searchDocumentDao,
			UserRepository<User> userRepository,
			final DocumentAdapter documentAdapter){
		this.searchDocumentDao=searchDocumentDao;
		this.userRepository=userRepository;
		this.documentAdapter = documentAdapter;
	}
	
	public Set<Document> retrieveDocument(User user) {
		return userRepository.findByLogin(user.getLogin()).getDocuments();
	}

	public List<DocumentVo> retrieveDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion) {
		
		List<Document> docs= searchDocumentDao.retrieveUserDocumentWithMatchCriterion(searchDocumentCriterion, searchDocumentDao.getAnyWhere());
		
		//we do not want shares object in this list anymore
		//List<Share> shares = searchDocumentDao.retrieveUserReceivedSharedDocWithMatchCriterion(searchDocumentCriterion, searchDocumentDao.getAnyWhere());
		//return documentAdapter.disassembleList(docs, shares);
		return documentAdapter.disassembleDocList(docs);
	}

	public Set<Document> retrieveDocument(String login) {
		return userRepository.findByLogin(login).getDocuments();
	}

	public List<DocumentVo> retrieveDocuments(User user) {
		
		List<Document> docs= new ArrayList<Document>(user.getDocuments());
		List<Share> shares = new ArrayList<Share>(user.getReceivedShares());
		
		return documentAdapter.disassembleList(docs, shares);
	}


	public List<ShareDocumentVo> retrieveShareDocumentContainsCriterion(
			SearchDocumentCriterion searchDocumentCriterion) {
		List<Share> shares = searchDocumentDao.retrieveUserReceivedSharedDocWithMatchCriterion(searchDocumentCriterion, searchDocumentDao.getAnyWhere());
		return documentAdapter.disassembleShareList(shares);
	}

	
}
