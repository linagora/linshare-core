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

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.SearchDocumentService;

public class SearchDocumentServiceImpl implements SearchDocumentService{

	private final DocumentEntryRepository documentEntryRepository;
	
	private final ShareEntryRepository shareEntryRepository;


	public SearchDocumentServiceImpl(DocumentEntryRepository documentEntryRepository, ShareEntryRepository shareEntryRepository) {
		super();
		this.documentEntryRepository = documentEntryRepository;
		this.shareEntryRepository = shareEntryRepository;
	}


	public List<DocumentEntry> retrieveDocumentContainsCriterion(Account actor, SearchDocumentCriterion searchDocumentCriterion) {
		// TODO : check search permissions
		return documentEntryRepository.retrieveUserDocumentEntriesWithMatchCriterion(searchDocumentCriterion);
	}


	public List<ShareEntry> retrieveShareDocumentContainsCriterion(Account actor, SearchDocumentCriterion searchDocumentCriterion) {
		// TODO : check search permissions
		return shareEntryRepository.retrieveUserShareEntriesWithMatchCriterion(searchDocumentCriterion);
	}
}
