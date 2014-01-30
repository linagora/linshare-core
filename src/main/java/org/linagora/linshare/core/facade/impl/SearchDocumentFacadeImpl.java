/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.transformers.impl.DocumentEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.ShareEntryTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.SearchDocumentFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.SearchDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchDocumentFacadeImpl implements SearchDocumentFacade{
	
	private static final Logger logger = LoggerFactory.getLogger(SearchDocumentFacadeImpl.class);
	

	private final SearchDocumentService searchDocumentService;
	private final DocumentEntryTransformer documentEntryTransformer;
	
	private final ShareEntryTransformer shareEntryTransformer;
	
	private final AccountService accountService;
	private final DocumentEntryService documentEntryService;
	
	
	public SearchDocumentFacadeImpl(SearchDocumentService searchDocumentService, DocumentEntryTransformer documentEntryTransformer, ShareEntryTransformer shareEntryTransformer,
			AccountService accountService, DocumentEntryService documentEntryService) {
		super();
		this.searchDocumentService = searchDocumentService;
		this.documentEntryTransformer = documentEntryTransformer;
		this.shareEntryTransformer = shareEntryTransformer;
		this.accountService = accountService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public List<DocumentVo> retrieveDocument(UserVo userVo) {
		User user = (User) accountService.findByLsUuid(userVo.getLsUuid());
		try {
			List<DocumentEntry> documentEntries = documentEntryService.findAllMyDocumentEntries(user, user);
			return documentEntryTransformer.disassembleList(documentEntries);
		} catch (BusinessException e) {
			logger.error("can't find my document entries");
			logger.debug(e.toString());
			return null;
		}
	}

	@Override
	public List<DocumentVo> retrieveDocumentContainsCriterion(UserVo actorVo, SearchDocumentCriterion searchDocumentCriterion) {
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
		return documentEntryTransformer.disassembleList(searchDocumentService.retrieveDocumentContainsCriterion(actor, searchDocumentCriterion));
	}

	@Override
	public List<ShareDocumentVo> retrieveShareDocumentContainsCriterion(UserVo actorVo, SearchDocumentCriterion searchDocumentCriterion) {
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
		return shareEntryTransformer.disassembleList(this.searchDocumentService.retrieveShareDocumentContainsCriterion(actor, searchDocumentCriterion));
	}
}
