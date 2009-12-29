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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linShare.core.Facade.ShareExpiryDateFacade;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.service.ShareExpiryDateService;

public class ShareExpiryDateFacadeImpl implements ShareExpiryDateFacade {

	private final DocumentRepository documentRepository;
	private final ShareExpiryDateService shareExpiryDateService;
	
	public ShareExpiryDateFacadeImpl(final DocumentRepository documentRepository,
			final ShareExpiryDateService shareExpiryDateService) {
		super();
		this.documentRepository = documentRepository;
		this.shareExpiryDateService = shareExpiryDateService;
	}

	public Calendar computeMinShareExpiryDateOfList(List<DocumentVo> docsVo) {
		List<Document> docList = new ArrayList<Document>();
		for (DocumentVo documentVo : docsVo) {
			docList.add(documentRepository.findById(documentVo.getIdentifier()));
		}
		return shareExpiryDateService.computeMinShareExpiryDateOfList(docList);
	}

	public Calendar computeShareExpiryDate(DocumentVo docVo) {
		Document document = documentRepository.findById(docVo.getIdentifier());
		return shareExpiryDateService.computeShareExpiryDate(document);
	}

}
