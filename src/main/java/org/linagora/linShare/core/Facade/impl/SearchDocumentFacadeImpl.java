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
package org.linagora.linShare.core.Facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.Facade.SearchDocumentFacade;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.transformers.impl.DocumentTransformer;
import org.linagora.linShare.core.domain.transformers.impl.UserTransformer;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.SearchDocumentService;
import org.linagora.linShare.core.service.UserService;

public class SearchDocumentFacadeImpl implements SearchDocumentFacade{

	private SearchDocumentService searchDocumentService;
	private DocumentService documentService;
	private DocumentTransformer documentTransformer;
	private UserService userService;
	
	public SearchDocumentFacadeImpl(SearchDocumentService searchDocumentService, 
			DocumentTransformer documentTransformer,
			DocumentService documentService,UserService userService){
		this.searchDocumentService=searchDocumentService;
		this.documentTransformer=documentTransformer;
		this.documentService = documentService;
		this.userService = userService;
	}
	
	public List<DocumentVo> retrieveDocument(UserVo userVo) {
		User user = userService.findUnkownUserInDB(userVo.getMail());
		return documentTransformer.disassembleList(new ArrayList<Document>(this.searchDocumentService.retrieveDocument(user)));
	}
/*
	public List<DocumentVo> retrieveDocumentWithCriterion(
			SearchDocumentCriterion searchDocumentCriterion) {
		
		return documentTransformer.disassembleList(this.searchDocumentService.retrieveDocumentWithCriterion(searchDocumentCriterion));
	}

	/*
	public List<DocumentVo> retrieveDocumentBeginWithCriterion(
			SearchDocumentCriterion searchDocumentCriterion) {
		
		return documentTransformer.disassembleList(this.searchDocumentService.retrieveDocumentBeginWithCriterion(searchDocumentCriterion));
	}

	public List<DocumentVo> retrieveDocumentEndWithCriterion(
			SearchDocumentCriterion searchDocumentCriterion) {
		
		return documentTransformer.disassembleList(this.searchDocumentService.retrieveDocumentEndWithCriterion(searchDocumentCriterion));
	}
	*/
	public List<DocumentVo> retrieveDocumentContainsCriterion(SearchDocumentCriterion searchDocumentCriterion) {
		
		return this.searchDocumentService.retrieveDocumentContainsCriterion(searchDocumentCriterion);
	}

	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor) throws BusinessException {
		
		return documentService.retrieveFileStream(doc, actor);
	}

	public List<ShareDocumentVo> retrieveShareDocumentContainsCriterion(
			SearchDocumentCriterion searchDocumentCriterion) {
		return this.searchDocumentService.retrieveShareDocumentContainsCriterion(searchDocumentCriterion);
	}

}
