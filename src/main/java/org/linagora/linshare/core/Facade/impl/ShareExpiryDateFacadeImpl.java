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
package org.linagora.linshare.core.Facade.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.Facade.ShareExpiryDateFacade;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.ShareExpiryDateService;

public class ShareExpiryDateFacadeImpl implements ShareExpiryDateFacade {

	private final DocumentRepository documentRepository;
	private final ShareExpiryDateService shareExpiryDateService;
	private final UserRepository<User> userRepository;
	
	public ShareExpiryDateFacadeImpl(final DocumentRepository documentRepository,
			final ShareExpiryDateService shareExpiryDateService,
			final UserRepository<User> userRepository) {
		super();
		this.documentRepository = documentRepository;
		this.shareExpiryDateService = shareExpiryDateService;
		this.userRepository = userRepository;
	}

	public Calendar computeMinShareExpiryDateOfList(List<DocumentVo> docsVo, UserVo ownerVo) {
		User owner = userRepository.findByMail(ownerVo.getLogin());
		
		List<Document> docList = new ArrayList<Document>();
		for (DocumentVo documentVo : docsVo) {
			docList.add(documentRepository.findById(documentVo.getIdentifier()));
		}
		return shareExpiryDateService.computeMinShareExpiryDateOfList(docList, owner);
	}

	public Calendar computeShareExpiryDate(DocumentVo docVo, UserVo ownerVo) {
		User owner = userRepository.findByMail(ownerVo.getLogin());
		Document document = documentRepository.findById(docVo.getIdentifier());
		return shareExpiryDateService.computeShareExpiryDate(document, owner);
	}

}
