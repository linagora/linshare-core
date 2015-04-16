/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ShareExpiryDateFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareExpiryDateFacadeImpl implements ShareExpiryDateFacade {

	private final DocumentEntryService documentEntryService;
	private final ShareExpiryDateService shareExpiryDateService;
	private final AccountService accountService;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareExpiryDateFacadeImpl.class);
	
	public ShareExpiryDateFacadeImpl(final DocumentEntryService documentEntryService,
			final ShareExpiryDateService shareExpiryDateService,
			final AccountService accountService) {
		super();
		this.documentEntryService = documentEntryService;
		this.shareExpiryDateService = shareExpiryDateService;
		this.accountService = accountService;
	}

	public Calendar computeMinShareExpiryDateOfList(List<DocumentVo> docsVo, UserVo ownerVo) {
		if(ownerVo.getLsUuid() == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}
		
		Account account = accountService.findByLsUuid(ownerVo.getLsUuid());
		if(account == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		
		List<DocumentEntry> docList = new ArrayList<DocumentEntry>();
		for (DocumentVo documentVo : docsVo) {
			try {
				docList.add(documentEntryService.findById(account, documentVo.getIdentifier()));
			} catch (BusinessException e) {
				logger.error("document "  + documentVo.getIdentifier() + " not found : " + e.getMessage());
			}
		}
		return shareExpiryDateService.computeMinShareExpiryDateOfList(docList, account);
	}

	public Calendar computeShareExpiryDate(DocumentVo documentVo, UserVo ownerVo) {
		
		if(ownerVo.getLsUuid() == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}
		
		Account account = accountService.findByLsUuid(ownerVo.getLsUuid());
		if(account == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		
		DocumentEntry doc;
		try {
			doc = documentEntryService.findById(account, documentVo.getIdentifier());
			return shareExpiryDateService.computeShareExpiryDate(doc, account);
		} catch (BusinessException e) {
			logger.error("document "  + documentVo.getIdentifier() + " not found : " + e.getMessage());
		}
		return null;
	}

}
