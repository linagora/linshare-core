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
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.dao.document.SearchDocumentDao;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SearchDocumentService;

public class SearchDocumentServiceImpl implements SearchDocumentService{

	private final SearchDocumentDao searchDocumentDao;
	private final UserRepository<User> userRepository;

	public SearchDocumentServiceImpl(SearchDocumentDao searchDocumentDao, UserRepository<User> userRepository){
		this.searchDocumentDao=searchDocumentDao;
		this.userRepository=userRepository;
	}
	
	public List<DocumentVo> retrieveDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion) {
		
		List<Document> docs= searchDocumentDao.retrieveUserDocumentWithMatchCriterion(searchDocumentCriterion, searchDocumentDao.getAnyWhere());
		
		//we do not want shares object in this list anymore
		//List<Share> shares = searchDocumentDao.retrieveUserReceivedSharedDocWithMatchCriterion(searchDocumentCriterion, searchDocumentDao.getAnyWhere());
		//return documentAdapter.disassembleList(docs, shares);
		 List<DocumentVo> vos = new ArrayList<DocumentVo>();
		return vos;
	}


//	public List<DocumentVo> retrieveDocuments(User user) {
//		
//		List<Document> docs= new ArrayList<Document>(user.getDocuments());
//		List<Share> shares = new ArrayList<Share>(user.getReceivedShares());
//		
//		return documentAdapter.disassembleList(docs, shares);
//	}


	public List<ShareDocumentVo> retrieveShareDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion) {
//		List<Share> shares = searchDocumentDao.retrieveUserReceivedSharedDocWithMatchCriterion(searchDocumentCriterion, searchDocumentDao.getAnyWhere());
		// TODO : To be fix
//		return documentAdapter.disassembleShareList(shares);
		return  new ArrayList<ShareDocumentVo>();
	}

	@Override
	public Set<Document> retrieveDocument(String login) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Document> retrieveDocument(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DocumentVo> retrieveDocuments(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
